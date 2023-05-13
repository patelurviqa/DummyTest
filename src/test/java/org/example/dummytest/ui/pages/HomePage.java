package org.example.dummytest.ui.pages;

import org.example.dummytest.ui.model.CryptoCurrencyInformation;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

public class HomePage {

    private WebDriver driver;

    //properties for page size
    private static final String pageRowSizeDropdownClass = "table-control-page-sizer";
    private static final String pageSizeOptionsListId = "tippy-1";
    private static final String pageSizeOptionsXpathFormat = ".//button[contains(text(), '%d')]";

    //properties for table
    private static final String currencyTableClass = "cmc-table";
    private static final String tableRowsInTableXpath = "./tbody/tr";
    private static final String nameXpathInTableCell = "./div/a/div/div/p";
    private static final String symbolClassInCell = "coin-item-symbol";
    private static final String volumeDollarsXpathInTableCell = "./div/a/p";
    private static final String volumeUnitsXpathInTableCell = "./div/div/p";
    private static final String circulatingSupplyXpathInTableCell = "./div/div/p";

    //properties for filters
    private static final String filterRowClass = "fsDYiB";
    private static final String tableControlAreaClass = "table-control-area";
    private static final String filterButtonXpathInTableControlArea = "./div[2]/button";
    private static final String filterExpansionCheckElementXpath = "./div/span";
    private static final String filterExpansionCheckElementAttribute = "aria-expanded";
    private static final String simpleFilterOptionXpathFormat = ".//li[text()='%s']";
    private static final String filterAlreadySetXpath = "./button";
    private static final String filterAreaClass = "filter-area";
    private static final String applyFiltersXpath = "./following-sibling::div/button";

    //cryptocurrency filter properties
    private static final String cryptocurrenciesFilterXpath = "./div[position()=1]";
    private static final String cryptoFilterControlsXpath = "./div[@data-qa-id='range-filter-crypto']";
    private static final String cryptoFilterOptionXpathFormat = ".//button[contains(text(), '%s')]";

    //mineable filter properties
    private static final String mineableFilterXpath = "./div[position()=7]";
    private static final String mineableToggleButtonXpath = ".//label[@id='mineable']";

    //price filter properties
    private static final String priceFilterXpath = "./div[position()=3]/button";
    private static final String priceFilterControlsXpath = "./div[@data-qa-id='range-filter-price']";

    //generic toggle filter properties
    private static final String toggleButtonOffClass = "sc-f3593cc1-1 jZWMqF switch-label";
    private static final String toggleButtonOnClass = "sc-f3593cc1-1 gFMJer switch-label";

    //generic range filter properties
    private static final String rangeFilterMinXpath = ".//input[@data-qa-id='range-filter-input-min']";
    private static final String rangeFilterMaxXpath = ".//input[@data-qa-id='range-filter-input-max']";
    private static final String rangeFilterApplyXpath = ".//button[@data-qa-id='filter-dd-button-apply']";
    private static final  String rejectCookiesButtonId = "onetrust-reject-all-handler";
    private Map<String, WebElement> filtersMap;

    public HomePage(WebDriver driver) {
        this.driver = driver;
    }

    public void setPageRowSize(int size) throws InterruptedException {
        WebElement pageSizeDropDown = driver.findElement(By.className(pageRowSizeDropdownClass))
                .findElement(By.xpath("./div"));
        pageSizeDropDown.click();
        WebDriverWait webDriverWait = new WebDriverWait(driver, 2);
        webDriverWait.until(ExpectedConditions.elementToBeClickable(By.id(pageSizeOptionsListId)));
        WebElement pageSizeOptionsList = driver.findElement(By.id(pageSizeOptionsListId));
        List<WebElement> options =  pageSizeOptionsList
                .findElements(By.xpath(String.format(pageSizeOptionsXpathFormat, size)));
        if (options.isEmpty()) {
            throw new IllegalArgumentException("No option found for page size of : " + size);
        }
        WebElement option = options.get(0);
        webDriverWait.until(d -> option.isDisplayed());
        option.sendKeys(Keys.ENTER);
        webDriverWait.until(ExpectedConditions.invisibilityOfElementLocated(By.id(pageSizeOptionsListId)));
    }

    public List<CryptoCurrencyInformation> getListedCurrencies() throws ParseException {
       List<WebElement> tableRows1 = driver.findElement(By.className(currencyTableClass))
                .findElements(By.xpath(tableRowsInTableXpath));
        Actions actions = new Actions(driver);
        actions.moveToElement(tableRows1.get(tableRows1.size()-1)).perform();

        List<CryptoCurrencyInformation> cryptoCurrencyInformationList = new ArrayList<>();
        List<WebElement> tableRows = driver.findElement(By.className(currencyTableClass))
                .findElements(By.xpath(tableRowsInTableXpath));

        NumberFormat nfDollars = NumberFormat.getCurrencyInstance(Locale.US);
        NumberFormat nfPercentage = NumberFormat.getPercentInstance(Locale.US);
        NumberFormat nfCommas = NumberFormat.getInstance(Locale.US);

        for (WebElement row : tableRows) {
            List<WebElement> columns = row.findElements(By.tagName("td"));
            CryptoCurrencyInformation information = new CryptoCurrencyInformation();
            information.setRank(Integer.parseInt(columns.get(1).getText()));
            information.setName(columns.get(2).findElement(By.xpath(nameXpathInTableCell)).getText());
            information.setSymbol(columns.get(2).findElement(By.className(symbolClassInCell)).getText());
            information.setPrice(nfDollars.parse(columns.get(3).getText()).doubleValue());
            information.setHourlyChange(nfPercentage.parse(columns.get(4).getText()).doubleValue());
            information.setDailyChange(nfPercentage.parse(columns.get(5).getText()).doubleValue());
            information.setWeeklyChange(nfPercentage.parse(columns.get(6).getText()).doubleValue());
            information.setMarketCap(nfDollars.parse(columns.get(7).getText()).longValue());
            information.setVolumeUsd(nfDollars.parse(columns.get(8)
                    .findElement(By.xpath(volumeDollarsXpathInTableCell)).getText()).longValue());
            String volumeInUnitsText = columns.get(8).findElement(By.xpath(volumeUnitsXpathInTableCell)).getText();
            information.setVolumeUnits(nfCommas.parse(volumeInUnitsText.split(" ")[0]).longValue());
            String circulatingSupplyText = columns.get(9)
                    .findElement(By.xpath(circulatingSupplyXpathInTableCell)).getText();
            information.setCirculatingSupply(nfCommas.parse(circulatingSupplyText.split(" ")[0]).longValue());
            cryptoCurrencyInformationList.add(information);
            System.out.println(information);
        }

        actions.moveToElement(tableRows.get(0)).perform();
        return cryptoCurrencyInformationList;
    }

    public void showFilters() {
        if (driver.findElements(By.className(filterRowClass)).isEmpty()) {
            driver.findElement(By.className(tableControlAreaClass))
                    .findElement(By.xpath(filterButtonXpathInTableControlArea)).click();
            WebDriverWait wait = new WebDriverWait(driver, 5);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.className(filterRowClass)));
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
        wait.until(d -> filter.findElement(By.xpath(filterExpansionCheckElementXpath))
                .getAttribute(filterExpansionCheckElementAttribute).equals("true"));
        filter.findElement(By.xpath(String.format(simpleFilterOptionXpathFormat, filterValue))).click();
    }

    private void clearFilterIfExists(WebElement filter) {
        if (!filter.findElements(By.xpath(filterAlreadySetXpath)).isEmpty()) {
            filter.click();
        }
    }

    private WebElement openAdditionalFiltersPopup() {
        if (driver.findElements(By.className(filterAreaClass)).isEmpty()) {
            filtersMap.get("Add Filter").click();
        }
        return driver.findElement(By.className(filterAreaClass));
    }

    public void applyAdditionalFilters() {
        WebElement filterPopup = openAdditionalFiltersPopup();
        filterPopup.findElement(By.xpath(applyFiltersXpath)).click();
    }

    public void addCryptoCurrenciesFilter(String option) throws InterruptedException {
        WebElement filtersPopup = openAdditionalFiltersPopup();
        WebElement cryptocurrenciesFilter = filtersPopup.findElement(By.xpath(cryptocurrenciesFilterXpath));
        Thread.sleep(1000);
        cryptocurrenciesFilter.click();
        WebElement cryptoFilterControls = filtersPopup.findElement(By.xpath(cryptoFilterControlsXpath));
        WebElement optionToSelect = cryptoFilterControls.findElement(
                By.xpath(String.format(cryptoFilterOptionXpathFormat, option)));
        optionToSelect.click();
    }

    public void toggleMineableFilter(boolean value) {
        WebElement filtersPopup = openAdditionalFiltersPopup();
        WebElement mineableFilter = filtersPopup.findElement(By.xpath(mineableFilterXpath));
        WebElement mineableToggleButton = mineableFilter.findElement(By.xpath(mineableToggleButtonXpath));
        setAdditionalFilterToggleButton(mineableToggleButton, value);
    }

    public void addPriceFilter(double min, double max) throws InterruptedException {
        WebElement filtersPopup = openAdditionalFiltersPopup();
        WebElement priceFilter = filtersPopup.findElement(By.xpath(priceFilterXpath));
        Thread.sleep(1000);
        priceFilter.click();

        WebDriverWait wait = new WebDriverWait(driver, 5);
        wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(filtersPopup, By.xpath(priceFilterControlsXpath)));
        WebElement priceFilterControls = filtersPopup.findElement(By.xpath(priceFilterControlsXpath));
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
        filterControl.findElement(By.xpath(rangeFilterMinXpath)).sendKeys(min);
        filterControl.findElement(By.xpath(rangeFilterMaxXpath)).sendKeys(max);
        filterControl.findElement(By.xpath(rangeFilterApplyXpath)).click();
    }
}