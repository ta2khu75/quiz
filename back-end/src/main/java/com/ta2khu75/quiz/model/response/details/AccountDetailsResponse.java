package com.ta2khu75.quiz.model.response.details;

import com.ta2khu75.quiz.model.response.AccountResponse;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
@Data
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountDetailsResponse extends AccountResponse{
	int blogCount;
	int examCount;
	int followCount;
}
