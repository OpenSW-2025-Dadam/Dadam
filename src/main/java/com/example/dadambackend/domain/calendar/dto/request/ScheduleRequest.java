package com.example.dadambackend.domain.calendar.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ScheduleRequest {

    // ✅ 필수
    private String title;      // 약속 이름
    private LocalDate date;    // 날짜 (yyyy-MM-dd)

    // ✅ 선택 (비어 있어도 / null이어도 됨)
    private String time;       // "18:30" 같은 문자열
    private String place;      // 장소
    private String memo;       // 메모
    private String type;       // "trip" / "dinner" 등 (없으면 null)
    private Boolean remind;    // 알림 여부 (null이면 false로 처리)
}
