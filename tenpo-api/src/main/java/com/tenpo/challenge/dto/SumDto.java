package com.tenpo.challenge.dto;

import com.tenpo.challenge.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SumDto {
    private UUID id;
    private Double numberOne;
    private Double numberTwo;
    private Double percentage;
    private Double total;
    private Status status;
    private String error;
    private Instant timestamp;
}
