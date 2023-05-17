package com.tenpo.challenge.service;

import com.tenpo.challenge.client.RandomNumberClientImpl;
import com.tenpo.challenge.dto.RandomNumberDto;
import com.tenpo.challenge.dto.SumDto;
import com.tenpo.challenge.entity.Status;
import com.tenpo.challenge.entity.Sum;
import com.tenpo.challenge.event.SaveSumEvent;
import com.tenpo.challenge.exception.AppException;
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
        try {
            Double percentage = randomNumberClient.getRandomNumber().getValue();
            Double total = BigDecimal
                    .valueOf((numberOne + numberTwo) * (1 + percentage / 100))
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();

            SumDto sumDto = SumDto.builder()
                    .id(UUID.randomUUID())
                    .timestamp(Instant.now())
                    .status(Status.success)
                    .numberOne(numberOne)
                    .numberTwo(numberTwo)
                    .percentage(percentage)
                    .total(total)
                    .build();

            applicationEventPublisher.publishEvent(new SaveSumEvent(sumDto));
            return sumDto;
        } catch (Exception ex) {
            String errorMessage = "Error trying to get percentage value from random number service.";
            SumDto sumDto = SumDto.builder()
                    .id(UUID.randomUUID())
                    .timestamp(Instant.now())
                    .status(Status.error)
                    .error(errorMessage)
                    .numberOne(numberOne)
                    .numberTwo(numberTwo)
                    .build();

            applicationEventPublisher.publishEvent(new SaveSumEvent(sumDto));
            throw new AppException(errorMessage);
        }
    }

    public void save(SumDto sumDto) {
        Sum sum = sumMapper.toModel(sumDto);
        sumRepository.save(sum);
    }

    public Page<SumDto> findAll(Pageable pageable) {
        return sumRepository.findAll(pageable).map(sumMapper::toDto);
    }

}