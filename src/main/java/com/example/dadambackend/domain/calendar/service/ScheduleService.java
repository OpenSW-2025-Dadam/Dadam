package com.example.dadambackend.domain.calendar.service;

import com.example.dadambackend.domain.calendar.dto.request.ScheduleRequest;
import com.example.dadambackend.domain.calendar.dto.response.ScheduleResponse;
import com.example.dadambackend.domain.calendar.dto.response.ScheduleUpdateResponse;
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

    public static final int UPCOMING_DAYS = 30;

    private final ScheduleRepository scheduleRepository;

    /**
     * 일정 등록
     */
    @Transactional
    public ScheduleResponse createSchedule(ScheduleRequest request) {

        // 필수값 검증 (프론트에서 막아주지만, 백엔드에서도 한 번 더)
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "약속 이름(title)은 필수입니다.");
        }
        if (request.getDate() == null) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "약속 날짜(date)는 필수입니다.");
        }

        Schedule schedule = Schedule.create(request);
        Schedule saved = scheduleRepository.save(schedule);

        return ScheduleResponse.from(saved, isUpcoming(saved.getDate()));
    }

    /**
     * 일정 상세 조회 (수정용)
     */
    public ScheduleUpdateResponse getScheduleForUpdate(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.GAME_NOT_FOUND, "수정하려는 일정을 찾을 수 없습니다."));

        return ScheduleUpdateResponse.from(schedule);
    }

    /**
     * 일정 수정
     */
    @Transactional
    public ScheduleResponse updateSchedule(Long scheduleId, ScheduleRequest request) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.GAME_NOT_FOUND, "수정하려는 일정을 찾을 수 없습니다."));

        schedule.update(request);

        return ScheduleResponse.from(schedule, isUpcoming(schedule.getDate()));
    }

    /**
     * 다가오는 일정 목록 조회 (오늘부터 30일)
     */
    public List<ScheduleResponse> getUpcomingSchedules() {
        LocalDate today = LocalDate.now();
        LocalDate end = today.plusDays(UPCOMING_DAYS);

        List<Schedule> schedules =
                scheduleRepository.findByDateBetweenOrderByDateAsc(today, end);

        return schedules.stream()
                .map(s -> ScheduleResponse.from(s, true))
                .collect(Collectors.toList());
    }

    /**
     * ✅ NEW
     * 특정 날짜의 일정 목록 조회
     *
     * - date가 null 이면 오늘(LocalDate.now()) 기준으로 조회
     * - 리턴은 ScheduleResponse 리스트
     */
    public List<ScheduleResponse> getSchedulesByDate(LocalDate date) {
        LocalDate targetDate = (date != null) ? date : LocalDate.now();

        List<Schedule> schedules = scheduleRepository.findByDate(targetDate);

        return schedules.stream()
                .map(s -> ScheduleResponse.from(s, isUpcoming(s.getDate())))
                .collect(Collectors.toList());
    }

    /**
     * 일정 취소 (삭제)
     */
    @Transactional
    public void cancelSchedule(Long scheduleId) {
        if (!scheduleRepository.existsById(scheduleId)) {
            throw new BusinessException(ErrorCode.GAME_NOT_FOUND, "취소하려는 일정을 찾을 수 없습니다.");
        }
        scheduleRepository.deleteById(scheduleId);
    }

    private boolean isUpcoming(LocalDate date) {
        long diff = ChronoUnit.DAYS.between(LocalDate.now(), date);
        return diff >= 0 && diff <= UPCOMING_DAYS;
    }
}
