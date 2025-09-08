package com.cabbooking.serviceImpl;

import com.cabbooking.entity.Driver;
import com.cabbooking.repository.DriverRepository;
import com.cabbooking.service.DriverService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor

@Service
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;

    public Driver registerDriver(Driver driver) {

        Driver d = driverRepository.save(driver);

        return d;
    }
}
