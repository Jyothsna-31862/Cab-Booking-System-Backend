package com.cabbooking.serviceImpl;

import com.cabbooking.dto.DriverDto;
import com.cabbooking.dto.DriverRequest;
import com.cabbooking.dto.DriverServiceResponse;
import com.cabbooking.exception.EmailAlreadyExistsException;
import com.cabbooking.exception.PhoneAlreadyExistsException;
import com.cabbooking.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import com.cabbooking.entity.Driver;
import com.cabbooking.service.DriverService;


@Slf4j
@RequiredArgsConstructor
@Service
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final ModelMapper modelMapper;

    @Override
    public DriverServiceResponse registerDriver(DriverRequest driverRequest) {

        log.info("DriverService class invoked");

        if (driverRepository.existsByEmail(driverRequest.getEmail())) {
            throw new EmailAlreadyExistsException("Given Email Already Registered: " + driverRequest.getEmail());
        }

        if (driverRepository.existsByPhone(driverRequest.getPhone())) {
            throw new PhoneAlreadyExistsException("Given Phone number already registered: " + driverRequest.getPhone());
        }

        Driver driver = modelMapper.map(driverRequest, Driver.class);

        Driver savedDriver = driverRepository.save(driver);

        log.info("Driver Saved Successfully");

        DriverDto driverDto = modelMapper.map(savedDriver, DriverDto.class);

        DriverServiceResponse driverResponse = new DriverServiceResponse();
        driverResponse.setBody(driverDto);
        driverResponse.setStatus("success");
        driverResponse.setMessage("Driver Registered Successfully");
        return driverResponse;

    }
}
