package com.api.futmail.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubscriberResponse {
    private Long id;
    private String email;
    private LocalDateTime createdAt;
    private String status;
}
