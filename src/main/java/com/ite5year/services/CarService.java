package com.ite5year.services;

import com.ite5year.models.Car;
import com.ite5year.models.GenericSpecification;
import com.ite5year.models.PurchaseCarObject;
import com.ite5year.payload.exceptions.ResourceNotFoundException;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.http.ResponseEntity;

import javax.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface CarService {
    List<Car> findAll();
    Car findById(long id);
    Car deleteById(long id) throws ResourceNotFoundException;
    Car save(Car car);
    List<Car> findAllUnSoldCar();
    @Lock(LockModeType.PESSIMISTIC_FORCE_INCREMENT)
    List<Car> findAllSoldCardByDate(LocalDateTime date);
    ResponseEntity<Object> purchaseCar(long carId, PurchaseCarObject purchaseCarObject);

}
