package com.bamdoliro.maru.application.fair;

import com.bamdoliro.maru.domain.fair.domain.Attendee;
import com.bamdoliro.maru.domain.fair.domain.Fair;
import com.bamdoliro.maru.infrastructure.persistence.fair.AttendeeRepository;
import com.bamdoliro.maru.infrastructure.persistence.fair.FairRepository;
import com.bamdoliro.maru.shared.fixture.FairFixture;
import com.bamdoliro.maru.shared.util.SaveFileUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@Disabled
@ActiveProfiles("test")
@SpringBootTest
class ExportAttendeeListUseCaseTest {

    @Autowired
    private ExportAttendeeListUseCase exportAttendeeListUseCase;

    @Autowired
    private FairRepository fairRepository;

    @Autowired
    private AttendeeRepository attendeeRepository;

    @Test
    void 신청자_명단을_엑셀로_다운로드한다() throws IOException {
        Fair fair = fairRepository.save(FairFixture.createFair());
        List<Attendee> attendeeList = FairFixture.createAttendeeList(fair);
        attendeeRepository.saveAll(attendeeList);
        Resource resource = exportAttendeeListUseCase.execute(fair.getId());

        SaveFileUtil.execute(resource, SaveFileUtil.XLSX);
    }
}