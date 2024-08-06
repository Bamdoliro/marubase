package com.bamdoliro.maru.domain.form.domain;

import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FormTest {

    @Test
    void isReceived() {
        Form form = FormFixture.createForm(FormType.REGULAR);
        assertFalse(form.isReceived());

        form.submit();
        assertFalse(form.isReceived());

        form.approve();
        assertFalse(form.isReceived());

        form.reject();
        assertFalse(form.isReceived());

        form.receive();
        assertTrue(form.isReceived());

        form.firstPass();
        assertTrue(form.isReceived());

        form.firstFail();
        assertTrue(form.isReceived());

        form.pass();
        assertTrue(form.isReceived());

        form.fail();
        assertTrue(form.isReceived());

        form.noShow();
        assertTrue(form.isReceived());
    }

    @Test
    void isFirstPassed() {
        Form form = FormFixture.createForm(FormType.REGULAR);
        assertNull(form.isFirstPassed());

        form.submit();
        assertNull(form.isFirstPassed());

        form.approve();
        assertNull(form.isFirstPassed());

        form.reject();
        assertNull(form.isFirstPassed());

        form.receive();
        assertNull(form.isFirstPassed());

        form.firstPass();
        assertTrue(form.isFirstPassed());

        form.firstFail();
        assertFalse(form.isFirstPassed());

        form.pass();
        assertTrue(form.isFirstPassed());

        form.fail();
        assertTrue(form.isFirstPassed());

        form.noShow();
        assertTrue(form.isFirstPassed());
    }

    @Test
    void isPassed() {
        Form form = FormFixture.createForm(FormType.REGULAR);
        assertNull(form.isPassed());

        form.submit();
        assertNull(form.isPassed());

        form.approve();
        assertNull(form.isPassed());

        form.reject();
        assertNull(form.isPassed());

        form.receive();
        assertNull(form.isPassed());

        form.firstPass();
        assertNull(form.isPassed());

        form.firstFail();
        assertNull(form.isPassed());

        form.pass();
        assertTrue(form.isPassed());

        form.fail();
        assertFalse(form.isPassed());

        form.noShow();
        assertFalse(form.isPassed());
    }
}