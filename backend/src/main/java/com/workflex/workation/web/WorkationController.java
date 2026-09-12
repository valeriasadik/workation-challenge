package com.workflex.workation.web;

import com.workflex.workation.service.WorkationDto;
import com.workflex.workation.service.WorkationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/workflex")
@RequiredArgsConstructor
public class WorkationController {

	private final WorkationService workationService;

	@GetMapping("/workation")
	public List<WorkationDto> workations() {
		return workationService.findAll();
	}
}
