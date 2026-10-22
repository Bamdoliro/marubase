package com.bamdoliro.maru.infrastructure.xlsx;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SidoNormalizerTest {

    static final Set<String> SIDO_LIST = Set.of(
            "강원특별자치도", "경기도", "경상남도", "경상북도", "대구광역시", "대전광역시",
            "부산광역시", "서울특별시", "세종특별자치시", "울산광역시", "인천광역시",
            "전남광주통합특별시", "전북특별자치도", "제주특별자치도", "충청남도", "충청북도",
            "검정고시", "기타"
    );

    @ParameterizedTest
    @CsvSource({
            "서울, 서울특별시", "서울특별시, 서울특별시",
            "부산, 부산광역시", "부산광역시, 부산광역시",
            "대구, 대구광역시", "대구광역시, 대구광역시",
            "인천, 인천광역시", "인천광역시, 인천광역시",
            "대전, 대전광역시", "대전광역시, 대전광역시",
            "울산, 울산광역시", "울산광역시, 울산광역시",
            "세종, 세종특별자치시", "세종특별자치시, 세종특별자치시",
            "경기, 경기도", "경기도, 경기도",
            "강원, 강원특별자치도", "강원도, 강원특별자치도", "강원특별자치도, 강원특별자치도",
            "충북, 충청북도", "충청북도, 충청북도",
            "충남, 충청남도", "충청남도, 충청남도",
            "전북, 전북특별자치도", "전라북도, 전북특별자치도", "전북특별자치도, 전북특별자치도",
            "경북, 경상북도", "경상북도, 경상북도",
            "경남, 경상남도", "경상남도, 경상남도",
            "제주, 제주특별자치도", "제주도, 제주특별자치도", "제주특별자치도, 제주특별자치도",
            "광주, 전남광주통합특별시", "광주광역시, 전남광주통합특별시",
            "전남, 전남광주통합특별시", "전라남도, 전남광주통합특별시", "전남광주통합특별시, 전남광주통합특별시"
    })
    void 시도명을_양식의_시도_목록으로_변환한다(String input, String expected) {
        String actual = SidoNormalizer.normalize(input).orElseThrow();

        assertEquals(expected, actual);
        assertTrue(SIDO_LIST.contains(actual));
    }

    @Test
    void 주소의_첫_토큰으로_시도를_판별한다() {
        assertEquals("부산광역시", SidoNormalizer.normalize("부산 강서구 가락대로 1393").orElseThrow());
        assertEquals("경상남도", SidoNormalizer.normalize("  경남 김해시 김해대로 2401").orElseThrow());
        assertEquals("세종특별자치시", SidoNormalizer.normalize("세종특별자치시 한누리대로 2130").orElseThrow());
        assertEquals("전북특별자치도", SidoNormalizer.normalize("전북특별자치도 전주시 완산구 효자로 225").orElseThrow());
        assertEquals("전남광주통합특별시", SidoNormalizer.normalize("광주 서구 내방로 111").orElseThrow());
        assertTrue(SidoNormalizer.normalize("광주시 오포읍 1").isEmpty());
    }

    @Test
    void 알_수_없는_값이면_빈_값을_반환한다() {
        assertTrue(SidoNormalizer.normalize("Seoul").isEmpty());
        assertTrue(SidoNormalizer.normalize("").isEmpty());
        assertTrue(SidoNormalizer.normalize(null).isEmpty());
    }
}
