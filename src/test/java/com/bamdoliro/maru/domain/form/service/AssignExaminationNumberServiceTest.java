package com.bamdoliro.maru.domain.form.service;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.exception.ExaminationNumberExhaustedException;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AssignExaminationNumberServiceTest {

    @InjectMocks
    private AssignExaminationNumberService assignExaminationNumberService;

    @Mock
    private FormRepository formRepository;

    @Mock
    private ExaminationNumberLock examinationNumberLock;

    @Test
    void 수험번호를_부여한다() {
        // given
        Form form = FormFixture.createForm(FormType.REGULAR);
        given(formRepository.findAllExaminationNumber()).willReturn(List.of(1001L, 1002L, 1003L, 1004L, 1005L));

        // when
        assignExaminationNumberService.execute(form);

        // then
        assertEquals(1006L, form.getExaminationNumber());
        verify(formRepository).findAllExaminationNumber();
    }

    @Test
    void 이전에_수험번호가_없다면_초기값을_부여한다() {
        // given
        Form form = FormFixture.createForm(FormType.TEEN_HOUSEHOLDER);
        given(formRepository.findAllExaminationNumber()).willReturn(List.of());

        // when
        assignExaminationNumberService.execute(form);

        // then
        assertEquals(3001L, form.getExaminationNumber());
        verify(formRepository).findAllExaminationNumber();
    }

    @Test
    void 락을_잡은_뒤에_수험번호를_조회한다() {
        // given
        Form form = FormFixture.createForm(FormType.REGULAR);
        given(formRepository.findAllExaminationNumber()).willReturn(List.of());

        // when
        assignExaminationNumberService.execute(form);

        // then
        InOrder inOrder = inOrder(examinationNumberLock, formRepository);
        inOrder.verify(examinationNumberLock).lock();
        inOrder.verify(formRepository).findAllExaminationNumber();
    }

    @ParameterizedTest
    @CsvSource({
            "REGULAR, 1001",
            "MEISTER_TALENT, 2001",
            "ONE_PARENT, 3001",
            "MULTI_CHILDREN, 3001",
            "NATIONAL_VETERANS_EDUCATION, 4001",
            "SPECIAL_ADMISSION, 4001"
    })
    void 전형별_첫_수험번호를_부여한다(FormType type, Long expected) {
        // given
        Form form = FormFixture.createForm(type);
        given(formRepository.findAllExaminationNumber()).willReturn(List.of());

        // when
        assignExaminationNumberService.execute(form);

        // then
        assertEquals(expected, form.getExaminationNumber());
    }

    @Test
    void 다른_전형의_수험번호는_계산에_영향을_주지_않는다() {
        // given
        Form form = FormFixture.createForm(FormType.MEISTER_TALENT);
        given(formRepository.findAllExaminationNumber()).willReturn(List.of(1001L, 1999L, 2001L, 2002L, 3001L, 4001L));

        // when
        assignExaminationNumberService.execute(form);

        // then
        assertEquals(2003L, form.getExaminationNumber());
    }

    @Test
    void 구간의_마지막_번호까지_부여한다() {
        // given
        Form form = FormFixture.createForm(FormType.REGULAR);
        given(formRepository.findAllExaminationNumber()).willReturn(List.of(1997L, 1998L));

        // when
        assignExaminationNumberService.execute(form);

        // then
        assertEquals(1999L, form.getExaminationNumber());
    }

    @Test
    void 구간의_번호를_모두_사용하면_다음_전형_번호를_부여하지_않고_에러가_발생한다() {
        // given
        Form form = FormFixture.createForm(FormType.REGULAR);
        given(formRepository.findAllExaminationNumber()).willReturn(List.of(1998L, 1999L, 2001L));

        // when and then
        assertThrows(ExaminationNumberExhaustedException.class, () -> assignExaminationNumberService.execute(form));
        assertNull(form.getExaminationNumber());
    }

    @Test
    void 다음_전형의_시작_번호는_이전_전형의_계산에_포함하지_않는다() {
        // given
        Form regularForm = FormFixture.createForm(FormType.REGULAR);
        Form meisterForm = FormFixture.createForm(FormType.MEISTER_TALENT);
        given(formRepository.findAllExaminationNumber()).willReturn(List.of(1001L, 2000L));

        // when
        assignExaminationNumberService.execute(regularForm);
        assignExaminationNumberService.execute(meisterForm);

        // then
        assertEquals(1002L, regularForm.getExaminationNumber());
        assertEquals(2001L, meisterForm.getExaminationNumber());
    }

    @Test
    void 수험번호_구간이_정의되지_않은_전형이면_에러가_발생한다() {
        assertThrows(IllegalStateException.class,
                () -> AssignExaminationNumberService.getStartNumber(FormType.Category.SPECIAL));
        assertThrows(IllegalStateException.class,
                () -> AssignExaminationNumberService.getStartNumber(FormType.Category.EQUAL_OPPORTUNITY));
    }

    @Test
    void 동시에_수험번호를_부여해도_중복되지_않는다() throws Exception {
        // given
        int applicantCount = 50;
        List<Long> savedNumbers = Collections.synchronizedList(new ArrayList<>());
        given(formRepository.findAllExaminationNumber()).willAnswer(invocation -> {
            synchronized (savedNumbers) {
                return new ArrayList<>(savedNumbers);
            }
        });
        TransactionScopedLock lock = new TransactionScopedLock();
        AssignExaminationNumberService service = new AssignExaminationNumberService(formRepository, lock);

        ExecutorService executor = Executors.newFixedThreadPool(applicantCount);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<?>> futures = new ArrayList<>();

        // when
        for (int i = 0; i < applicantCount; i++) {
            futures.add(executor.submit(() -> {
                start.await();
                Form form = FormFixture.createForm(FormType.REGULAR);
                service.execute(form);
                // 저장 후 커밋 시점에 락이 풀리는 트랜잭션 동작을 흉내 낸다.
                savedNumbers.add(form.getExaminationNumber());
                lock.commit();
                return null;
            }));
        }
        start.countDown();
        for (Future<?> future : futures) {
            future.get(10, TimeUnit.SECONDS);
        }
        executor.shutdown();

        // then
        Set<Long> uniqueNumbers = new HashSet<>(savedNumbers);
        assertEquals(applicantCount, savedNumbers.size());
        assertEquals(applicantCount, uniqueNumbers.size());
        assertEquals(
                LongStream.rangeClosed(1001L, 1000L + applicantCount).boxed().toList(),
                savedNumbers.stream().sorted().toList()
        );
    }

    private static class TransactionScopedLock implements ExaminationNumberLock {

        private final ReentrantLock lock = new ReentrantLock();

        @Override
        public void lock() {
            lock.lock();
        }

        void commit() {
            lock.unlock();
        }
    }
}
