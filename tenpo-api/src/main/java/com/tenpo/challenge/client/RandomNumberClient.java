package com.tenpo.challenge.client;

import com.tenpo.challenge.dto.RandomNumberDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "randomNumberClient")
public interface RandomNumberClient {

    @GetMapping("/api/number")
    RandomNumberDto getRandomNumber();
}
