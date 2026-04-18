package com.nopcommerce.tests;

import com.nopcommerce.pages.*;
import com.nopcommerce.utils.BaseTest;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class OrderTest extends BaseTest {

    private ShopPage shop;
    private CartPage cart;

    private void debugState(String label, CartPage cart) {
        System.out.println(label + " URL=" + driver.getCurrentUrl()
                + " rows=" + cart.getCartItemsCount()
                + " totalQty=" + cart.getTotalQuantity());
    }

    @BeforeClass(alwaysRun = true)
    public void login() {

        HomePage home = new HomePage(driver);
        home.dismissInitialPopups();
        home.forceLogoutIfLoggedIn();
        home.goToLogin();

        LoginPage lp = new LoginPage(driver);
        lp.login("admin@juice-sh.op", "admin123");

        Assert.assertTrue(lp.isLoginSuccess(), "No se logró login");

        // Cerrar modales bloqueantes
        home.dismissInitialPopups();
    }

    @BeforeMethod(alwaysRun = true)
    public void setupEachTest() {

        shop = new ShopPage(driver);
        cart = new CartPage(driver);

        // 1) Limpia carrito
        cart.clearCartIfNotEmpty();

        // 2) Vuelve a /search (para que addFirstProduct no falle)
        driver.get("http://localhost:3000/#/search");
    }

    // TC-Order-01
    @Test(groups = {"functional", "regression"}, description = "TC-Order-01 Añadir productos al carrito")
    public void TC_Order_01() {
        debugState("ANTES ADD", cart);
        shop.addFirstProduct();
        shop.addFirstProduct();
        debugState("DESPUÉS ADD (aún en search)", cart);
        HomePage home = new HomePage(driver);
        home.openBasket();
        debugState("EN BASKET", cart);
        Assert.assertTrue(cart.getTotalQuantity() > 0, "No se agregaron productos al carrito");
    }

    // TC-Order-02
    @Test(groups = {"functional"}, description = "TC-Order-02 Eliminar productos del carrito")
    public void TC_Order_02() {
        debugState("ANTES ADD", cart);
        shop.addFirstProduct();
        debugState("DESPUÉS ADD (aún en search)", cart);
        HomePage home = new HomePage(driver);
        home.openBasket();
        debugState("EN BASKET", cart);
        int initial = cart.getCartItemsCount();
        cart.removeFirstItem();

        Assert.assertTrue(cart.getCartItemsCount() < initial,
                "El producto no fue eliminado del carrito");
    }


    // TC-Order-03
    @Test(groups = {"functional"}, description = "TC-Order-03 Modificar cantidad de productos")
    public void TC_Order_03() {
        debugState("ANTES ADD", cart);
        shop.addFirstProduct();
        debugState("DESPUÉS ADD (aún en search)", cart);
        HomePage home = new HomePage(driver);
        home.openBasket();
        debugState("EN BASKET", cart);
        cart.changeQuantity(0, 3);

        Assert.assertEquals(cart.getQuantity(0), 3,
                "La cantidad del producto no se actualizó correctamente");
    }

    // TC-Order-04
    @Test(groups = {"integration", "regression"}, description = "TC-Order-04 Creación de orden con 3 productos")
    public void TC_Order_04() {
        debugState("ANTES ADD", cart);
        shop.addFirstProduct();
        shop.addFirstProduct();
        shop.addFirstProduct();
        debugState("DESPUÉS ADD (aún en search)", cart);
        HomePage home = new HomePage(driver);
        home.openBasket();
        debugState("EN BASKET", cart);
        Assert.assertEquals(cart.getTotalQuantity(), 3,
                "No se agregaron 3 unidades al carrito");

        Assert.assertTrue(cart.hasTotalPrice(),
                "No se mostró el Total Price en el basket");
        System.out.println("Total mostrado: " + cart.getTotalPriceText());

        cart.proceedToCheckout();

        Assert.assertTrue(
                driver.getCurrentUrl().contains("address")
                        || driver.getCurrentUrl().contains("delivery")
                        || driver.getCurrentUrl().contains("payment")
                        || driver.getCurrentUrl().contains("checkout"),
                "No se redirigió al flujo de checkout (address/delivery/payment)"
        );
    }

}