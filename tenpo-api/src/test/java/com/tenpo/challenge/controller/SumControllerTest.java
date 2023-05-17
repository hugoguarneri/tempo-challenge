package com.tenpo.challenge.controller;

import com.tenpo.challenge.client.RandomNumberClient;
import com.tenpo.challenge.dto.RandomNumberDto;
import com.tenpo.challenge.entity.Status;
import com.tenpo.challenge.entity.Sum;
import com.tenpo.challenge.repository.SumRepository;
import io.github.bucket4j.Bucket;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;


@SpringBootTest
@AutoConfigureMockMvc
public class SumControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private Bucket rateLimiter;

    @Autowired
    private SumRepository sumRepository;

    @MockBean
    private RandomNumberClient randomNumberClient;

    private final static String URI = "/api/sum";

    @AfterEach
    public void tearDown() {
        rateLimiter.reset();
        Objects.requireNonNull(cacheManager.getCache("randomNumberCache")).clear();
        sumRepository.deleteAll();
    }

    @Test
    public void test_sum_returnsOk() throws Exception {
        double numberOne = 10.0;
        double numberTwo = 5.0;
        RandomNumberDto randomNumberDto = new RandomNumberDto(26.0);

        Mockito.when(randomNumberClient.getRandomNumber()).thenReturn(randomNumberDto);

        mvc.perform(MockMvcRequestBuilders.get(URI+"?number_one="+numberOne+"&number_two="+numberTwo))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.number_one", Matchers.equalTo(numberOne)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.number_two", Matchers.equalTo(numberTwo)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.percentage", Matchers.equalTo(randomNumberDto.getValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.total", Matchers.equalTo(18.9)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.equalTo(Status.success.toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error", Matchers.nullValue()));

    }

    @Test
    public void test_sum_fourTimes_returnsTooManyRequest() throws Exception {
        double numberOne = 10.0;
        double numberTwo = 5.0;
        RandomNumberDto randomNumberDto = new RandomNumberDto(26.0);

        Mockito.when(randomNumberClient.getRandomNumber()).thenReturn(randomNumberDto);

        for (int i = 0; i < 3; i++) {
            mvc.perform(MockMvcRequestBuilders.get(URI+"?number_one="+numberOne+"&number_two="+numberTwo))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(MockMvcResultMatchers.status().isOk());
        }

        mvc.perform(MockMvcRequestBuilders.get(URI+"?number_one="+numberOne+"&number_two="+numberTwo))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isTooManyRequests());
    }

    @Test
    public void test_sum_consumesTheExternalServiceOnlyOnTheFirstCallThenUseCachedValue_returnsOk() throws Exception {
        double numberOne = 10.0;
        double numberTwo = 5.0;
        RandomNumberDto randomNumberDto = new RandomNumberDto(26.0);

        Mockito.when(randomNumberClient.getRandomNumber()).thenReturn(randomNumberDto);

        for (int i = 0; i < 3; i++) {
            mvc.perform(MockMvcRequestBuilders.get(URI+"?number_one="+numberOne+"&number_two="+numberTwo))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.number_one", Matchers.equalTo(numberOne)))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.number_two", Matchers.equalTo(numberTwo)))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.percentage", Matchers.equalTo(randomNumberDto.getValue())))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.total", Matchers.equalTo(18.9)))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.equalTo(Status.success.toString())))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.error", Matchers.nullValue()));
        }

        Mockito.verify(randomNumberClient, Mockito.times(1)).getRandomNumber();
    }

    @Test
    public void test_sum_consumesTheExternalServiceAndGetInternalServerErrorThreeTimes_returnsServicesUnavailable() throws Exception {
        double numberOne = 10.0;
        double numberTwo = 5.0;
        RandomNumberDto randomNumberDto = new RandomNumberDto(26.0);

        Mockito.when(randomNumberClient.getRandomNumber()).thenThrow(new RuntimeException("Internal Server Error"));

        mvc.perform(MockMvcRequestBuilders.get(URI+"?number_one="+numberOne+"&number_two="+numberTwo))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isServiceUnavailable())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.equalTo("Failed to perform the operation.")));

        Mockito.verify(randomNumberClient, Mockito.times(3)).getRandomNumber();
    }

    @Test
    public void test_sum_consumesTheExternalServiceAndGetInternalServerErrorTwoTimesAndThenWorks_returnsOk() throws Exception {
        double numberOne = 10.0;
        double numberTwo = 5.0;
        RandomNumberDto randomNumberDto = new RandomNumberDto(26.0);

        Mockito.when(randomNumberClient.getRandomNumber())
                .thenThrow(new RuntimeException("Internal Server Error"))
                .thenThrow(new RuntimeException("Internal Server Error"))
                .thenReturn(randomNumberDto);

        mvc.perform(MockMvcRequestBuilders.get(URI+"?number_one="+numberOne+"&number_two="+numberTwo))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.number_one", Matchers.equalTo(numberOne)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.number_two", Matchers.equalTo(numberTwo)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.percentage", Matchers.equalTo(randomNumberDto.getValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.total", Matchers.equalTo(18.9)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.equalTo(Status.success.toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error", Matchers.nullValue()));

        Mockito.verify(randomNumberClient, Mockito.times(3)).getRandomNumber();
    }

    @Test
    public void test_history_returnsOk() throws Exception {
        Sum sum = sumRepository.save(Sum.builder()
                .id(UUID.randomUUID())
                .numberOne(10.0)
                .numberTwo(5.0)
                .percentage(26.0)
                .total(18.9)
                .status(Status.success)
                .timestamp(Instant.now())
                .build()
        );

        mvc.perform(MockMvcRequestBuilders.get(URI+"/history?page=0"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()", Matchers.equalTo(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].id", Matchers.equalTo(sum.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].status", Matchers.equalTo(sum.getStatus().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].total", Matchers.equalTo(sum.getTotal())));
    }

}
