package com.epam.template;

import com.epam.template.car.dto.Car;
import com.epam.template.car.dto.CarDto;
import com.epam.template.car.dto.CarResource;
import com.epam.template.car.dto.CarStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class CarManager implements CarResource {
    Map<Long, Car> cars = new HashMap<>();

    @PostConstruct
    void init() {
        for (int i = 0; i < 3; i++) {
            Long id = (long) i;
            CarDto dto = CarDto.builder()
                    .id(id)
                    .model("Tesla")
                    .regNumber("00" + i)
                    .build();

            Car car = new Car(dto, CarStatus.AVAILABLE);

            cars.put(id, car);
        }

        log.info("initialization done");
    }

    @Override
    public CarDto createCar(CarDto carDto) {
        log.info("createCar...");

        cars.put(carDto.getId(), new Car(carDto, CarStatus.AVAILABLE));
        return carDto;
    }

    @Override
    public CarDto getCar(Long id) {
        log.info("getCar id={}...", id);

        if (!cars.containsKey(id))
            return null;

        return cars.get(id).getDto();
    }

    @Override
    public Page<CarDto> getCars(String filter, Pageable pageable) {
        log.info("getCars filter={}...", filter);
        return getAllCars();
    }

    @Override
    public Page<CarDto> getAllCars() {
        log.info("getAllCars...");
        List<CarDto> list = cars.values().stream().map(Car::getDto).collect(Collectors.toList());
        return new PageImpl<>(list);
    }

    @Override
    public CarDto updateCar(Long id, CarDto carDto) {
        log.info("updateCar id={}...", id);
        Car car = cars.get(id);
        if (car == null)
            throw new IllegalStateException("Car not found");

        car.setDto(carDto);
        cars.put(id, car);
        return carDto;
    }

    @Override
    public CarDto inRentCar(Long id, Date endDate) {
        log.info("inRentCar id={}, endDate={}...", id, endDate);
        Car car = cars.get(id);
        if (car == null)
            throw new IllegalStateException("Car not found");

        if (car.getStatus() != CarStatus.AVAILABLE)
            throw new IllegalStateException("Car not AVAILABLE");

        car.setStatus(CarStatus.IN_RENT);
        CarDto dto = car.getDto();
        dto.setCurrentStatus("IN_RENT");
        dto.setDateOfCurrentStatus(endDate);

        cars.put(id, car);
        return dto;
    }

    @Override
    public CarDto inStockCar(Long id) {
        throw new NotImplementedException();
    }

    @Override
    public CarDto onMaintenance(Long id) {
        throw new NotImplementedException();
    }

    @Override
    public CarDto dropOut(Long id) {
        log.info("dropOut id={}...", id);

        Car car = cars.get(id);
        if (car == null)
            throw new IllegalStateException("Car not found");

        if (car.getStatus() == CarStatus.AVAILABLE)
            throw new IllegalStateException("Car is AVAILABLE already");

        car.setStatus(CarStatus.AVAILABLE);
        CarDto dto = car.getDto();
        dto.setCurrentStatus("AVAILABLE");

        cars.put(id, car);
        return dto;
    }

    @Override
    public void deleteCar(Long id) {
        log.info("deleteCar id={}...", id);
        Car car = cars.get(id);
        if (car == null)
            throw new IllegalStateException("Car not found");

        cars.remove(id);
    }
}
