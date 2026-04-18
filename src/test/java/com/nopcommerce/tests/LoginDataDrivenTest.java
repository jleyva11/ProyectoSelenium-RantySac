package com.nopcommerce.tests;

import com.nopcommerce.dataproviders.LoginDataProvider;
import com.nopcommerce.pages.HomePage;
import com.nopcommerce.pages.LoginPage;
import com.nopcommerce.utils.BaseTest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LoginDataDrivenTest extends BaseTest {

    private LoginPage lp;

    @BeforeMethod(alwaysRun = true)
    public void setup() {
        HomePage home = new HomePage(driver);

        // 1) Ir SIEMPRE al login (estado determinista)
        driver.get("http://localhost:3000/#/login");

        // 2) Cerrar popups (welcome/cookies)
        home.dismissInitialPopups();

        // 3) Cerrar menú lateral si quedó abierto
        home.closeSideMenuIfOpen();

        // 4) asegurar sin sesión
        home.forceLogoutIfLoggedIn();
        driver.get("http://localhost:3000/#/login");
        home.closeSideMenuIfOpen();

        lp = new LoginPage(driver);
    }

    @Test(dataProvider = "loginData", dataProviderClass = LoginDataProvider.class)
    public void loginDataDriven(String email, String password, String expected) {

        if ("REQUIRED".equals(expected)) {
            lp.triggerRequiredValidation();
            Assert.assertTrue(lp.isRequiredValidationTriggered(), "Se esperaba validación required");
            return;
        }

        lp.login(email, password);

        if ("SUCCESS".equals(expected)) {
            Assert.assertTrue(lp.isLoginSuccess(), "Se esperaba login exitoso");
        } else {
            Assert.assertTrue(lp.isLoginErrorVisible(), "Se esperaba error de credenciales inválidas");
        }
    }
}