package com.bamdoliro.maru.infrastructure.aop.log;

import com.bamdoliro.maru.domain.schedule.domain.AdmissionSchedule;
import com.bamdoliro.maru.domain.schedule.domain.AdmissionScheduleChangeLog;
import com.bamdoliro.maru.domain.schedule.domain.type.ScheduleChangeType;
import com.bamdoliro.maru.infrastructure.persistence.schedule.AdmissionScheduleChangeLogRepository;
import com.bamdoliro.maru.presentation.schedule.dto.response.ScheduleResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RequiredArgsConstructor
@Component
public class AdmissionScheduleAuditService {

    private final AdmissionScheduleChangeLogRepository changeLogRepository;
    private final ObjectMapper objectMapper;

    public String snapshot(AdmissionSchedule schedule) {
        try {
            return objectMapper.writeValueAsString(new ScheduleResponse(schedule));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("입학 일정 변경 이력을 생성할 수 없습니다.", e);
        }
    }

    public void log(
            AdmissionSchedule schedule,
            Long userId,
            ScheduleChangeType changeType,
            String beforeSchedule,
            String afterSchedule
    ) {
        HttpServletRequest request = getCurrentRequest();
        changeLogRepository.save(
                AdmissionScheduleChangeLog.builder()
                        .schedule(schedule)
                        .userId(userId)
                        .clientIp(getClientIp(request))
                        .userAgent(getUserAgent(request))
                        .changeType(changeType)
                        .beforeSchedule(beforeSchedule)
                        .afterSchedule(afterSchedule)
                        .build()
        );
    }

    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        return attributes.getRequest();
    }

    private String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return "Unknown";
        }
        String clientIp = request.getHeader("x-forwarded-for");
        if (clientIp == null || clientIp.isBlank()) {
            return request.getRemoteAddr();
        }
        return clientIp.split(",")[0].trim();
    }

    private String getUserAgent(HttpServletRequest request) {
        if (request == null || request.getHeader("User-Agent") == null) {
            return "Unknown";
        }
        return request.getHeader("User-Agent");
    }
}
