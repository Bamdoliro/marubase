package com.bamdoliro.maru.domain.policy;

import com.bamdoliro.maru.shared.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tbl_admission_policy_change_log")
@Entity
public class AdmissionPolicyChangeLog extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long scheduleId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String beforePolicy;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String afterPolicy;

    public AdmissionPolicyChangeLog(Long scheduleId, Long userId, String beforePolicy, String afterPolicy) {
        this.scheduleId = scheduleId;
        this.userId = userId;
        this.beforePolicy = beforePolicy;
        this.afterPolicy = afterPolicy;
    }
}
