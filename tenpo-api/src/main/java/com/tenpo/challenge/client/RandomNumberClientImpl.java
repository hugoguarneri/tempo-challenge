package com.tenpo.challenge.client;

import com.tenpo.challenge.dto.RandomNumberDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
public class RandomNumberClientImpl {

    private final RandomNumberClient randomNumberClient;

    @Autowired
    public RandomNumberClientImpl(RandomNumberClient randomNumberClient) {
        this.randomNumberClient = randomNumberClient;
    }

    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000))
    @Cacheable(cacheManager = "cacheManager", value = "randomNumberCache", key = "'randomNumber'")
    public RandomNumberDto getRandomNumber() {
        return randomNumberClient.getRandomNumber();
    }

}
