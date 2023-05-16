package com.tenpo.challenge.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
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
@Entity
@Table(name = "sum_history")
public class Sum {
    @Id
    @Column(name = "id")
    //@GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "number_one")
    private Double numberOne;

    @Column(name = "number_two")
    private Double numberTwo;

    @Column(name = "percentage")
    private Double percentage;

    @Column(name = "total")
    private Double total;

    @Column(name = "status")
    private Status status;

    @Column(name = "error")
    private String error;

    @Column(name = "timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Instant timestamp;
}
