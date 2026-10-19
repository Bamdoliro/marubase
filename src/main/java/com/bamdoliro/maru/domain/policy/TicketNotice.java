package com.bamdoliro.maru.domain.policy;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TicketNotice(
        @NotBlank @Size(max = 40) String examinationLocation,
        @NotBlank @Size(max = 40) String announcementLocation,
        @NotBlank @Size(max = 40) String registrationLocation,
        @NotBlank @Size(max = 80) String preliminaryMeetingNotice,
        @NotBlank @Size(max = 80) String registrationMethod,
        @NotBlank @Size(max = 120) String lunchNotice,
        @NotBlank @Size(max = 120) String requiredItems,
        @NotBlank @Size(max = 180) String allowedItems,
        @NotBlank @Size(max = 120) String prohibitedItems
) {
    public static TicketNotice defaults() {
        return new TicketNotice(
                "본교 평가장", "본교 홈페이지", "본교 행정실",
                "예비소집일 별도 안내 예정", "우편, 등기, 직접 제출",
                "점심식사 제공 안함, 개별 도시락 반드시 지참 바랍니다.",
                "수험표, 컴퓨터용 사인펜, 흑색 또는 청색 볼펜 등",
                "컴퓨터용 사인펜, 연필(흑색), 지우개, 샤프연필, 샤프심, 아날로그 시계(블루투스 등 통신기능 및 전자식 화면표시(LCD, LED)가 없는 시계 등",
                "휴대용 전화기, 노트북, 태블릿 PC, 스마트 센서 등의 웨어러블 기기, 통신기기 등"
        );
    }
}
