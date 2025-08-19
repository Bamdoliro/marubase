package com.bamdoliro.maru.domain.scheduler;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tbl_schudule")
@Entity
public class FirstPassSchedule {

    @Id
    private Long id = 1L;

    private LocalDateTime scheduledTime;

    public FirstPassSchedule(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public void update(LocalDateTime newSchedule) {
        this.scheduledTime = newSchedule;
    }

}
