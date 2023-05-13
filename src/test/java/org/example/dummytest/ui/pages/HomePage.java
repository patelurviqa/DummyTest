package org.example.dummytest.ui.pages;

import org.example.dummytest.ui.model.CryptoCurrencyInformation;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.nio.file.WatchEvent;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

public class HomePage {

    private WebDriver driver;

    //properties for filter
    private String pageRowSizeDropdownClass = "table-control-page-sizer";
    private String pageSizeOptionsListId = "tippy-1";
    //properties for table
    private String currencyTableClass = "cmc-table";

    //properties for filters
    private String filterButtonClass = "table-control-filter";
    private String filterRowClass = "fsDYiB";


    private final String toggleButtonOffClass = "sc-f3593cc1-1 jZWMqF switch-label";
    private final String toggleButtonOnClass = "sc-f3593cc1-1 gFMJer switch-label";

    private final String rejectCookiesButtonId = "onetrust-reject-all-handler";
    private Map<String, WebElement> filtersMap;

    public HomePage(WebDriver driver) {
        this.driver = driver;
    }

    public void setPageRowSize(int size) throws InterruptedException {
        WebElement pageSizeDropDown = driver.findElement(By.className(pageRowSizeDropdownClass)).findElement(By.xpath("./div"));
        pageSizeDropDown.click();
        WebDriverWait webDriverWait = new WebDriverWait(driver, 2);
        webDriverWait.until(ExpectedConditions.elementToBeClickable(By.id(pageSizeOptionsListId)));
        WebElement pageSizeOptionsList = driver.findElement(By.id(pageSizeOptionsListId));
        List<WebElement> options =  pageSizeOptionsList.findElements(By.xpath(String.format(".//button[contains(text(), '%d')]", size)));
        if (options.isEmpty()) {
            throw new IllegalArgumentException("No option found for page size of : " + size);
        }
        WebElement option = options.get(0);
        webDriverWait.until(d -> option.isDisplayed());
      //  webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath(String.format(".//button[contains(text(), '%d')]", size))));
        option.sendKeys(Keys.ENTER);
        webDriverWait.until(ExpectedConditions.invisibilityOfElementLocated(By.id(pageSizeOptionsListId)));
    }

    private void scrollPage(int pixels) {
        System.out.println("scrolling  by " + pixels);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(String.format("window.scrollBy(0,%d)", pixels));
    }
    public List<CryptoCurrencyInformation> getListedCurrencies() throws ParseException {
//        JavascriptExecutor js = (JavascriptExecutor) driver;
//        js.executeScript("window.scrollBy(0,1500)");
//
//
//        List<CryptoCurrencyInformation> cryptoCurrencyInformationList = new ArrayList<>();
//        List<WebElement> tableRows = driver.findElement(By.className(currencyTableClass)).findElements(By.xpath("./tbody/tr"));

        WebElement table = driver.findElement(By.className(currencyTableClass));
        int tableHeight = table.getSize().getHeight();
        scrollPage(tableHeight);

        List<CryptoCurrencyInformation> cryptoCurrencyInformationList = new ArrayList<>();
        List<WebElement> tableRows = driver.findElement(By.className(currencyTableClass)).findElements(By.xpath("./tbody/tr"));

        NumberFormat nfDollars = NumberFormat.getCurrencyInstance(Locale.US);
        NumberFormat nfPercentage = NumberFormat.getPercentInstance(Locale.US);
        NumberFormat nfCommas = NumberFormat.getInstance(Locale.US);

        for (WebElement row : tableRows) {
            List<WebElement> columns = row.findElements(By.tagName("td"));
            CryptoCurrencyInformation information = new CryptoCurrencyInformation();
            information.setRank(Integer.parseInt(columns.get(1).getText()));
            information.setName(columns.get(2).findElement(By.xpath("./div/a/div/div/p")).getText());
            information.setSymbol(columns.get(2).findElement(By.className("coin-item-symbol")).getText());
            information.setPrice(nfDollars.parse(columns.get(3).getText()).doubleValue());
            information.setHourlyChange(nfPercentage.parse(columns.get(4).getText()).doubleValue());
            information.setDailyChange(nfPercentage.parse(columns.get(5).getText()).doubleValue());
            information.setWeeklyChange(nfPercentage.parse(columns.get(6).getText()).doubleValue());
            information.setMarketCap(nfDollars.parse(columns.get(7).getText()).longValue());
            information.setVolumeUsd(nfDollars.parse(columns.get(8).findElement(By.xpath("./div/a/p")).getText()).longValue());
            String volumeInUnitsText = columns.get(8).findElement(By.xpath("./div/div/p")).getText();
            information.setVolumeUnits(nfCommas.parse(volumeInUnitsText.split(" ")[0]).longValue());
            String circulatingSupplyText = columns.get(9).findElement(By.xpath("./div/div/p")).getText();
            information.setCirculatingSupply(nfCommas.parse(circulatingSupplyText.split(" ")[0]).longValue());
            cryptoCurrencyInformationList.add(information);
            System.out.println(information);
        }

//        js.executeScript("window.scrollBy(0,-1500)");
        scrollPage(tableHeight * -1);
        return cryptoCurrencyInformationList;
    }

    public void showFilters() {
        if (driver.findElements(By.className(filterRowClass)).isEmpty()) {
            driver.findElement(By.className("table-control-area")).findElement(By.xpath("./div[2]/button")).click();
            WebDriverWait wait = new WebDriverWait(driver, 5);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.className(filterRowClass)));
            //driver.findElement(By.className(filterButtonClass)).click();
            //WebElement filterButton = driver.findElement(By.xpath(String.format("//button[contains(@class,'%s')]", filterButtonClass)));
//            WebDriverWait wait = new WebDriverWait(driver, 5);
//            wait.until(d -> filterButton.isEnabled());
 //           filterButton.sendKeys(Keys.RETURN);
        }

        if (filtersMap == null) {
            initFiltersMap();
        }
    }

    private void initFiltersMap() {
        filtersMap = new HashMap<>();
        List<WebElement> filterElements = driver.findElement(By.className(filterRowClass)).findElements(By.tagName("li"));
        filtersMap.put("Category", filterElements.get(0));
        filtersMap.put("Algorithm", filterElements.get(1));
        filtersMap.put("Platform", filterElements.get(2));
        filtersMap.put("Industry", filterElements.get(3));
        filtersMap.put("Add Filter", filterElements.get(4));
        if (filterElements.size() > 5) {
            filtersMap.put("Clear Filters", filterElements.get(5));
        }
    }

    public void setSimpleFilter(String filterName, String filterValue) {
        showFilters();
        WebElement filter = filtersMap.get(filterName);
        clearFilterIfExists(filter);
        filter.click();
        WebDriverWait wait = new WebDriverWait(driver, 2);
        wait.until(d -> filter.findElement(By.xpath("./div/span")).getAttribute("aria-expanded").equals("true"));
//        wait.until(ExpectedConditions.attributeToBe(By.xpath("./div/span"), "aria-expanded", "true"));
        filter.findElement(By.xpath(String.format(".//li[text()='%s']", filterValue))).click();
    }

    private void clearFilterIfExists(WebElement filter) {
        if (!filter.findElements(By.xpath("./button")).isEmpty()) {
            filter.click();
        }
    }

    private WebElement openAdditionalFiltersPopup() {
        if (driver.findElements(By.className("filter-area")).isEmpty()) {
            filtersMap.get("Add Filter").click();
        }
        return driver.findElement(By.className("filter-area"));
    }

    public void applyAdditionalFilters() {
        WebElement filterPopup = openAdditionalFiltersPopup();
        filterPopup.findElement(By.xpath("./following-sibling::div/button")).click();
    }

    public void addCryptoCurrenciesFilter(String option) {
        WebElement filtersPopup = openAdditionalFiltersPopup();
        WebElement cryptocurrenciesFilter = filtersPopup.findElement(By.xpath("./div[position()=1]"));
        cryptocurrenciesFilter.click();
        WebElement cryptoFilterControls = filtersPopup.findElement(By.xpath("./div[@data-qa-id='range-filter-crypto']"));
        WebElement optionToSelect = cryptoFilterControls.findElement(By.xpath(String.format(".//button[contains(text(), '%s')]", option)));
        optionToSelect.click();
    }

    public void toggleMineableFilter(boolean value) {
        WebElement filtersPopup = openAdditionalFiltersPopup();
        WebElement mineableFilter = filtersPopup.findElement(By.xpath("./div[position()=7]"));
        WebElement mineableToggleButton = mineableFilter.findElement(By.xpath(".//label[@id='mineable']"));
        setAdditionalFilterToggleButton(mineableToggleButton, value);
    }

    public void addPriceFilter(double min, double max) throws InterruptedException {
        WebElement filtersPopup = openAdditionalFiltersPopup();
        WebElement priceFilter = filtersPopup.findElement(By.xpath("./div[position()=3]/button"));
        Thread.sleep(1000);
        priceFilter.sendKeys(Keys.RETURN);
        //priceFilter.click();

        WebDriverWait wait = new WebDriverWait(driver, 5);
        wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(filtersPopup, By.xpath("./div[@data-qa-id='range-filter-price']")));
        WebElement priceFilterControls = filtersPopup.findElement(By.xpath("./div[@data-qa-id='range-filter-price']"));
        setRangeFilter(priceFilterControls, String.valueOf(min), String.valueOf(max));
    }

    public void rejectCookies() {
        WebDriverWait webDriverWait = new WebDriverWait(driver, 5);
        webDriverWait.until(ExpectedConditions.elementToBeClickable(By.id(rejectCookiesButtonId)));
        driver.findElement(By.id(rejectCookiesButtonId)).click();
    }

    private void setAdditionalFilterToggleButton(WebElement toggleButton, boolean value) {
        String toggleButtonClass = toggleButton.getAttribute("class");
        if ((value && toggleButtonClass.equals(toggleButtonOffClass)) ||
        (!value && toggleButtonClass.equals(toggleButtonOnClass))) {
            toggleButton.click();
        }
    }

    private void setRangeFilter(WebElement filterControl, String min, String max) {
        filterControl.findElement(By.xpath(".//input[@data-qa-id='range-filter-input-min']")).sendKeys(min);
        filterControl.findElement(By.xpath(".//input[@data-qa-id='range-filter-input-max']")).sendKeys(max);
        filterControl.findElement(By.xpath(".//button[@data-qa-id='filter-dd-button-apply']")).click();
    }


}