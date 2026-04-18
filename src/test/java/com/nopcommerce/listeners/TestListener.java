package com.nopcommerce.listeners;

import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;

/**
 * Listener TestNG: adjunta screenshot a Allure cuando un test falla.
 */
public class TestListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {
        Object instance = result.getInstance();

        try {
            // obtiene el driver desde BaseTest (campo "driver")
            var driverField = instance.getClass().getSuperclass().getDeclaredField("driver");
            driverField.setAccessible(true);
            Object driverObj = driverField.get(instance);

            if (driverObj instanceof TakesScreenshot ts) {
                byte[] png = ts.getScreenshotAs(OutputType.BYTES);
                Allure.addAttachment("Screenshot - FAIL", new ByteArrayInputStream(png));
            }
        } catch (Exception ignored) {
        }
    }
}