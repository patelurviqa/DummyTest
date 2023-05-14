@api
Feature: Convert currencies and cryptocurrencies using Coinmarket apis
  As a pro user of CoinmarketCap api
  I want to use the api to convert different currencies and crypto instruments
  So that I can verify the conversion functionality

  Scenario: Converting currency to other currency and crypto
    Given I have a client for Coinmarket api
    When I send a fiat currency conversion request for 10000000 from "GTQ" to "GBP"
    Then I get a successful response
    And I get the converted "GBP" value
    When I send a crypto currency conversion request for converted value from "GBP" to "DOGE"
    Then I get a successful response
    And I get the converted "DOGE" value