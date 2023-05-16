package com.tenpo.challenge.service;

import com.tenpo.challenge.client.RandomNumberClientImpl;
import com.tenpo.challenge.dto.RandomNumberDto;
import com.tenpo.challenge.dto.SumDto;
import com.tenpo.challenge.entity.Status;
import com.tenpo.challenge.entity.Sum;
import com.tenpo.challenge.event.SaveSumEvent;
import com.tenpo.challenge.mapper.SumMapper;
import com.tenpo.challenge.repository.SumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class SumService {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final RandomNumberClientImpl randomNumberClient;
    private final SumRepository sumRepository;
    private final SumMapper sumMapper;

    @Autowired
    public SumService(
            ApplicationEventPublisher applicationEventPublisher,
            RandomNumberClientImpl randomNumberClient,
            SumRepository sumRepository,
            SumMapper sumMapper
    ) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.randomNumberClient = randomNumberClient;
        this.sumRepository = sumRepository;
        this.sumMapper = sumMapper;
    }

    public SumDto calculate(Double numberOne, Double numberTwo) {
        Double percentage = Optional.ofNullable(randomNumberClient.getRandomNumber())
                .map(RandomNumberDto::getValue)
                .orElseThrow(() -> new RuntimeException("Error trying to get percentage value"));

        Double total = BigDecimal
                .valueOf((numberOne + numberTwo) * (1 + percentage / 100))
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();

        SumDto sumDto = SumDto.builder()
                .id(UUID.randomUUID())
                .numberOne(numberOne)
                .numberTwo(numberTwo)
                .percentage(percentage)
                .total(total)
                .timestamp(Instant.now())
                .status(Status.success)
                .build();

        applicationEventPublisher.publishEvent(new SaveSumEvent(sumDto));
        return sumDto;
    }

    public void save(SumDto sumDto) {
        Sum sum = sumMapper.toModel(sumDto);
        sumRepository.save(sum);
    }

    public Page<SumDto> findAll(Pageable pageable) {
        return sumRepository.findAll(pageable).map(sumMapper::toDto);
    }

}