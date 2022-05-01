package com.example.myitemsrest.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class CurrencyService {

    private final RestTemplate restTemplate;
    @Value("${myitems.cb.url}")
    private String cbUrl;

    @Cacheable("currencies")
    public HashMap<String, String> getCurrencies() {
        String rubCurrencyUrl = cbUrl ;
        ResponseEntity<HashMap> currencies = restTemplate.getForEntity(rubCurrencyUrl, HashMap.class);
        HashMap<String, String> currencyMap = currencies.getBody();
        return currencyMap;
    }

}
