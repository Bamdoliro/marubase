package com.bamdoliro.maru.infrastructure.xlsx;

import lombok.experimental.UtilityClass;

import java.util.Map;
import java.util.Optional;

/**
 * 학교 소재지나 도로명 주소의 첫 단어를 지원자등록양식의 시·도 명칭으로 변환한다.
 */
@UtilityClass
public class SidoNormalizer {

    private static final Map<String, String> SIDO = Map.ofEntries(
            Map.entry("서울", "서울특별시"),
            Map.entry("서울특별시", "서울특별시"),
            Map.entry("부산", "부산광역시"),
            Map.entry("부산광역시", "부산광역시"),
            Map.entry("대구", "대구광역시"),
            Map.entry("대구광역시", "대구광역시"),
            Map.entry("인천", "인천광역시"),
            Map.entry("인천광역시", "인천광역시"),
            Map.entry("대전", "대전광역시"),
            Map.entry("대전광역시", "대전광역시"),
            Map.entry("울산", "울산광역시"),
            Map.entry("울산광역시", "울산광역시"),
            Map.entry("세종", "세종특별자치시"),
            Map.entry("세종특별자치시", "세종특별자치시"),
            Map.entry("경기", "경기도"),
            Map.entry("경기도", "경기도"),
            Map.entry("강원", "강원특별자치도"),
            Map.entry("강원도", "강원특별자치도"),
            Map.entry("강원특별자치도", "강원특별자치도"),
            Map.entry("충북", "충청북도"),
            Map.entry("충청북도", "충청북도"),
            Map.entry("충남", "충청남도"),
            Map.entry("충청남도", "충청남도"),
            Map.entry("전북", "전북특별자치도"),
            Map.entry("전라북도", "전북특별자치도"),
            Map.entry("전북특별자치도", "전북특별자치도"),
            Map.entry("경북", "경상북도"),
            Map.entry("경상북도", "경상북도"),
            Map.entry("경남", "경상남도"),
            Map.entry("경상남도", "경상남도"),
            Map.entry("제주", "제주특별자치도"),
            Map.entry("제주도", "제주특별자치도"),
            Map.entry("제주특별자치도", "제주특별자치도"),
            Map.entry("광주", "전남광주통합특별시"),
            Map.entry("광주광역시", "전남광주통합특별시"),
            Map.entry("전남", "전남광주통합특별시"),
            Map.entry("전라남도", "전남광주통합특별시"),
            Map.entry("전남광주통합특별시", "전남광주통합특별시")
    );

    public static Optional<String> normalize(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }

        String firstToken = value.trim().split("\\s+")[0];
        return Optional.ofNullable(SIDO.get(firstToken));
    }
}
