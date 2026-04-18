package com.nopcommerce.tests;

import com.nopcommerce.dataproviders.RegisterDataProvider;
import com.nopcommerce.pages.HomePage;
import com.nopcommerce.pages.RegisterPage;
import com.nopcommerce.utils.BaseTest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class RegisterDataDrivenTest extends BaseTest {

    private RegisterPage rp;

    @BeforeMethod(alwaysRun = true)
    public void setup() {
        HomePage home = new HomePage(driver);

        driver.get("http://localhost:3000/#/login");
        home.dismissInitialPopups();
        home.closeSideMenuIfOpen();

        rp = new RegisterPage(driver);
        rp.openRegisterForm();
    }

    @Test(dataProvider = "registerData", dataProviderClass = RegisterDataProvider.class)
    public void TC_Register_DataDriven(String email, String pass, String repeat,
                                       String question, String answer, String expected) {

        rp.openRegisterForm();

        // Llenar formulario
        rp.fillForm(email, pass, repeat, question, answer);

        switch (expected) {

            case "SUCCESS" -> {
                boolean clicked = rp.clickRegisterIfEnabled();
                Assert.assertTrue(clicked, "El botón Register debería estar habilitado en SUCCESS");
                Assert.assertTrue(rp.isRegisterSuccess(), "Se esperaba registro exitoso");
            }

            case "REQUIRED" -> {
                rp.triggerValidation();

                boolean requiredOk =
                        rp.isRegisterButtonDisabled()   // validación preventiva
                                || rp.hasAnyValidationError()   // mat-error presente
                                || rp.hasRequiredFieldErrors(); // tu validación “estricta” (si aplica)

                Assert.assertTrue(requiredOk,
                        "Se esperaban errores de campos obligatorios (botón disabled o mat-error)");
            }

            case "INVALID" -> {
                rp.triggerValidation();
                Assert.assertTrue(rp.hasInvalidEmailError(), "Se esperaba error de email inválido");
            }

            case "MISMATCH" -> {
                rp.triggerValidation();
                Assert.assertTrue(rp.hasPasswordMismatchError(), "Se esperaba mismatch de passwords");
            }

            default -> Assert.fail("Expected result no reconocido: " + expected);
        }
    }
}