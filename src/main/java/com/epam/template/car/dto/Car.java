package com.epam.template.car.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Car {
    private CarDto dto;
    private CarStatus status;
}
