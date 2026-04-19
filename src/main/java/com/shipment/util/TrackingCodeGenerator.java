package com.shipment.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class TrackingCodeGenerator {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    public String generate(Long id) {
        return "ENV-" + LocalDate.now().format(FORMATTER) + "-" + String.format("%05d", id);
    }
}
