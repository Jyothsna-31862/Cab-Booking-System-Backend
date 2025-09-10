package com.cabbooking.service;
import com.cabbooking.dto.DriverRequest;
import com.cabbooking.dto.DriverServiceResponse;


public interface DriverService {
    DriverServiceResponse registerDriver(DriverRequest driver);

}
