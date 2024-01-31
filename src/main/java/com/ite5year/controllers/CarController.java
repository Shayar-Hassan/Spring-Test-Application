package com.ite5year.controllers;


import com.ite5year.daos.CarDao;
import com.ite5year.messagingrabbitmq.RabbitMQSender;
import com.ite5year.models.*;
import com.ite5year.payload.exceptions.ResourceNotFoundException;
import com.ite5year.repositories.ApplicationUserRepository;
import com.ite5year.repositories.CarRepository;
import com.ite5year.repositories.LogsRepository;
import com.ite5year.services.*;
import com.ite5year.utils.GlobalOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.ite5year.utils.GlobalConstants.BASE_URL;

@RestController()
@RequestMapping(BASE_URL + "/cars")
public class CarController {


    private CacheServiceImpl cacheService;
    private CarRepository carRepository;
    private CarServiceImpl carService;
    RabbitMQSender rabbitMQSender;
    private SharedParametersServiceImpl sharedParametersService;
    private CarDao carDao;
    private AuthenticationService authenticationService;
    private ApplicationUserDetailsServiceImpl applicationUserDetailsService;
    private ApplicationUserRepository applicationUserRepository;
    private LogsRepository logsRepository;

    @Autowired
    public void setApplicationUserRepository(ApplicationUserRepository applicationUserRepository) {
        this.applicationUserRepository = applicationUserRepository;
    }

    @Autowired
    public void setLogsRepository(LogsRepository logsRepository) {
        this.logsRepository = logsRepository;
    }

    @Autowired
    public void setApplicationUserDetailsService(ApplicationUserDetailsServiceImpl applicationUserDetailsService) {
        this.applicationUserDetailsService = applicationUserDetailsService;
    }

    @Autowired
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Autowired
    public void setRabbitMQSender(RabbitMQSender rabbitMQSender) {
        this.rabbitMQSender = rabbitMQSender;
    }

    @Autowired
    public void setCarDao(CarDao carDao) {
        this.carDao = carDao;
    }

    public CarServiceImpl getCarService() {
        return carService;
    }

    @Autowired
    public void setCarService(CarServiceImpl carService) {
        this.carService = carService;
    }

    @Autowired
    public void setCacheService(CacheServiceImpl cacheService) {
        this.cacheService = cacheService;
    }

    public CarRepository getCarRepository() {
        return carRepository;
    }

    @Autowired
    public void setSharedParametersService(SharedParametersServiceImpl sharedParametersService) {
        this.sharedParametersService = sharedParametersService;
    }

    @Autowired
    public void setCarRepository(CarRepository carRepository) {
        this.carRepository = carRepository;
    }


    private void addLog(String processName, String target) {
        try {
            ApplicationUser user = applicationUserDetailsService.currentUser()
                    .orElseThrow(() -> new UsernameNotFoundException("You need to login again"));
            Logs logs = new Logs(new Date(), processName, user.getUsername(), user.getEmail(), target);
            logsRepository.save(logs);
        } catch (Exception e) {
            System.out.println("ERROR: " + e);
        }
    }

    @GetMapping
    public List<Car> retrieveAllCars() {
        addLog(GlobalOperations.GET_CARS, "all_cars");
        List<Car> cars =  carService.findAll();
        cars.forEach(System.out::println);
        return cars;
    }


    @GetMapping("/{id}")
    @Cacheable(value = "cars", key = "#id")
    public Car retrieveCarById(@PathVariable long id) throws ResourceNotFoundException {
        addLog(GlobalOperations.GET_CAR, String.valueOf(id));
        return carRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Car with id " + id + " is not found!"));

    }



    @DeleteMapping("/{id}")
    @CacheEvict(value = "cars", allEntries = true)
    public Map<String, Boolean> deleteCar(@PathVariable long id) throws ResourceNotFoundException {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found for this id :: " + id));


        carRepository.delete(car);
        addLog(GlobalOperations.DELETE_CAR, car.getId().toString());

        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return response;
    }

    private void updateSeatsNumberOfCar(Car car) throws Exception {
        if (car.getSeatsNumber() <= 0) {
            SharedParam sharedParam = sharedParametersService.findByKey("seatsNumber");
            if(sharedParam != null) {
                String numberOfSeats = sharedParam.getFieldValue();
                int seatsNumber = Integer.parseInt(numberOfSeats);
                car.setSeatsNumber(seatsNumber);
            } else {
                throw new Exception("Shared param for seats number does not exist");
            }
        }
    }


    @PostMapping("/create")
    public @ResponseBody
    Car createNewCar(@RequestBody Car car) throws Exception {
        updateSeatsNumberOfCar(car);
        if (car.getDateOfSale() != null || car.getPayerName() != null) {
            throw new Exception("Cannot provide " + car.getDateOfSale() + " or " + car.getPayerName() + "  when you're creating the car");
        }

        try {
            ApplicationUser user = applicationUserDetailsService.currentUser()
                    .orElseThrow(() -> new UsernameNotFoundException("You need to login again"));
            car.setOwner(user);
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
        addLog(GlobalOperations.ADD_CAR, car.getId().toString());

        return carService.save(car);
    }


    @PostMapping("/create/with-optimistic-lock")
    public @ResponseBody
    Car createNewCarWithOptimisticLock(@RequestBody Car car) throws Exception {
        updateSeatsNumberOfCar(car);
        if (car.getDateOfSale() != null || car.getPayerName() != null) {
            throw new Exception("Cannot provide " + car.getDateOfSale() + " or " + car.getPayerName() + "  when you're creating the car");
        }
        addLog(GlobalOperations.ADD_CAR, car.getId().toString());
        return carDao.saveCarByJDBC(car);
    }

    @Transactional
    @PutMapping("/{id}")
    @CachePut(value = "cars", key = "#updatedCar.id")
    public ResponseEntity<Object> updateCar(@RequestBody Car updatedCar) throws ResourceNotFoundException {

        Car car = carRepository.findById(updatedCar.getId()).orElseThrow(() -> new ResourceNotFoundException("Car with id " + updatedCar.getId() + " is not found!"));
        car.setSeatsNumber(updatedCar.getSeatsNumber());
        car.setPayerName(updatedCar.getPayerName());
        car.setDateOfSale(updatedCar.getDateOfSale());
        car.setPriceOfSale(updatedCar.getPriceOfSale());
        car.setName(updatedCar.getName());

        final Car responseCar = carRepository.save(car);
        addLog(GlobalOperations.UPDATE_CAR, responseCar.getId().toString());

        return ResponseEntity.ok(responseCar);
    }

    int counter = 0;
    @PutMapping("/purchase/opt")
    public ResponseEntity<Object> purchaseCarWithOptimisticLock(@RequestBody Car car)
            throws ResourceNotFoundException{
//        Car car = carRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Car with id " + id + " is not found!"));
//        car.setDateOfSale(purchaseCarObject.getDateOfSale());
//        car.setPayerName(purchaseCarObject.getPayerName());
//
//        System.out.println("HERE LONG VERSION: " + car.getVersion());
//        System.out.println("CAR OWNER: " + car.getPayerName());
//
//        SharedParam sharedParam = sharedParametersService.findByKey("profitPercentage");
//        if(sharedParam != null) {
//            double defaultPrice = Double.parseDouble(sharedParam.getFieldValue());
//            double finalPrice;
//            if(purchaseCarObject.getPriceOfSale() != 0) {
//                finalPrice = defaultPrice * purchaseCarObject.getPriceOfSale();
//            } else {
//                finalPrice = defaultPrice * car.getPrice();
//            }
//            car.setPriceOfSale(finalPrice);
//        }
        long pr;
        if(counter == 0) pr = 30;
        else pr = 10;
        counter++;
        addLog(GlobalOperations.PURCHASE_CAR, car.getId().toString());
        return ResponseEntity.ok(carDao.updateCarByJDBC(car, pr));
    }

    @PutMapping("/purchase/{id}")
    public ResponseEntity<Object> purchaseCar(@RequestBody PurchaseCarObject purchaseCarObject, @PathVariable long id) {
        addLog(GlobalOperations.PURCHASE_CAR, String.valueOf(id));
        return carService.purchaseCar(id, purchaseCarObject);
    }


    @GetMapping("/un-sold")
    public @ResponseBody
    List<Car> getAllUnSoldCars() {
        addLog(GlobalOperations.GET_UN_SOLD_CARS, "un_sold_cars");
        return carService.findAllUnSoldCar();
    }

    @GetMapping("/selling-date/{sellingDate}")
    public @ResponseBody
    List<Car> getAllSoldCarInMonth(@PathVariable String sellingDate) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        final LocalDateTime dt = LocalDateTime.parse(sellingDate, formatter);
        try {
            addLog(GlobalOperations.GET_SOLD_CARS_IN_MONTH, sellingDate);
            return carService.findAllSoldCardByDate(dt);
        } catch (Exception e) {
            System.out.println("Exc: " + e);
            return null;
        }
    }


    @PostMapping("/report")
    public RabbitMessage generateReportForCars(@RequestBody RabbitMessage rabbitMessage) {
        System.out.println(rabbitMessage);
        addLog(GlobalOperations.GENERATE_REPORT_FOR_CARS, rabbitMessage.getEmail());
        return rabbitMQSender.send(rabbitMessage);
    }


    @DeleteMapping("/evict-caching")
    public void evictCaching() {
        addLog(GlobalOperations.EVICT_CACHING_FOR_CARS, "cached_cars");
        cacheService.evictAllCacheValues("cars");
    }
}
