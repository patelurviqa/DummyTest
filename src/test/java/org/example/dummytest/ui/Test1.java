package org.example.dummytest.ui;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.example.dummytest.ui.model.CryptoCurrencyInformation;
import org.example.dummytest.ui.pages.HomePage;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.List;
import java.util.Properties;

public class Test1 {

    Properties properties;

    @Test
    public void testLaunch() throws IOException, InterruptedException, ParseException {
        properties = new Properties();
        properties.load(new FileInputStream("src/test/resources/application.properties"));
        System.out.println("url is " + properties.get("url"));

        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        driver.navigate().to(properties.getProperty("url"));
        driver.manage().window().maximize();


        HomePage homePage = new HomePage(driver);
        homePage.rejectCookies();
        homePage.setPageRowSize(20);

        List<CryptoCurrencyInformation> currencyInformationList = homePage.getListedCurrencies();

        System.out.println(currencyInformationList);

        homePage.setSimpleFilter("Algorithm", "PoW");
        homePage.addCryptoCurrenciesFilter("Coins");
        homePage.toggleMineableFilter(true);
        homePage.addPriceFilter(100, 10000);
        homePage.applyAdditionalFilters();

        List<CryptoCurrencyInformation> filteredCurrencyInformationList = homePage.getListedCurrencies();

    }
}
