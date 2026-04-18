package com.nopcommerce.utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class BasePage {

    protected WebDriver driver;
    protected WebDriverWait wait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }
    protected void safeClick(WebElement element) {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));

        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                shortWait.until(ExpectedConditions.invisibilityOfElementLocated(
                        By.cssSelector(".cdk-overlay-backdrop, .cdk-overlay-backdrop-showing, .mat-drawer-backdrop.mat-drawer-shown")
                ));

                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].scrollIntoView({block:'center', inline:'center'});",
                        element
                );

                wait.until(ExpectedConditions.elementToBeClickable(element)).click();
                return;

            } catch (StaleElementReferenceException e) {
                // reintento
            } catch (TimeoutException | ElementClickInterceptedException e) {
                try {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
                    return;
                } catch (Exception ignored) { }
            }
        }
        throw new RuntimeException("safeClick(WebElement) falló tras reintentos: no clickeable o stale.");
    }

    protected void safeClick(By locator) {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));

        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                shortWait.until(ExpectedConditions.invisibilityOfElementLocated(
                        By.cssSelector(".cdk-overlay-backdrop, .cdk-overlay-backdrop-showing, .mat-drawer-backdrop.mat-drawer-shown")
                ));

                WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));

                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].scrollIntoView({block:'center', inline:'center'});",
                        element
                );

                wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
                return;

            } catch (StaleElementReferenceException e) {
                // reintento (se relocaliza)
            } catch (TimeoutException | ElementClickInterceptedException e) {
                try {
                    WebElement element = driver.findElement(locator);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
                    return;
                } catch (Exception ignored) { }
            }
        }
        throw new RuntimeException("safeClick(By) falló tras reintentos: " + locator);
    }
}