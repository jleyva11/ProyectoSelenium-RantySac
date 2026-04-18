package com.nopcommerce.pages;

import com.nopcommerce.utils.BasePage;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;

import java.util.List;

public class CartPage extends BasePage {

    public CartPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    // ===== LOCATORS =====
    private final By basketTableBy = By.cssSelector("mat-table");

    private final By cartRowBy = By.cssSelector("mat-table mat-row, tr.mat-mdc-row");

    // Botón trash (adaptado a tu HTML: svg con fa-trash-alt)
    private final By trashButtonBy = By.xpath(
            "(//mat-table//mat-row | //tr[contains(@class,'mat-mdc-row')])" +
                    "//button[contains(@class,'mat-mdc-icon-button')][.//*[name()='svg' and contains(@class,'fa-trash-alt')]]"
    );

    @FindBy(css = "#price")
    private WebElement totalPrice;

    @FindBy(id = "checkoutButton")
    private WebElement checkoutBtn;

    @FindBy(xpath = "//mat-row | //tr[contains(@class,'mat-mdc-row')]")
    private List<WebElement> cartRows;

    @FindBy(xpath =
            "(//mat-cell[contains(@class,'mat-column-quantity')] | //td[contains(@class,'mat-column-quantity')])" +
                    "//span[contains(@class,'cell-initial-font')]")
    private List<WebElement> quantityValues;


    @FindBy(xpath =
            "//mat-cell[contains(@class,'mat-column-remove')]//button[.//*[name()='svg' and contains(@class,'fa-trash-alt')]]")
    private List<WebElement> removeButtons;


    @FindBy(xpath =
            "(//mat-cell[contains(@class,'mat-column-quantity')] | //td[contains(@class,'mat-column-quantity')])" +
                    "//button[.//*[name()='svg' and contains(@class,'fa-minus-square')]]")
    private List<WebElement> minusButtons;

    @FindBy(xpath =
            "(//mat-cell[contains(@class,'mat-column-quantity')] | //td[contains(@class,'mat-column-quantity')])" +
                    "//button[.//*[name()='svg' and contains(@class,'fa-plus-square')]]")
    private List<WebElement> plusButtons;

    // ===== ACTIONS =====
    private void waitBasketReady() {
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("mat-table")));
        // cartRows puede ser 0 si el carrito está vacío (estado válido)
    }

    public int getCartItemsCount() {
        // 0 es válido; no uses waits que exigen filas.
        return cartRows.size();
    }

    public int getTotalQuantity() {
        int sum = 0;
        for (WebElement span : quantityValues) {
            String t = span.getText().trim();
            if (!t.isBlank()) sum += Integer.parseInt(t);
        }
        return sum;
    }

    public void removeFirstItem() {
        int initial = cartRows.size();
        Assert.assertTrue(initial > 0, "No hay items para eliminar");

        safeClick(removeButtons.get(0));
        wait.until(d -> cartRows.size() < initial);
    }

    public void changeQuantity(int index, int target) {

        // Asegurar que existen botones y valor en esa fila
        Assert.assertTrue(quantityValues.size() > index, "No existe el producto en la posición " + index);
        Assert.assertTrue(plusButtons.size() > index, "No existe botón PLUS para la posición " + index);
        Assert.assertTrue(minusButtons.size() > index, "No existe botón MINUS para la posición " + index);

        int current = getQuantity(index);

        while (current < target) {
            safeClick(plusButtons.get(index));
            int expected = current + 1;
            wait.until(d -> getQuantity(index) == expected);
            current = expected;
        }

        while (current > target) {
            safeClick(minusButtons.get(index));
            int expected = current - 1;
            wait.until(d -> getQuantity(index) == expected);
            current = expected;
        }
    }

    public int getQuantity(int index) {
        Assert.assertTrue(quantityValues.size() > index,
                "No existe el producto en la posición " + index);

        String text = quantityValues.get(index).getText().trim(); // "3"
        return Integer.parseInt(text);
    }

    public void proceedToCheckout() {

        // Asegurar que el basket está renderizado
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("mat-table")));

        // Esperar que el botón exista
        wait.until(ExpectedConditions.visibilityOf(checkoutBtn));

        // Scroll al botón (en /basket suele estar abajo)
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center', inline:'center'});",
                checkoutBtn
        );

        // Click robusto
        safeClick(checkoutBtn);

        // Confirmar que salimos de /basket
        wait.until(d -> !d.getCurrentUrl().contains("/basket"));
    }

    public void clearCartIfNotEmpty() {
        HomePage home = new HomePage(driver);
        home.dismissInitialPopups();
        home.closeSideMenuIfOpen();
        home.openBasket();

        // ancla: basket cargado
        wait.until(ExpectedConditions.presenceOfElementLocated(basketTableBy));

        while (true) {
            int rows = driver.findElements(cartRowBy).size();
            if (rows == 0) break;

            // Click siempre sobre el primer trash DISPONIBLE re-localizando
            safeClick(trashButtonBy);

            // esperar que disminuya la cantidad de filas (o al menos que se refresque)
            wait.until(d -> d.findElements(cartRowBy).size() < rows);
        }
    }

    public String getTotalPriceText() {
        return wait.until(ExpectedConditions.visibilityOf(totalPrice)).getText().trim();
    }

    public boolean hasTotalPrice() {
        String text = getTotalPriceText();
        return text.toLowerCase().contains("total price") && text.matches(".*\\d+.*");
    }

}