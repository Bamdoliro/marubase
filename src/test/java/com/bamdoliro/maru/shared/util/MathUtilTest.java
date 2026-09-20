package com.bamdoliro.maru.shared.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MathUtilTest {

    @Test
    void 값이_null이면_null을_반환한다() {
        assertNull(MathUtil.roundTo(null, 3));
    }

    @Test
    void 지정한_자리수로_반올림한다() {
        assertEquals(1.235, MathUtil.roundTo(1.2345, 3));
    }

    @Test
    void NaN이면_에러가_발생한다() {
        assertThrows(IllegalArgumentException.class, () -> MathUtil.roundTo(Double.NaN, 3));
    }

    @Test
    void Infinite면_에러가_발생한다() {
        assertThrows(IllegalArgumentException.class, () -> MathUtil.roundTo(Double.POSITIVE_INFINITY, 3));
        assertThrows(IllegalArgumentException.class, () -> MathUtil.roundTo(Double.NEGATIVE_INFINITY, 3));
    }
}
