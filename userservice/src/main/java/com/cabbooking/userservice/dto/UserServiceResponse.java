package com.cabbooking.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.ResponseStatus;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserServiceResponse {
   private UserDto body;
   private String status;
   private String message;
}
