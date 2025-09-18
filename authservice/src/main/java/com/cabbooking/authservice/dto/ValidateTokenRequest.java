package com.cabbooking.authservice.dto;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ValidateTokenRequest {
    private String token;
}