package org.example.dummytest.ui.model;

import lombok.Data;

@Data
public class CryptoCurrencyInformation {
    private int rank;
    private String name;
    private String symbol;
    private double price;
    private double hourlyChange;
    private double dailyChange;
    private double weeklyChange;
    private long marketCap;
    private long volumeUsd;
    private long volumeUnits;
    private long circulatingSupply;
}
