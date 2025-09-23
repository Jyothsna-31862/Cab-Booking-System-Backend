package com.cabbooking.paymentservice;

import com.cabbooking.paymentservice.controller.PaymentControllerTests;
import com.cabbooking.paymentservice.service.PaymentServiceTests;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
//@ActiveProfiles("test")
//@TestPropertySource(properties = {
//        "spring.datasource.url=jdbc:h2:mem:testdb",
//        "spring.datasource.driver-class-name=org.h2.Driver",
//        "spring.datasource.username=sa",
//        "spring.datasource.password=",
//        "spring.jpa.hibernate.ddl-auto=create-drop",
//        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
//        "eureka.client.enabled=false",
//        "spring.cloud.config.enabled=false",
//        "spring.cloud.discovery.enabled=false"
//})
@Suite
@SelectClasses({
        PaymentServiceTests.class,
        PaymentControllerTests.class
})
class PaymentserviceApplicationTests {

    // Test suite for Payment Service
    // This orchestrates the execution of all payment service tests
    // including service layer and controller layer tests
}
