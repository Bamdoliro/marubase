package com.bamdoliro.maru.domain.policy;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tbl_admission_policy")
@Entity
public class AdmissionPolicy {
    // 일정의 학년도가 수정되어도 같은 정책을 참조한다.
    @Id
    private Long scheduleId;

    @Version
    private Long version;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String selectionJson;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String ticketJson;

    @Column(nullable = false)
    private boolean selectionLocked;

    public AdmissionPolicy(Long scheduleId, String selectionJson, String ticketJson) {
        this.scheduleId = scheduleId;
        this.selectionJson = selectionJson;
        this.ticketJson = ticketJson;
    }

    public void update(String selectionJson, String ticketJson) {
        this.selectionJson = selectionJson;
        this.ticketJson = ticketJson;
    }

    public void lockSelection() {
        selectionLocked = true;
    }
}
