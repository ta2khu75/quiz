package com.ta2khu75.quiz.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ta2khu75.quiz.entity.QuizType;
import com.ta2khu75.quiz.entity.request.QuizRequest;
import com.ta2khu75.quiz.entity.response.QuizResponse;
import com.ta2khu75.quiz.service.QuizSerivce;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("${app.api-prefix}/quiz")
public class QuizController {
	QuizSerivce service;
	ObjectMapper objectMapper;

	@PostMapping(consumes = "multipart/form-data")
	public ResponseEntity<QuizResponse> postMethodName(@RequestPart("quiz_request") String request, @RequestPart(name="file", required = false) MultipartFile file) throws IOException {
		QuizRequest quizRequest = objectMapper.readValue(request, QuizRequest.class);
		return ResponseEntity.status(HttpStatus.CREATED).body(service.create(quizRequest, file));
	}

	@GetMapping("exam/{id}")
	public ResponseEntity<List<QuizResponse>> getMethodNam(@PathVariable("id") Long id) {
		return ResponseEntity.ok().body(service.readByExamId(id));
	}

	@GetMapping("quiz-type")
	public ResponseEntity<QuizType[]> getMethodName() {
		return ResponseEntity.ok(QuizType.values());
	}

	@GetMapping("{id}")
	public ResponseEntity<QuizResponse> getMethodName(@PathVariable("id") Long id) {
		return ResponseEntity.status(HttpStatus.OK).body(service.read(id));
	}

	@PutMapping("{id}")
	public ResponseEntity<QuizResponse> putMethodName(@PathVariable("id") Long id,
			@RequestPart("quiz_request") String request, @RequestPart(name="file", required = false) MultipartFile file) throws IOException {
		QuizRequest quizRequest = objectMapper.readValue(request, QuizRequest.class);
		return ResponseEntity.status(HttpStatus.OK).body(service.update(id, quizRequest, file));
	}

	@DeleteMapping("{id}")
	public ResponseEntity<Void> deleteMethodName(@PathVariable("id") Long id) {
		service.delete(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
	@PatchMapping("delete-image/{id}")
	public ResponseEntity<Void> deleteImage(@PathVariable("id") Long id) {
		service.deleteFile(id);
		return ResponseEntity.noContent().build();
	}

}
