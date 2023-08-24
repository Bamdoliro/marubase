package com.bamdoliro.maru.domain.fair.domain;

import com.bamdoliro.maru.domain.fair.domain.type.FairStatus;
import com.bamdoliro.maru.domain.fair.domain.type.FairType;
import com.bamdoliro.maru.infrastructure.persistence.fair.AttendeeRepository;
import com.bamdoliro.maru.shared.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tbl_fair")
@Entity
public class Fair extends BaseTimeEntity {

    @Column(name = "fair_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private LocalDateTime start;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false, length = 30)
    private String place;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FairType type;

    @Column(nullable = false)
    private LocalDate applicationStartDate;

    @Column(nullable = false)
    private LocalDate applicationEndDate;

    @OneToMany(mappedBy = "fair", fetch = FetchType.LAZY)
    private final List<Attendee> attendeeList = new ArrayList<>();

    @Builder
    public Fair(LocalDateTime start, Integer capacity, String place, FairType type, LocalDate applicationStartDate, LocalDate applicationEndDate) {
        this.start = start;
        this.capacity = capacity;
        this.place = place;
        this.type = type;
        this.applicationStartDate = applicationStartDate == null ? LocalDate.now() : applicationStartDate;
        this.applicationEndDate = applicationEndDate == null ? start.minusDays(3).toLocalDate() : applicationEndDate;
    }

    public Integer getHeadcount(AttendeeRepository attendeeRepository) {
        return attendeeRepository.countByFair(this);
    }

    public FairStatus getStatus(AttendeeRepository attendeeRepository) {
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(start)) {
            return FairStatus.CLOSED;
        } else if (now.toLocalDate().isBefore(applicationStartDate)) {
            return FairStatus.APPLICATION_NOT_STARTED;
        } else if (now.toLocalDate().isAfter(applicationEndDate)) {
            return FairStatus.APPLICATION_ENDED;
        } else if (getHeadcount(attendeeRepository) >= capacity) {
            return FairStatus.APPLICATION_EARLY_CLOSED;
        }

        return FairStatus.APPLICATION_IN_PROGRESS;
    }

    public FairStatus getStatus() {
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(start)) {
            return FairStatus.CLOSED;
        } else if (now.toLocalDate().isBefore(applicationStartDate)) {
            return FairStatus.APPLICATION_NOT_STARTED;
        } else if (now.toLocalDate().isAfter(applicationEndDate)) {
            return FairStatus.APPLICATION_ENDED;
        }

        return FairStatus.APPLICATION_IN_PROGRESS;
    }
}
