package com.nopcommerce.pages;

import com.nopcommerce.utils.BasePage;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class HomePage extends BasePage {

    public HomePage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    // ================= LOCATORS =================

    @FindBy(id = "navbarAccount")
    private WebElement accountBtn;

    @FindBy(id = "navbarLoginButton")
    private WebElement loginBtn;

    @FindBy(css = "button[aria-label='Close']")
    private WebElement closeBannerBtn;

    @FindBy(css = "[aria-label='Logout']")
    private WebElement logoutButton;

    @FindBy(css = "button[aria-label='Show the shopping cart']")
    private WebElement basketButton;

    @FindBy(css = "button.close-dialog[aria-label='Close Welcome Banner']")
    private WebElement dismissWelcomeButton;

    @FindBy(css = "button[aria-label='Me want it!']")
    private WebElement acceptCookiesButton;

    @FindBy(css = ".mat-drawer-backdrop.mat-drawer-shown")
    private WebElement drawerBackdrop;

    // ================= ACTIONS =================

    public void dismissBanner() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(closeBannerBtn)).click();
        } catch (Exception e) {
        }
    }

    public void goToLogin() {
        try {
            if (accountBtn.isDisplayed()) {
                safeClick(accountBtn);
                safeClick(loginBtn);
            }
        } catch (Exception e) {
            System.out.println("Usuario ya autenticado, no se requiere login");
        }
    }

    public void forceLogoutIfLoggedIn() {
        try {
            // Si estamos en páginas típicas post-login
            if (driver.getCurrentUrl().contains("/search")
                    || driver.getCurrentUrl().contains("/basket")
                    || driver.getCurrentUrl().contains("/profile")) {

                safeClick(logoutButton);

                // Esperar volver al login
                wait.until(driver ->
                        driver.getCurrentUrl().contains("/login"));
            }
        } catch (Exception e) {
            // Si no hay sesión, no hacemos nada
        }
    }

    public void openBasket() {
        safeClick(basketButton);
        wait.until(d -> d.getCurrentUrl().contains("/basket"));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("mat-table")));
    }

    public void dismissInitialPopups() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));

        try {
            shortWait.until(ExpectedConditions.elementToBeClickable(dismissWelcomeButton)).click();
        } catch (TimeoutException ignored) {
        }

        try {
            shortWait.until(ExpectedConditions.elementToBeClickable(acceptCookiesButton)).click();
        } catch (TimeoutException ignored) {
        }
    }

    public void closeSideMenuIfOpen() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(2));

        try {
            // Si existe el backdrop, el menú está abierto
            if (!driver.findElements(By.cssSelector(".mat-drawer-backdrop.mat-drawer-shown")).isEmpty()) {
                // Cerrar haciendo click en el backdrop
                shortWait.until(ExpectedConditions.elementToBeClickable(drawerBackdrop)).click();

                // Confirmar que desapareció
                shortWait.until(ExpectedConditions.invisibilityOfElementLocated(
                        By.cssSelector(".mat-drawer-backdrop.mat-drawer-shown")
                ));
            }
        } catch (Exception ignored) {
            // Fallback: ESC suele cerrar
            try {
                new Actions(driver).sendKeys(Keys.ESCAPE).perform();
            } catch (Exception ignored2) {}
        }
    }

}