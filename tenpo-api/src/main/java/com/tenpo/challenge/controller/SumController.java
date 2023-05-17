package com.tenpo.challenge.controller;

import com.tenpo.challenge.dto.SumDto;
import com.tenpo.challenge.exception.RateLimitException;
import com.tenpo.challenge.service.SumService;
import io.github.bucket4j.Bucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/sum")
public class SumController {

    private final SumService sumService;
    private final Bucket rateLimiter;

    @Autowired
    public SumController(SumService sumService, Bucket rateLimiter) {
        this.sumService = sumService;
        this.rateLimiter = rateLimiter;
    }

    @GetMapping("")
    private ResponseEntity<SumDto> sum(
            @RequestParam(value = "number_one", required = true) Double numberOne,
            @RequestParam(value = "number_two", required = true) Double numberTwo
    ) {
        if(!rateLimiter.tryConsume(1)) {
            throw new RateLimitException();
        }
        SumDto result = sumService.calculate(numberOne, numberTwo);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/history")
    private ResponseEntity<Page<SumDto>> history(
            Pageable pageable
    ) {
        Page<SumDto> page = sumService.findAll(pageable);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

}
