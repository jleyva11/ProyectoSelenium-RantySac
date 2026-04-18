package com.nopcommerce.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;

public class BaseTest {

    protected WebDriver driver;
    protected WebDriverWait wait;

    @Parameters({"browser", "baseUrl"})
    @BeforeClass(alwaysRun = true)
    public void setUp(
            @Optional("chrome") String browser,
            @Optional("http://localhost:3000/#/login") String baseUrl
    ) {
        // Detecta si estás en GitHub Actions y/o si se pidió HEADLESS
        boolean isGitHubActions = "true".equalsIgnoreCase(System.getenv("GITHUB_ACTIONS"));
        boolean isHeadless = "true".equalsIgnoreCase(System.getenv("HEADLESS")) || isGitHubActions;

        System.setProperty("webdriver.chrome.logfile", "target/chromedriver.log");
        System.setProperty("webdriver.chrome.verboseLogging", "true");

        switch (browser.toLowerCase()) {

            case "firefox": {
                FirefoxOptions options = new FirefoxOptions();
                if (isHeadless) {
                    options.addArguments("-headless");
                    // Tamaño estándar (Firefox headless puede ignorar maximize)
                    options.addArguments("--width=1920");
                    options.addArguments("--height=1080");
                }
                driver = new FirefoxDriver(options);
                break;
            }

            case "edge": {
                EdgeOptions options = new EdgeOptions();
                if (isHeadless) {
                    options.addArguments("--headless=new");
                    options.addArguments("--no-sandbox");
                    options.addArguments("--disable-dev-shm-usage");
                    options.addArguments("--window-size=1920,1080");
                }
                driver = new EdgeDriver(options);
                break;
            }

            default: { // chrome
                ChromeOptions options = new ChromeOptions();

                if (isHeadless) {
                    // Flags típicos para estabilidad en CI/Linux
                    options.addArguments("--headless=new");
                    options.addArguments("--no-sandbox");
                    options.addArguments("--disable-dev-shm-usage");
                    options.addArguments("--window-size=1920,1080");

                }

                driver = new ChromeDriver(options);
                break;
            }
        }

        // Fail fast: si el driver no inicializa, cortamos con error claro
        Assert.assertNotNull(driver, "Driver NO inicializado (revisa Headless/ChromeOptions/CI)");

        // En headless, maximize puede no aplicar; el window-size ya está seteado en options
        if (!isHeadless) {
            driver.manage().window().maximize();
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get(baseUrl);
        System.out.println("Driver iniciado en: " + baseUrl + " | browser=" + browser + " | headless=" + isHeadless);
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("Driver cerrado");
        }
    }
}