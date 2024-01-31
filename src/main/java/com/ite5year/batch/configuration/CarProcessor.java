package com.ite5year.batch.configuration;

import com.ite5year.models.Car;
import org.springframework.batch.item.ItemProcessor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

public class CarProcessor implements ItemProcessor<Car, Car> {


    @Override

    public Car process(final Car car) {
        final String name = car.getName();
        final double price = car.getPrice();
        final int seatsNumber = car.getSeatsNumber();
        final Date dateOfSale = car.getDateOfSale();
        final double priceOfSale = car.getPriceOfSale();


        final Car processedCar = new Car();
        processedCar.setName(name);
        processedCar.setPrice(price);
        processedCar.setSeatsNumber(seatsNumber);
        processedCar.setDateOfSale(dateOfSale);
        processedCar.setPriceOfSale(priceOfSale);
        return processedCar;

    }

}