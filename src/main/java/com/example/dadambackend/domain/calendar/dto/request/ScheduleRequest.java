// com/example/dadambackend/domain/calendar/dto/ScheduleRequest.java
package com.example.dadambackend.domain.calendar.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ScheduleRequest {
    private String appointmentName;
    private LocalDate appointmentDate;
    private int iconType; // 1~6
}
