package org.example.dummytest.ui.steps;

import io.cucumber.java.BeforeAll;
import io.cucumber.java.BeforeStep;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.example.dummytest.ui.model.CryptoCurrencyInformation;
import org.example.dummytest.ui.pages.HomePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class StepDefinitions {
    private static Properties properties;
    private WebDriver driver;
    private HomePage homePage;
    private List<CryptoCurrencyInformation> currencyInformationList1;
    private List<CryptoCurrencyInformation> currencyInformationList2;
    private Scenario scenario;

    @BeforeAll
    public static void beforeAllSetup() throws IOException {
        properties = new Properties();
        properties.load(new FileInputStream("src/test/resources/application.properties"));
    }

    @BeforeStep
    public void beforeStep(Scenario scenario) {
        this.scenario = scenario;
    }

    @Given("I have a browser")
    public void i_have_a_browser() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }
    @When("I navigate to the coinmarketcap portal")
    public void i_navigate_to_the_coinmarketcap_portal() {
        driver.navigate().to(properties.getProperty("url"));
        homePage = new HomePage(driver);
        homePage.rejectCookies();
    }
    @When("I resize the table to display {int} rows")
    public void i_resize_the_table_to_display_rows(Integer numberOfRows) throws InterruptedException {
        homePage.setPageRowSize(numberOfRows);
    }
    @Then("I can capture information for first set of currencies")
    public void i_can_capture_information_for_first_set_of_currencies() throws ParseException {
        currencyInformationList1 = homePage.getListedCurrencies();
        scenario.log("unfiltered currencies found as below :");for (CryptoCurrencyInformation c :  currencyInformationList1) {
            scenario.log(c.toString());
        }
    }
    @When("I filter by {string} with value {string}")
    public void i_filter_by_with_value(String string, String string2) {
        homePage.setSimpleFilter("Algorithm", "PoW");
    }
    @When("I filter by additional mineable filter with value true")
    public void i_filter_by_additional_mineable_filter_with_value_true() {
        homePage.toggleMineableFilter(true);
    }
    @When("I filter by additional All cryptocurrencies filter with value {string}")
    public void i_filter_by_additional_all_cryptocurrencies_filter_with_value(String string) throws InterruptedException {
        homePage.addCryptoCurrenciesFilter("Coins");
    }
    @When("I filter by additional Price filter with values {int} and {int}")
    public void i_filter_by_additional_price_filter_with_values_and(Integer int1, Integer int2) throws InterruptedException {
        homePage.addPriceFilter(100, 10000);
    }
    @When("I apply the filters")
    public void i_apply_the_filters() {
        homePage.applyAdditionalFilters();
    }
    @Then("I can capture information for second set of currencies")
    public void i_can_capture_information_for_second_set_of_currencies() throws ParseException {
        currencyInformationList2 = homePage.getListedCurrencies();
        scenario.log("filtered currencies found as below :");
        for (CryptoCurrencyInformation c :  currencyInformationList1) {
            scenario.log(c.toString());
        }
    }
    @Then("I can compare first and second currency sets")
    public void i_can_compare_first_and_second_currency_sets() {
        scenario.log("unfiltered currencies count : " + currencyInformationList1.size());
        scenario.log("filtered currencies count : " + currencyInformationList2.size());
        List<CryptoCurrencyInformation> commonCurrencies = new ArrayList<>(currencyInformationList1);
        commonCurrencies.retainAll(currencyInformationList2);

        scenario.log("common currencies count : " + commonCurrencies.size());
        if (!commonCurrencies.isEmpty()) {
            scenario.log("common currencies are as below ");
            for (CryptoCurrencyInformation c : commonCurrencies) {
                scenario.log(c.toString());
            }
        }
    }

}
