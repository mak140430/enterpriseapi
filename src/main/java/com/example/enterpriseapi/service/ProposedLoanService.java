package com.example.enterpriseapi.service;

import com.example.enterpriseapi.client.SageworksClient;
import org.springframework.stereotype.Service;

@Service
public class ProposedLoanService {

    private final SageworksClient client;

    public ProposedLoanService(SageworksClient client) {
        this.client = client;
    }

    public String getProposedLoanById(String id) {
        String token = client.getAccessToken();
        return client.getProposedLoanById(id, token);
    }
}