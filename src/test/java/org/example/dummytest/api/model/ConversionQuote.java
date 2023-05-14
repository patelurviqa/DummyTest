package org.example.dummytest.api.model;

import lombok.Data;

import java.util.Map;

@Data
public class ConversionQuote {
    int id;
    String symbol;
    String name;
    double amount;
    Map<String, QuoteItem> quote;
}
