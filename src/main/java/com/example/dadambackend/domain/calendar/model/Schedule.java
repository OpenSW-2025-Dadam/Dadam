// com/example/dadambackend/domain/calendar/model/Schedule.java
package com.example.dadambackend.domain.calendar.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Schedule {

    // 아이콘 종류: 1부터 6까지
    public static final int MAX_ICON_TYPE = 6;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String appointmentName; // 약속 이름

    @Column(nullable = false)
    private LocalDate appointmentDate; // 약속 날짜

    // 1~6 사이의 아이콘 번호
    @Column(nullable = false)
    private int iconType;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // 생성자
    public Schedule(String appointmentName, LocalDate appointmentDate, int iconType) {
        // 아이콘 타입 유효성 검사 (1~6)
        if (iconType < 1 || iconType > MAX_ICON_TYPE) {
            throw new IllegalArgumentException("아이콘 타입은 1부터 " + MAX_ICON_TYPE + " 사이여야 합니다.");
        }

        this.appointmentName = appointmentName;
        this.appointmentDate = appointmentDate;
        this.iconType = iconType;
        this.createdAt = LocalDateTime.now();
    }
}
