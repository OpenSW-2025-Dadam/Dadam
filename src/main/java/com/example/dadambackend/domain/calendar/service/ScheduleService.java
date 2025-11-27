// com/example/dadambackend/domain/calendar/service/ScheduleService.java
package com.example.dadambackend.domain.calendar.service;

import com.example.dadambackend.domain.calendar.dto.ScheduleRequest;
import com.example.dadambackend.domain.calendar.dto.ScheduleResponse;
import com.example.dadambackend.domain.calendar.model.Schedule;
import com.example.dadambackend.domain.calendar.repository.ScheduleRepository;
import com.example.dadambackend.global.exception.BusinessException;
import com.example.dadambackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {

    // 다가오는 일정 기준 (30일)
    public static final int UPCOMING_DAYS = 30;

    private final ScheduleRepository scheduleRepository;

    /**
     * 일정 등록 (약속 이름, 날짜, 아이콘)
     */
    @Transactional
    public ScheduleResponse createSchedule(ScheduleRequest request) {
        // 엔티티 생성 시 아이콘 유효성 검사는 Schedule 엔티티에서 처리됩니다.
        Schedule schedule = new Schedule(
                request.getAppointmentName(),
                request.getAppointmentDate(),
                request.getIconType()
        );
        Schedule savedSchedule = scheduleRepository.save(schedule);

        // 등록 직후에는 다가오는 일정으로 간주하지 않음 (API 용도를 위해)
        return ScheduleResponse.from(savedSchedule, isUpcoming(savedSchedule.getAppointmentDate()));
    }

    /**
     * 다가오는 일정 목록 조회 (오늘부터 30일 이내)
     */
    public List<ScheduleResponse> getUpcomingSchedules() {
        LocalDate today = LocalDate.now();
        LocalDate maxDate = today.plusDays(UPCOMING_DAYS);

        List<Schedule> schedules = scheduleRepository.findByAppointmentDateBetweenOrderByAppointmentDateAsc(today, maxDate);

        return schedules.stream()
                // 다가오는 일정(isUpcoming)은 항상 true로 설정
                .map(schedule -> ScheduleResponse.from(schedule, true))
                .collect(Collectors.toList());
    }

    /**
     * 일정 취소 (삭제)
     */
    @Transactional
    public void cancelSchedule(Long scheduleId) {
        // 1. 일정 존재 여부 확인
        if (!scheduleRepository.existsById(scheduleId)) {
            // GAME_NOT_FOUND 재사용 (일정=게임/이벤트)
            throw new BusinessException(ErrorCode.GAME_NOT_FOUND, "취소하려는 일정을 찾을 수 없습니다.");
        }

        // 2. 일정 삭제
        scheduleRepository.deleteById(scheduleId);
    }

    /**
     * Helper: 다가오는 일정 여부 판단 (30일 이하)
     */
    private boolean isUpcoming(LocalDate date) {
        long daysUntil = ChronoUnit.DAYS.between(LocalDate.now(), date);
        return daysUntil >= 0 && daysUntil <= UPCOMING_DAYS;
    }
}
