package com.nopcommerce.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;

public class BaseTest {

    protected WebDriver driver; //Se crea OBJETO
    protected WebDriverWait wait;

    @Parameters({"browser", "baseUrl"})
    @BeforeClass(alwaysRun = true)
    public void setUp(
            @Optional("chrome") String browser,
            @Optional("http://localhost:3000/#/login") String baseUrl
    ) {

        switch (browser.toLowerCase()) {
            case "firefox":
                driver = new FirefoxDriver();
                break;
            case "edge":
                driver = new EdgeDriver();
                break;
            default:
                driver = new ChromeDriver();
        }

        Assert.assertNotNull(driver, "Driver NO inicializado");

        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get(baseUrl);
        System.out.println("Driver iniciado en: " + baseUrl);
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("Driver cerrado");
        }
    }

}
