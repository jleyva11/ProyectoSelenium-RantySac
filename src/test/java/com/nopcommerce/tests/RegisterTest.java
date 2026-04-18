package com.nopcommerce.tests;

import com.nopcommerce.utils.BaseTest;
import com.nopcommerce.pages.HomePage;
import com.nopcommerce.pages.RegisterPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class RegisterTest extends BaseTest {

    private RegisterPage rp;

    @BeforeMethod(alwaysRun = true)
    public void setup() {

        Assert.assertNotNull(driver, "Driver NO inicializado");
        HomePage home = new HomePage(driver);
        home.dismissBanner();
        rp = new RegisterPage(driver);
        Assert.assertNotNull(rp, "RegisterPage no inicializada");
        rp.openRegisterForm();

    }

    // ================= TC-Register-01 =================

    @Test(groups = {"functional"}, description = "TC-Register-01 Registro exitoso")
    public void TC_Register_01() {

        String email = "user" + System.currentTimeMillis() + "@mail.com";

        rp.register(email, "Password123!", "Name of your favorite pet?", "Dog");

        Assert.assertTrue(rp.isRegisterSuccess(),
                "El registro no fue exitoso con datos válidos");
    }

    // ================= TC-Register-02 =================

    @Test(groups = {"functional", "integration"}, description = "TC-Register-02 Campos obligatorios")
    public void TC_Register_02() {

        rp.triggerValidation();

        Assert.assertTrue(rp.hasRequiredFieldErrors(),
                "No se mostraron errores de campos obligatorios");
    }

    // ================= TC-Register-03 =================

    @Test(groups = {"functional", "regression"}, description = "TC-Register-03 Validación de email")
    public void TC_Register_03() {

        rp.enterEmail("usuario@");

        Assert.assertTrue(rp.hasInvalidEmailError(),
                "No se validó el formato incorrecto del email");

        rp.enterEmail("user" + System.currentTimeMillis() + "@mail.com");

        Assert.assertFalse(rp.hasInvalidEmailError(),
                "Se mostró error con un email válido");
    }

    // ================= TC-Register-04 =================

    @Test(groups = {"regression"}, description = "TC-Register-04 Contraseña segura")
    public void TC_Register_04() {

        rp.enterEmail("test" + System.currentTimeMillis() + "@mail.com");
        rp.selectSecurityQuestion("Name of your favorite pet?");
        rp.enterSecurityAnswer("Dog");

        // Password corta
        rp.enterPassword("123");
        rp.enterRepeatPassword("123");

        Assert.assertTrue(rp.hasPasswordLengthError(),
                "No se mostró error para contraseña corta");

        // Password válida
        rp.enterPassword("StrongPass123!");
        rp.enterRepeatPassword("StrongPass123!");

        Assert.assertFalse(rp.hasPasswordLengthError(),
                "Se mostró error con contraseña válida");
    }

    // ================= TC-Register-05 =================

    @Test(groups = {"integration"}, description = "TC-Register-05 Confirmación de contraseña")
    public void TC_Register_05() {

        rp.enterEmail("test" + System.currentTimeMillis() + "@mail.com");
        rp.enterPassword("Password123!");
        rp.enterRepeatPassword("Different123!");
        rp.selectSecurityQuestion("Name of your favorite pet?");
        rp.enterSecurityAnswer("Dog");

        Assert.assertTrue(rp.hasPasswordMismatchError(),
                "No se mostró error cuando las contraseñas no coinciden");
    }

    // ================= TC-Register-06 =================

    @Test(groups = {"regression", "integration"}, description = "TC-Register-06 Email único")
    public void TC_Register_06() {

        String email = "test" + System.currentTimeMillis() + "@mail.com";
        System.out.println("Email usado: " + email);

        // PRIMER REGISTRO
        rp.openRegisterForm();
        rp.register(email, "Str0ng!Pass12", "Name of your favorite pet?", "Dog");
        Assert.assertTrue(rp.success(), "El registro inicial falló");

        // SEGUNDO REGISTRO (duplicado)
        rp.openRegisterForm();
        rp.register(email, "Str0ng!Pass12", "Name of your favorite pet?", "Dog");
        System.out.println("Segundo registro ejecutado");
        System.out.println("Mensaje después del segundo intento: " + rp.success());

        Assert.assertTrue(rp.isDuplicateEmailError(),
                "El sistema no validó email duplicado");
    }
}