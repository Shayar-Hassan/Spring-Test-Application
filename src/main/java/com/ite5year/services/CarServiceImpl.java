package com.ite5year.services;

import com.ite5year.enums.SearchOperation;
import com.ite5year.models.*;
import com.ite5year.optimisticlock.OptimisticallyLocked;
import com.ite5year.payload.exceptions.ResourceNotFoundException;
import com.ite5year.repositories.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service(value = "CarServiceImpl")
@Transactional
public class CarServiceImpl implements CarService {

    @Autowired
    private CarRepository carRepository;
    private RedisTemplate redisTemplate;
    private SharedParametersServiceImpl sharedParametersService;
    private HashOperations<String, Long, Car> hashOperations;

    public CarServiceImpl(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public CarServiceImpl() {
    }

    @Autowired
    JdbcTemplate jdbcTemplate;



    @Autowired
    public void setRedisTemplate(RedisTemplate<String, SharedParam> redisTemplate) {
        this.redisTemplate = redisTemplate;
        hashOperations = this.redisTemplate.opsForHash();
    }

    private Map<Long, Car> mergeCarsEntitiesFromDBToRedis() {
        Map<Long, Car> dbCars = carRepository.findAll().stream()
                .collect(Collectors.toMap(Car::getId, Function.identity()));
        hashOperations.putAll("Cars", dbCars);
        return dbCars;
    }

    @Autowired
    public void setSharedParametersService(SharedParametersServiceImpl sharedParametersService) {
        this.sharedParametersService = sharedParametersService;
    }

    @Override
    public List<Car> findAll() {
        try {
            Map<Long, Car> cachedCars = hashOperations.entries("Cars");
            if (cachedCars.size() > 0) return new ArrayList<Car>(cachedCars.values());
            return new ArrayList<Car>(mergeCarsEntitiesFromDBToRedis().values());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Override
    public Car findById(long id) {
        Car car = hashOperations.get("Cars", id);
        if(car == null) {
            Optional<Car> optionalCar = carRepository
                    .findAll()
                    .stream()
                    .filter(c -> c.getId() == id)
                    .findFirst();
            optionalCar.ifPresent(c -> hashOperations.put("SharedParameters", id, c));
            return optionalCar.get();
        }
        return car;
    }

    @Override
    public Car deleteById(long id) throws ResourceNotFoundException {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found for this id :: " + id));
        carRepository.delete(car);
        hashOperations.delete("Cars", car.getId());
        return car;
    }

    @Override
    public Car save(Car car) {
        Car savedCar = carRepository.save(car);
        hashOperations.put("Cars", car.getId(), savedCar);
        return savedCar;
    }

    @Override
    public List<Car> findAllUnSoldCar() {
        GenericSpecification<Car> genericSpecification = new GenericSpecification<Car>();
        genericSpecification.add(new SearchCriteria("priceOfSale", 0, SearchOperation.GREATER_THAN));
        return carRepository.findAll(genericSpecification);
    }

    @Override
    public List<Car> findAllSoldCardByDate(LocalDateTime date) {
        List<Car> cars = carRepository.findAll();

        List<Car> res = new ArrayList<>();
        for(Car car: cars) {
            Date carDate = car.getDateOfSale();
            if(carDate != null) {
                Instant instant = carDate.toInstant();
                LocalDateTime ldtCar = LocalDateTime.from(instant.atZone(ZoneId.of("UTC")));
                if(ldtCar.getYear() == date.getYear()) {
                    if(ldtCar.getMonthValue() <= date.getMonthValue() && ldtCar.getMonthValue() > (date.getMonthValue() - 1)) {
                        res.add(car);
                    }
                }
            }
        }
        return res;
    }

    @Transactional
    @Override
    public ResponseEntity<Object> purchaseCar(long carId, PurchaseCarObject purchaseCarObject) {
        Optional<Car> carOptional = carRepository.findById(carId);
        if (!carOptional.isPresent())
            return ResponseEntity.notFound().build();


        System.out.println("Completed...");
        Car car = carOptional.get();
        car.setPayerName(purchaseCarObject.getPayerName());
        car.setDateOfSale(purchaseCarObject.getDateOfSale());
        try {
            SharedParam sharedParam = sharedParametersService.findByKey("profitPercentage");
            if(sharedParam != null) {
                double defaultPrice = Double.parseDouble(sharedParam.getFieldValue());
                double finalPrice;
                if(purchaseCarObject.getPriceOfSale() != 0) {
                    finalPrice = defaultPrice * purchaseCarObject.getPriceOfSale();
                } else {
                    finalPrice = defaultPrice * car.getPrice();
                }
                car.setPriceOfSale(finalPrice);
                carRepository.save(car);
                return ResponseEntity.ok(car);
            }
            else {
                return ResponseEntity.unprocessableEntity().body(car);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(car);
        }
    }

    public CarRepository getCarRepository() {
        return carRepository;
    }

    public void setCarRepository(CarRepository carRepository) {
        this.carRepository = carRepository;
    }
}
