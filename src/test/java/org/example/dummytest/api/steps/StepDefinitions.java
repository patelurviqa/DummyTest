package org.example.dummytest.api.steps;

import io.cucumber.java.BeforeAll;
import io.cucumber.java.BeforeStep;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.http.Method;
import io.restassured.response.Response;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.example.dummytest.api.model.ConversionQuoteDto;
import org.junit.Assert;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static org.example.dummytest.api.util.RequestMapper.getMapper;

public class StepDefinitions {
    private static Properties properties;
    private Scenario scenario;
    private Header apiKeyHeader;
    private Header contentTypeHeader;
    private static final String apiHeaderKey = "X-CMC_PRO_API_KEY";
    private Response response;
    private double convertedValue;

    private static final String conversionUrlFormat = "/v2/tools/price-conversion?amount=%f&symbol=%s&convert=%s";

    @BeforeAll
    public static void beforeAllSetup() throws IOException {
        properties = new Properties();
        properties.load(new FileInputStream("src/test/resources/application.properties"));
    }

    @BeforeStep
    public void beforeStep(Scenario scenario) {
        this.scenario = scenario;
    }

    @Given("I have a client for Coinmarket api")
    public void i_have_a_client_for_coinmarket_api() {
        RestAssured.baseURI = properties.getProperty("api_url");
        apiKeyHeader = new Header(apiHeaderKey, properties.getProperty("api_key"));
        contentTypeHeader = new Header(HttpHeaders.ACCEPT,"application/json");
    }

    @When("I send a fiat currency conversion request for {double} from {string} to {string}")
    public void i_send_a_fiat_currency_conversion_request_for_from_to(Double amount, String from, String to) {
        sendConversionRequest(amount, from, to);
    }

    @Then("I get a successful response")
    public void i_get_a_successful_response() {
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusCode());
        scenario.log("received response : " + response.asPrettyString());
    }

    @Then("I get the converted {string} value")
    public void i_get_the_converted_value(String symbol) {
        ConversionQuoteDto conversionQuoteDto = response.body().as(ConversionQuoteDto.class, getMapper());
        convertedValue = conversionQuoteDto.getData()[0].getQuote().get(symbol).getPrice();
        scenario.log(String.format("converted value is %.12f %s", convertedValue, symbol));
    }

    @When("I send a crypto currency conversion request for converted value from {string} to {string}")
    public void i_send_a_crypto_currency_conversion_request_for_converted_value_from_to(String from, String to) {
        sendConversionRequest(convertedValue, from, to);
    }

    private void sendConversionRequest(double amount, String from, String to) {
        response = RestAssured
                .with()
                .header(apiKeyHeader)
                .header(contentTypeHeader)
                .request(Method.GET, String.format(conversionUrlFormat, amount, from, to));
    }
}
