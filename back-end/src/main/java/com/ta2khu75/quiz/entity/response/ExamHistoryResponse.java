package com.ta2khu75.quiz.entity.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ta2khu75.quiz.entity.response.details.ExamDetailsResponse;

public record ExamHistoryResponse(Long id, float point, int correctCount, ExamDetailsResponse exam, AccountResponse account,
		@JsonProperty("end_time") LocalDateTime endTime, @JsonProperty("created_date") LocalDateTime createdDate, @JsonProperty("last_modified_date") LocalDateTime lastModifiedDate) {
}
