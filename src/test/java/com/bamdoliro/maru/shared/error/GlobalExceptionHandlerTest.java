package com.bamdoliro.maru.shared.error;

import com.bamdoliro.maru.shared.response.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @Test
    void 제약조건_위반_예외는_409로_응답하고_DB_내부_메시지를_노출하지_않는다() {
        // given
        String rootCauseMessage = "ERROR: new row for relation \"tbl_certificate\" violates check constraint \"tbl_certificate_certificate_check\"";
        RuntimeException rootCause = new RuntimeException(rootCauseMessage);
        DataIntegrityViolationException e = new DataIntegrityViolationException("could not execute statement", rootCause);

        // when
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleDataIntegrityViolationException(e);

        // then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(GlobalErrorProperty.CONFLICT.name(), response.getBody().getCode());
        assertFalse(response.getBody().getMessage().contains("tbl_certificate"));
        assertNull(response.getBody().getError());
    }

    @Test
    void 알수없는_예외는_500으로_응답한다() {
        // given
        Exception e = new NumberFormatException("For input string: \"NaN\"");

        // when
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleException(e);

        // then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(GlobalErrorProperty.INTERNAL_SERVER_ERROR.name(), response.getBody().getCode());
    }
}
