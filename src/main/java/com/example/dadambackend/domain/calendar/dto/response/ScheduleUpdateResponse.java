package com.example.dadambackend.domain.calendar.dto.response;

import com.example.dadambackend.domain.calendar.model.Schedule;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class ScheduleUpdateResponse {

    private Long id;

    private String title;
    private LocalDate date;
    private String time;
    private String place;
    private String memo;
    private String type;
    private boolean remind;

    public static ScheduleUpdateResponse from(Schedule schedule) {
        return ScheduleUpdateResponse.builder()
                .id(schedule.getId())
                .title(schedule.getTitle())
                .date(schedule.getDate())
                .time(schedule.getTime())
                .place(schedule.getPlace())
                .memo(schedule.getMemo())
                .type(schedule.getType())
                .remind(schedule.isRemind())
                .build();
    }
}
