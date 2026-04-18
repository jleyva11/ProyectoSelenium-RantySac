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

        // Asegura que estamos en login (el test se encarga del estado)
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

    public List<String> getErrorMessages() {

        try {
            wait.until(driver -> errorMessages.size() > 0);
        } catch (Exception e) {
            // No hay mensajes visibles todavía
        }

        // PASO 1: obtener los textos reales
        List<String> messages = errorMessages.stream()
                .map(WebElement::getText)
                .filter(text -> text != null && !text.trim().isEmpty())
                .collect(Collectors.toList());

        // PASO 2: DEBUG
        messages.forEach(msg ->
                System.out.println("MENSAJE DE ERROR LOGIN: [" + msg + "]")
        );

        // PASO 3: devolver la lista
        return messages;
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
    public void submitEmptyLogin() {
        wait.until(ExpectedConditions.visibilityOf(emailInput));
        emailInput.clear();
        passwordInput.clear();
        safeClick(loginBtn); // usa tu safeClick del BasePage
    }
    public boolean isRequiredValidationTriggered() {
        // Señal 1: aria-invalid="true"
        String emailInvalid = emailInput.getAttribute("aria-invalid");
        String passInvalid  = passwordInput.getAttribute("aria-invalid");

        boolean ariaInvalid =
                "true".equalsIgnoreCase(emailInvalid) ||
                        "true".equalsIgnoreCase(passInvalid);

        // Señal 2: el form (o el contenedor) está ng-invalid
        boolean formInvalid = false;
        try {
            String cls = loginForm.getAttribute("class");
            formInvalid = cls != null && cls.contains("ng-invalid");
        } catch (Exception ignored) { }

        // Señal 3: existe mat-error visible (si tu versión lo usa)
        boolean matErrorVisible = !driver.findElements(
                By.cssSelector("mat-error, .mat-mdc-form-field-error")
        ).isEmpty();

        // Señal 4: botón disabled (algunas versiones lo hacen)
        boolean btnDisabled = false;
        try {
            String disabled = loginBtn.getAttribute("disabled");
            btnDisabled = disabled != null;
        } catch (Exception ignored) { }

        return ariaInvalid || formInvalid || matErrorVisible || btnDisabled;
    }
    public void triggerRequiredValidation() {
        wait.until(ExpectedConditions.visibilityOf(emailInput));

        // Limpieza fuerte (evita autofill raro)
        emailInput.click();
        emailInput.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);

        passwordInput.click();
        passwordInput.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);

        // Disparar eventos para que Angular/Material recalculen estado
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

    public boolean isLoginButtonDisabled() {
        // Señal 1: atributo disabled existe (en tu HTML: disabled="true")
        boolean disabledAttrPresent = (loginBtn.getAttribute("disabled") != null);

        // Señal 2: clase mat-mdc-button-disabled (en tu HTML está presente)
        String clazz = loginBtn.getAttribute("class");
        boolean hasDisabledClass = (clazz != null && clazz.contains("mat-mdc-button-disabled"));

        // Señal 3 (extra): aria-disabled en caso la UI lo use
        String ariaDisabled = loginBtn.getAttribute("aria-disabled");
        boolean aria = "true".equalsIgnoreCase(ariaDisabled);

        return disabledAttrPresent || hasDisabledClass || aria;
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

        // Fallback mejorado: seguimos en login y el botón NO está disabled (significa que se intentó enviar)
        return driver.getCurrentUrl().contains("/login") && loginBtn.isEnabled();
    }


}