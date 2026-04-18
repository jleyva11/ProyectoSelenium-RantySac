package com.nopcommerce.pages;

import com.nopcommerce.utils.BasePage;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;
import java.util.stream.Collectors;

public class LoginPage extends BasePage {

    public LoginPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    // ================= LOCATORS =================

    @FindBy(id = "email")
    private WebElement emailInput;

    @FindBy(id = "password")
    private WebElement passwordInput;

    @FindBy(id = "loginButton")
    private WebElement loginBtn;

    @FindBy(css = "mat-error")
    private List<WebElement> errorMessages;

    @FindBy(css = "a[href='#/forgot-password']")
    private WebElement forgotPasswordLink;

    @FindBy(css = "div.error")
    private WebElement loginErrorMessage;

    @FindBy(css = "div#login-form form, form")
    private WebElement loginForm;
    private final By snackBarContainer = By.cssSelector(".mat-mdc-snack-bar-container");

    // ================= ACTIONS =================

    public void login(String email, String password) {

        // Asegura que estamos en login
        wait.until(ExpectedConditions.visibilityOf(emailInput));
        emailInput.clear();
        emailInput.sendKeys(email);
        passwordInput.clear();
        passwordInput.sendKeys(password);
        safeClick(loginBtn);
    }

    public void clickForgotPassword() {
        safeClick(forgotPasswordLink);
    }

    // ================= VALIDATIONS =================

    public boolean isLoginSuccess() {
        return wait.until(driver ->
                driver.getCurrentUrl().contains("search") ||
                        driver.getCurrentUrl().contains("products") ||
                        driver.getCurrentUrl().contains("shop")
        );
    }

    // ================= HELPERS =================

    public boolean isLoginErrorDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(loginErrorMessage))
                    .isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isRequiredValidationTriggered() {
        // Señal 1: aria-invalid="true"
        String emailInvalid = emailInput.getAttribute("aria-invalid");
        String passInvalid  = passwordInput.getAttribute("aria-invalid");

        boolean ariaInvalid =
                "true".equalsIgnoreCase(emailInvalid) ||
                        "true".equalsIgnoreCase(passInvalid);

        // Señal 2: el form está ng-invalid
        boolean formInvalid = false;
        try {
            String cls = loginForm.getAttribute("class");
            formInvalid = cls != null && cls.contains("ng-invalid");
        } catch (Exception ignored) { }

        // Señal 3: existe mat-error visible
        boolean matErrorVisible = !driver.findElements(
                By.cssSelector("mat-error, .mat-mdc-form-field-error")
        ).isEmpty();

        // Señal 4: botón disabled
        boolean btnDisabled = false;
        try {
            String disabled = loginBtn.getAttribute("disabled");
            btnDisabled = disabled != null;
        } catch (Exception ignored) { }

        return ariaInvalid || formInvalid || matErrorVisible || btnDisabled;
    }
    public void triggerRequiredValidation() {
        wait.until(ExpectedConditions.visibilityOf(emailInput));

        // Limpieza fuerte
        emailInput.click();
        emailInput.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
        passwordInput.click();
        passwordInput.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);

        // Disparar eventos
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(
                "arguments[0].dispatchEvent(new Event('input', {bubbles:true}));" +
                        "arguments[0].dispatchEvent(new Event('change', {bubbles:true}));" +
                        "arguments[0].blur();",
                emailInput
        );
        js.executeScript(
                "arguments[0].dispatchEvent(new Event('input', {bubbles:true}));" +
                        "arguments[0].dispatchEvent(new Event('change', {bubbles:true}));" +
                        "arguments[0].blur();",
                passwordInput
        );
    }

    public boolean isEmailHtml5Invalid() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        return (Boolean) js.executeScript("return !arguments[0].checkValidity();", emailInput);
    }

    public boolean isPasswordHtml5Invalid() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        return (Boolean) js.executeScript("return !arguments[0].checkValidity();", passwordInput);
    }

    public String getEmailDomValue() {
        return (String) ((JavascriptExecutor) driver).executeScript("return arguments[0].value;", emailInput);
    }

    public String getPasswordDomValue() {
        return (String) ((JavascriptExecutor) driver).executeScript("return arguments[0].value;", passwordInput);
    }

    public String getSnackBarText() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(snackBarContainer))
                    .getText().trim();
        } catch (TimeoutException e) {
            return "";
        }
    }
    public boolean isLoginErrorVisible() {
        String text = getSnackBarText().toLowerCase();

        if (!text.isBlank() && (text.contains("invalid") || text.contains("email") || text.contains("password"))) {
            return true;
        }

        return driver.getCurrentUrl().contains("/login") && loginBtn.isEnabled();
    }

}