package com.ynz.demo.springjpatransaction.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;


@Builder
@AllArgsConstructor
@Getter
@Setter
public class OrderDto {
    private OffsetDateTime creationDateTime;
    private UUID givenOrderId;
}
