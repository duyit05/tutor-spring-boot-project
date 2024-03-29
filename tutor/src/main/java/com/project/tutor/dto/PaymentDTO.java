package com.project.tutor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentDTO {
    private int paymentId;
    private String paymenName;
    private LocalDateTime createAt;
    private String description;
    private double paymentPrice;
    private String paymentStatus;
}
