package com.tenpo.challenge.repository;

import com.tenpo.challenge.entity.Sum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SumRepository extends JpaRepository<Sum, UUID> {
}