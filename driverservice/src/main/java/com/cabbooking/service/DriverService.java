package com.cabbooking.service;
import java.util.List;

import com.cabbooking.dto.DriverDto;
import com.cabbooking.dto.DriverRequest;
import com.cabbooking.dto.DriverServiceResponse;
import com.cabbooking.exception.DriverNotFoundException;


public interface DriverService {
    DriverServiceResponse registerDriver(DriverRequest driver);

    DriverDto getDriverById(String id);


    DriverDto updateDriverProfile(String id, DriverRequest driverRequest) throws DriverNotFoundException;
    void deleteDriver(String id) throws DriverNotFoundException;
    DriverServiceResponse forgotPassword(String email,String newPassword);
    DriverDto updateDriverStatus(String id) throws DriverNotFoundException;
    DriverDto getAvailableDrivers(String carSeater);

    DriverDto getDriverByEmail(String email);
}

