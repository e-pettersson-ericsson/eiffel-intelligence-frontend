package com.ericsson.ei.frontend.pageobjects;

import java.io.IOException;

import org.apache.http.impl.client.CloseableHttpClient;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SubscriptionPage extends PageBaseClass {
    private static final String ROUTE = "/#subscriptions";

    public SubscriptionPage(CloseableHttpClient mockedHttpClient, FirefoxDriver driver, String baseUrl)
            throws IOException {
        super(mockedHttpClient, driver, baseUrl);
    }

    public SubscriptionPage loadPage() {
        driver.get(baseUrl + ROUTE);
        waitForJQueryToLoad();
        return this;
    }

    public boolean presenceOfHeader(String loc) {
        try {
            new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.elementToBeClickable(By.id(loc)));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void clickAddSubscription() {
        WebElement addSubscriptionBtn = new WebDriverWait(driver, TIMEOUT_TIMER).until(
                ExpectedConditions.elementToBeClickable(By.id("addSubscription")));
        addSubscriptionBtn.click();
    }

    public void clickFormsCancelBtn() {
        new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.elementToBeClickable(By.id("btnFormCancel")));
        WebElement cancelBtn = driver.findElement(By.id("btnFormCancel"));
        cancelBtn.click();
    }

    public void clickBulkDelete() throws IOException {
        new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.elementToBeClickable(By.id("check-all")));
        WebElement checkbox = driver.findElement(By.id("check-all"));
        checkbox.click();

        new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.elementToBeClickable(By.id("bulkDelete")));
        WebElement bulkDeleteBtn = driver.findElement(By.id("bulkDelete"));
        bulkDeleteBtn.click();
        // Click confirm button to confirm delete
        new WebDriverWait(driver, TIMEOUT_TIMER).until(
                ExpectedConditions.elementToBeClickable(By.cssSelector(".confirm-delete .modal-footer .btn-danger")));
        WebElement confirmBtn = driver.findElement(By.cssSelector(".confirm-delete .modal-footer .btn-danger"));
        confirmBtn.click();
    }

    public void clickReload() {
        new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.elementToBeClickable(By.id("reloadButton")));
        WebElement reloadBtn = driver.findElement(By.id("reloadButton"));
        reloadBtn.click();
    }

    public void clickFormsSaveBtn() throws IOException {
        new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.elementToBeClickable(By.id("btnSave")));
        WebElement saveBtn = driver.findElement(By.id("btnSave"));
        saveBtn.click();
    }

    public void clickKVbtn(String loc) {
        new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.elementToBeClickable(By.id(loc)));
        WebElement kvBtn = driver.findElement(By.id(loc));
        kvBtn.click();
    }

    public void selectDropdown(String loc, String value) {
        new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.elementToBeClickable(By.id(loc)));
        WebElement selectEle = driver.findElement(By.id(loc));
        Select dropdown = new Select(selectEle);
        dropdown.selectByVisibleText(value);
    }

    public boolean isRadioCheckboxSelected(String id) {
        new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.elementToBeClickable(By.id(id)));
        WebElement checkbox = driver.findElement(By.id(id));
        boolean radioBtnIsSelected = checkbox.isSelected();
        return radioBtnIsSelected;
    }

    public boolean isCheckboxSelected(String id) {
        new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.presenceOfElementLocated(By.id(id)));
        WebElement checkbox = driver.findElement(By.id(id));
        boolean isSelected = checkbox.isSelected();
        return isSelected;
    }

    public void clickSpanAroundCheckbox(String id, String spanId) {
        new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.presenceOfElementLocated(By.id(id)));
        new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.presenceOfElementLocated(By.id(spanId)));

        WebElement checkbox = driver.findElement(By.id(id));
        WebElement span = driver.findElement(By.id(spanId));

        span.click();

        new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.elementSelectionStateToBe(checkbox, true));
    }

    public String getValueFromElement(String id) {
        new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.elementToBeClickable(By.id(id)));
        WebElement metaTxt = driver.findElement(By.id(id));
        return metaTxt.getAttribute("value");
    }

    public void addFieldValue(String loc, String value) {
        new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.elementToBeClickable(By.id(loc)));
        WebElement ele = driver.findElement(By.id(loc));
        ele.clear();
        ele.sendKeys(value);
    }

    public void clickUploadSubscriptionFunctionality(String filePath) throws IOException {
        new WebDriverWait(driver, TIMEOUT_TIMER).until(
                ExpectedConditions.presenceOfElementLocated(By.id("upload_sub")));
        WebElement uploadInputField = driver.findElement(By.id("upload_sub"));
        uploadInputField.sendKeys(filePath);
    }

    public boolean presenceOfClickGetTemplateButton() {
        try {
            driver.findElement(By.id("getTemplateButton"));
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public void clickGetTemplate() {
        new WebDriverWait(driver, TIMEOUT_TIMER).until(
                ExpectedConditions.elementToBeClickable(By.id("getTemplateButton")));
        WebElement getTemplateButton = driver.findElement(By.id("getTemplateButton"));
        getTemplateButton.click();
    }

    public void clickFormCloseBtn() {
        new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.elementToBeClickable(By.className("close")));
        WebElement viewBtn = driver.findElement(By.className("close"));
        viewBtn.click();
    }

    public void clickAddConditionBtn() {
        new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.elementToBeClickable(By.id("addCondition")));
        WebElement viewBtn = driver.findElement(By.id("addCondition"));
        viewBtn.click();
    }

    public void clickAddRequirementBtn() {
        new WebDriverWait(driver, TIMEOUT_TIMER).until(
                ExpectedConditions.elementToBeClickable(By.id("addRequirement")));
        WebElement viewBtn = driver.findElement(By.id("addRequirement"));
        viewBtn.click();
    }

    public String getSubscriptionNameFromSubscription() {
        new WebDriverWait(driver, TIMEOUT_TIMER).until(
                ExpectedConditions.elementToBeClickable(By.xpath("//tr[@class='odd']/td[3]")));
        WebElement subscriptionNameElement = driver.findElement(By.xpath("//tr[@class='odd']/td[3]"));
        return subscriptionNameElement.getText();
    }

    public boolean expandButtonExist(String XPath) {
        try {
            new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.elementToBeClickable(By.xpath(XPath)));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean buttonExistByXPath(String XPath) {
        // The row indicates weather or not the del / view and edit buttons has moved
        // down to
        // next row.
        String findInRow;
        try {
            findInRow = "[2]";
            new WebDriverWait(driver, TIMEOUT_TIMER).until(
                    ExpectedConditions.elementToBeClickable(By.xpath(XPath + findInRow)));
            return true;
        } catch (Exception e) {
        }
        try {
            findInRow = "[1]";
            new WebDriverWait(driver, TIMEOUT_TIMER).until(
                    ExpectedConditions.elementToBeClickable(By.xpath(XPath + findInRow)));
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    public boolean buttonDisabledByXPath(String XPath) {
        try {
            new WebDriverWait(driver, TIMEOUT_TIMER).until(
                    ExpectedConditions.presenceOfElementLocated(By.xpath(XPath)));
            WebElement element = driver.findElement(By.xpath(XPath));
            return !element.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickReloadLDAP() throws IOException {
        WebElement reloadBtn = new WebDriverWait(driver, TIMEOUT_TIMER).until(
                ExpectedConditions.elementToBeClickable(By.id("reloadButton")));
        reloadBtn.click();
    }

    public boolean textExistsInTable(String txt) {
        try {
            new WebDriverWait(driver, TIMEOUT_TIMER).until(
                    ExpectedConditions.elementToBeClickable(By.xpath("//tr[td[contains(.,'" + txt + "')]]")));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean textDoesNotExistsInTable(String txt) {
        try {
            new WebDriverWait(driver, TIMEOUT_TIMER).until(
                    ExpectedConditions.invisibilityOfElementLocated(By.xpath("//tr[td[contains(.,'" + txt + "')]]")));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean clickExpandButtonByXPath(String loc) {
        try {
            if (expandButtonExist(loc)) {
                new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.elementToBeClickable(By.xpath(loc)));
                driver.findElement(By.xpath(loc))
                      .click();
            } else {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public int countElements(String id) {
        return driver.findElements(By.id(id))
                     .size();
    }

    public void clickViewButtonByXPath(String XPath) {
        // The row indicates weather or not the del / view and edit buttons has moved
        // down to
        // next row.
        try {
            String Xpath2 = XPath + "[2]";
            new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.elementToBeClickable(By.xpath(Xpath2)));
            driver.findElement(By.xpath(Xpath2))
                  .click();
            return;
        } catch (Exception e) {
        }
        try {
            String Xpath1 = XPath + "[1]";
            new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.elementToBeClickable(By.xpath(Xpath1)));
            driver.findElement(By.xpath(Xpath1))
                  .click();
            return;
        } catch (Exception e) {
        }
    }

    public boolean noPresenceOfHeader(String loc) {
        try {
            new WebDriverWait(driver, TIMEOUT_TIMER).until(ExpectedConditions.invisibilityOfElementLocated(By.id(loc)));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

}
