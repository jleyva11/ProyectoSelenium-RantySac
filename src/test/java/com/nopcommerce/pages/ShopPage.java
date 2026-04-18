package com.nopcommerce.pages;

import com.nopcommerce.utils.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class ShopPage extends BasePage {

    private static final String SHOP_URL = "http://localhost:3000/#/search";

    public ShopPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    // ===== LOCATORS =====

    @FindBy(css = "button[aria-label='Add to Basket']")
    private List<WebElement> addToBasketButtons;

    // ===== ACTIONS =====

    public void addFirstProduct() {

        // 1) Asegurar que estamos en el listado de productos
        wait.until(ExpectedConditions.urlContains("/search"));

        // 2) Esperar a que Angular renderice los botones
        wait.until(driver -> addToBasketButtons.size() > 0);

        // 3) Click seguro (maneja overlays)
        safeClick(addToBasketButtons.get(0));

        // 4) Esperar snackbar real de confirmación
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".mat-mdc-snack-bar-container")
        ));
    }

}