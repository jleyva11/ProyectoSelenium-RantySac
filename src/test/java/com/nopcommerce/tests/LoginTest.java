package com.nopcommerce.tests;

import com.nopcommerce.utils.BaseTest;
import com.nopcommerce.pages.HomePage;
import com.nopcommerce.pages.LoginPage;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;

public class LoginTest extends BaseTest {

    private LoginPage lp;

    @BeforeMethod(alwaysRun = true)
    public void setup() {
        HomePage home = new HomePage(driver);
        home.dismissInitialPopups(); //Cerrar popup
        home.closeSideMenuIfOpen();//Cerrar menú lateral si está abierto
        home.forceLogoutIfLoggedIn(); //Asegurar estado sin sesión
        home.goToLogin();
        home.closeSideMenuIfOpen();
        lp = new LoginPage(driver);
        driver.switchTo().activeElement().sendKeys(Keys.ESCAPE);

        new WebDriverWait(driver, Duration.ofSeconds(3)).until(
                ExpectedConditions.invisibilityOfElementLocated(
                        By.cssSelector(".mat-drawer-backdrop.mat-drawer-shown, .cdk-overlay-backdrop")
                )
        );


    }

    // ================= TC-Login-01 =================
    @Test(groups = {"regression", "integration"}, description = "TC-Login-01 Inicio de sesión exitoso")
    public void TC_Login_01() {

        lp.login("admin@juice-sh.op", "admin123");
        Assert.assertTrue(
                lp.isLoginSuccess(),
                "No se logró iniciar sesión con credenciales válidas"
        );

    }

    // ================= TC-Login-02 =================
    @Test(groups = {"functional"}, description = "TC-Login-02 Inicio de sesión con credenciales incorrectas")


    public void TC_Login_02() {

        lp.login("cualquiera@fake.com", "cualquiera");
        Assert.assertTrue(
                lp.isLoginErrorDisplayed(),
                "No se mostró error para credenciales incorrectas"
        );
    }


    // ================= TC-Login-03 =================
    @Test(groups = {"functional"}, description = "TC-Login-03 Credenciales en blanco")



    public void TC_Login_03() {

        lp.triggerRequiredValidation();

        // Debug mínimo (temporal)
        System.out.println("Email DOM value: '" + lp.getEmailDomValue() + "'");
        System.out.println("Password DOM value: '" + lp.getPasswordDomValue() + "'");

        Assert.assertTrue(
                lp.isEmailHtml5Invalid() && lp.isPasswordHtml5Invalid(),
                "No se validaron campos obligatorios: HTML5 checkValidity() no detecta campos vacíos como inválidos"
        );
    }




    // ================= TC-Login-04 =================
    @Test(groups = {"regression"}, description = "TC-Login-02 Correo electrónico no registrado")

    public void TC_Login_04() {

        lp.login("noexiste@fake.com", "Password123");
        Assert.assertTrue(
                lp.isLoginErrorDisplayed(),
                "No se mostró error para email no registrado"
        );
    }

    // ================= TC-Login-05 =================
    @Test(groups = {"functional", "integration"}, description = "TC-Login-05 Contraseña olvidada")
    public void TC_Login_05() {

        lp.clickForgotPassword();
        Assert.assertTrue(
                wait.until(driver -> driver.getCurrentUrl().contains("forgot-password")),
                "No redirige a recuperación de contraseña"
        );
    }
}