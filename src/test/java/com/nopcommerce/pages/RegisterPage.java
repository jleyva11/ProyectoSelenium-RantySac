package com.nopcommerce.pages;

import com.nopcommerce.utils.BasePage;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;
import java.util.stream.Collectors;

public class RegisterPage extends BasePage {

    public RegisterPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    // ================= LOCATORS =================

    @FindBy(id = "navbarAccount")
    private WebElement accountBtn;

    @FindBy(id = "navbarLoginButton")
    private WebElement loginBtn;

    @FindBy(linkText = "Not yet a customer?")
    private WebElement registerLink;

    @FindBy(id = "emailControl")
    private WebElement emailInput;

    @FindBy(id = "passwordControl")
    private WebElement passwordInput;

    @FindBy(id = "repeatPasswordControl")
    private WebElement repeatPasswordInput;

    @FindBy(id = "securityAnswerControl")
    private WebElement answerInput;

    @FindBy(id = "registerButton")
    private WebElement registerBtn;

    @FindBy(css = "mat-error")
    private List<WebElement> errorMessages;

    @FindBy(xpath = "//*[contains(text(),'Email must be unique')]")
    private List<WebElement> duplicateEmailMessage;

    @FindBy(css = "div.mat-mdc-snack-bar-label")
    private WebElement snackbar;

    @FindBy(css = "mat-select")
    private WebElement securityDropdown;

    // ================= ACTIONS =================

    public void openRegisterForm() {

        // Cerrar posibles overlays con ESC
        driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
        safeClick(accountBtn);
        safeClick(loginBtn);

        // ESPERAR QUE DESAPAREZCA EL SNACKBAR
        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.cssSelector("div.mat-mdc-snack-bar-label")));

        // Click seguro
        safeClick(registerLink);
    }

    // Overload: permite setear repeatPassword diferente (para probar MISMATCH)
    public void fillForm(String email, String password, String repeatPassword,
                         String question, String answer) {

        type(emailInput, email);
        type(passwordInput, password);
        type(repeatPasswordInput, repeatPassword);
        selectSecurityQuestion(question);
        type(answerInput, answer);
    }

    public boolean clickRegisterIfEnabled() {
        wait.until(ExpectedConditions.visibilityOf(registerBtn));

        if (!registerBtn.isEnabled()) {
            return false; // no se puede clickear, está disabled
        }

        // Usa safeClick si lo tienes disponible, si no, click normal
        registerBtn.click();
        return true;
    }

    public void register(String email, String password, String question, String answer) {
        fillForm(email, password, password, question, answer);
        clickRegisterIfEnabled();
    }

    public void triggerValidation() {

        try {
            emailInput.click();
            emailInput.sendKeys(Keys.TAB);
            passwordInput.click();
            passwordInput.sendKeys(Keys.TAB);
            repeatPasswordInput.click();
            repeatPasswordInput.sendKeys(Keys.TAB);
            answerInput.click();
            answerInput.sendKeys(Keys.TAB);

            // abrir y cerrar dropdown para activar validación
            securityDropdown.click();
            driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);

            // pequeña espera hasta que aparezca al menos un mat-error
            try {
                wait.until(d -> errorMessages.size() > 0);
            } catch (Exception ignored) {}

        } catch (Exception ignored) {
        }
    }

    // ================= VALIDATIONS =================

    public boolean isRegisterSuccess() {
        String message = wait.until(ExpectedConditions.visibilityOf(snackbar)).getText();
        return message.contains("Registration completed successfully");
    }

    public boolean hasRequiredFieldErrors() {
        List<String> messages = getValidationMessages();

        return messages.contains("Please provide an email address.")
                && messages.contains("Please provide a password.")
                && messages.contains("Please repeat your password.")
                && messages.contains("Please provide an answer to your security question.")
                && messages.contains("Please select a security question.");
    }

    public boolean hasInvalidEmailError() {
        return getValidationMessages().stream()
                .anyMatch(msg -> msg.contains("Email address is not valid."));
    }

    public boolean hasPasswordLengthError() {
        return getValidationMessages().stream()
                .anyMatch(msg -> msg.contains("Password must be 5-40 characters long."));
    }

    public boolean hasPasswordMismatchError() {
        return getValidationMessages().stream()
                .anyMatch(msg -> msg.contains("Passwords do not match"));
    }

    public List<String> getValidationMessages() {

        try {
            wait.until(driver -> errorMessages.size() > 0);
        } catch (Exception e) {
            // no hay errores visibles
        }

        return errorMessages.stream()
                .map(e -> e.getText())
                .filter(text -> text != null && !text.isEmpty())
                .collect(Collectors.toList());
    }

    public boolean isDuplicateEmailError() {

        try {
            wait.until(driver -> duplicateEmailMessage.size() > 0);

            duplicateEmailMessage.forEach(e ->
                    System.out.println("Mensaje encontrado: " + e.getText())
            );

            return true;

        } catch (Exception e) {
            return false;
        }
    }
    public boolean hasAnyValidationError() {
        List<String> messages = getValidationMessages();
        return messages != null && !messages.isEmpty();
    }

    // ================= FIELD ACTIONS =================

    public void enterEmail(String email) {
        type(emailInput, email);
        passwordInput.click(); // trigger validation
    }

    public void enterPassword(String password) {
        type(passwordInput, password);
        repeatPasswordInput.click();
    }

    public void enterRepeatPassword(String repeatPassword) {
        type(repeatPasswordInput, repeatPassword);
        passwordInput.click();
    }

    public void enterSecurityAnswer(String answer) {
        type(answerInput, answer);
        emailInput.click();
    }

    public void selectSecurityQuestion(String questionText) {

        // Abrir dropdown
        wait.until(ExpectedConditions.elementToBeClickable(securityDropdown)).click();

        By optionLocator = By.xpath("//mat-option//span[contains(text(),'" + questionText + "')]");

        // Esperar que la opción exista
        WebElement option = wait.until(ExpectedConditions.visibilityOfElementLocated(optionLocator));

        // SCROLL SI ES NECESARIO (a veces el dropdown es scrollable y la opción no está visible)
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", option);

        // CLICK ROBUSTO
        try {
            wait.until(ExpectedConditions.elementToBeClickable(option)).click();
        } catch (ElementClickInterceptedException e) {
            // Fallback definitivo
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", option);
        }
    }

    // ================= HELPERS =================

    private void type(WebElement element, String text) {
        wait.until(ExpectedConditions.visibilityOf(element)).clear();
        element.sendKeys(text);
    }

    public boolean success() {

        try {
            WebElement snackbar = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("div.mat-mdc-snack-bar-label")));

            String message = snackbar.getText();
            System.out.println("Snackbar message: " + message);

            return message.contains("Registration completed successfully");

        } catch (Exception e) {
            return false;
        }
    }

    public boolean isRegisterButtonDisabled() {
        try {
            // 1) isEnabled (lo más directo)
            if (!registerBtn.isEnabled()) return true;

            // 2) atributo disabled (a veces aparece como "true" o vacío)
            String disabled = registerBtn.getAttribute("disabled");
            if (disabled != null) return true;

            // 3) clase disabled típica en Material
            String cls = registerBtn.getAttribute("class");
            return cls != null && cls.toLowerCase().contains("disabled");

        } catch (Exception e) {
            return false;
        }
    }


}