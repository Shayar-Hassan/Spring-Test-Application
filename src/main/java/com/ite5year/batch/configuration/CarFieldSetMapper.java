package com.ite5year.batch.configuration;

import com.ite5year.models.Car;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import java.time.ZoneId;

public class CarFieldSetMapper implements FieldSetMapper<Car> {
    @Override
    public Car mapFieldSet(FieldSet fieldSet) {
        final Car car = new Car();
        car.setName(fieldSet.readString("name"));
        car.setSeatsNumber(fieldSet.readInt("seatsNumber"));
        car.setPrice(fieldSet.readDouble("price"));
        car.setDateOfSale(fieldSet.readDate("dateOfSale"));
        car.setPriceOfSale(fieldSet.readDouble("priceOfSale"));
        return car;
    }
}
