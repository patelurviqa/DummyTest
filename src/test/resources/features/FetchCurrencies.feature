@ui
Feature: Fetch currency Information from CoinMarketCap
  As a user for CoinmarketCap
  I want to fetch currency information from the portal using filters
  So that I can verify the data and controls present on the portal

  Scenario:
    Given I have a browser
    When I navigate to the coinmarketcap portal
    And I resize the table to display 20 rows
    Then I can capture information for first set of currencies
    When I filter by "Algorithm" with value "PoW"
    And I filter by additional mineable filter with value true
    And I filter by additional All cryptocurrencies filter with value "Coins"
    And I filter by additional Price filter with values 100 and 10000
    And I apply the filters
    Then I can capture information for second set of currencies
    And I can compare first and second currency sets