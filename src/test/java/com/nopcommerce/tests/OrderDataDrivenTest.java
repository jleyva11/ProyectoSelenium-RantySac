package com.nopcommerce.tests;

import com.nopcommerce.dataproviders.OrderDataProvider;
import com.nopcommerce.pages.CartPage;
import com.nopcommerce.pages.HomePage;
import com.nopcommerce.pages.LoginPage;
import com.nopcommerce.pages.ShopPage;
import com.nopcommerce.utils.BaseTest;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class OrderDataDrivenTest extends BaseTest {

    private ShopPage shop;
    private CartPage cart;

    @BeforeMethod(alwaysRun = true)
    public void setup() {
        shop = new ShopPage(driver);
        cart = new CartPage(driver);

        HomePage home = new HomePage(driver);
        home.dismissInitialPopups();
        home.closeSideMenuIfOpen();

        cart.clearCartIfNotEmpty();

        driver.get("http://localhost:3000/#/search");
        home.dismissInitialPopups();
        home.closeSideMenuIfOpen();
    }
    @BeforeClass(alwaysRun = true)
    public void loginOnce() {
        HomePage home = new HomePage(driver);
        home.dismissInitialPopups();
        home.forceLogoutIfLoggedIn();
        home.goToLogin();

        LoginPage lp = new LoginPage(driver);
        lp.login("admin@juice-sh.op", "admin123");

        Assert.assertTrue(lp.isLoginSuccess(), "No se logró login");
        home.dismissInitialPopups();
    }

    @Test(dataProvider = "orderAddQuantityData", dataProviderClass = OrderDataProvider.class,
            description = "Data-driven: agregar N unidades y validar totalQty en basket")
    public void TC_Order_Add_Quantity_DataDriven(int productAdds, int expectedTotalQty) {

        for (int i = 0; i < productAdds; i++) {
            shop.addFirstProduct();
        }

        HomePage home = new HomePage(driver);
        home.openBasket();

        Assert.assertEquals(cart.getTotalQuantity(), expectedTotalQty,
                "La cantidad total del carrito no coincide con lo esperado");
    }

    @Test(dataProvider = "orderChangeQuantityData", dataProviderClass = OrderDataProvider.class,
            description = "Data-driven: modificar quantity en basket con +/- y validar")
    public void TC_Order_Change_Quantity_DataDriven(int initialAdds, int targetQty) {

        for (int i = 0; i < initialAdds; i++) {
            shop.addFirstProduct();
        }

        HomePage home = new HomePage(driver);
        home.openBasket();

        cart.changeQuantity(0, targetQty);
        Assert.assertEquals(cart.getQuantity(0), targetQty,
                "La cantidad no se actualizó correctamente");
    }
}
