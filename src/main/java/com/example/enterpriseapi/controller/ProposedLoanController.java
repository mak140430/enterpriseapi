package com.example.enterpriseapi.controller;

import com.example.enterpriseapi.service.ProposedLoanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/proposed-loans")
public class ProposedLoanController {
    private static final Logger logger = LoggerFactory.getLogger(ProposedLoanController.class);
    private final ProposedLoanService service;

    public ProposedLoanController(ProposedLoanService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public String getLoan(@PathVariable String id) {
        logger.info("Received request for proposed loan with ID: {}", id);
        String response = service.getProposedLoanById(id);
        logger.info("Successfully retrieved proposed loan with ID: {}", id);
        return response;
    }
}