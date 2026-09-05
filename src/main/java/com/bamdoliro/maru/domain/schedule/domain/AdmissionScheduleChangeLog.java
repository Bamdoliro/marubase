package com.bamdoliro.maru.domain.schedule.domain;

import com.bamdoliro.maru.domain.schedule.domain.type.ScheduleChangeType;
import com.bamdoliro.maru.shared.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tbl_admission_schedule_change_log")
@Entity
public class AdmissionScheduleChangeLog extends BaseTimeEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private AdmissionSchedule schedule;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String clientIp;

    @Column(nullable = false)
    private String userAgent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ScheduleChangeType changeType;

    @Column(columnDefinition = "TEXT")
    private String beforeSchedule;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String afterSchedule;

    @Builder
    public AdmissionScheduleChangeLog(
            AdmissionSchedule schedule,
            Long userId,
            String clientIp,
            String userAgent,
            ScheduleChangeType changeType,
            String beforeSchedule,
            String afterSchedule
    ) {
        this.schedule = schedule;
        this.userId = userId;
        this.clientIp = clientIp;
        this.userAgent = userAgent;
        this.changeType = changeType;
        this.beforeSchedule = beforeSchedule;
        this.afterSchedule = afterSchedule;
    }
}
