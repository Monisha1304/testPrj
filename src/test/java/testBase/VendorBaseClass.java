package testBase;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;

/**
 * VendorBaseClass - standalone base class for vendor tests
 * Does NOT extend BaseClass to avoid @BeforeClass conflicts
 */
public class VendorBaseClass {

    public static WebDriver driver;

    @BeforeClass(alwaysRun = true)
    @Parameters({"os","browser"})
    public void vendorSetup(String os, String browser) {
        System.out.println("=== Vendor Setup Starting ===");
        System.out.println("OS: " + os);
        System.out.println("Browser: " + browser);

        try {
            switch (browser.toLowerCase()) {
                case "chrome":
                    driver = new ChromeDriver();
                    System.out.println("Chrome driver initialized");
                    break;
                case "edge":
                    driver = new EdgeDriver();
                    System.out.println("Edge driver initialized");
                    break;
                default:
                    System.out.println("Invalid Browser: " + browser);
                    return;
            }

            driver.manage().deleteAllCookies();
            driver.get("https://internal-food-booking.vercel.app/");
            driver.manage().window().maximize();
            System.out.println("=== Driver Setup Completed Successfully ===");

        } catch (Exception e) {
            System.out.println("ERROR during vendor setup: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Vendor setup failed: " + e.getMessage(), e);
        }
    }

    @AfterClass(alwaysRun = true)
    public void vendorTearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("Driver quit successfully");
        }
    }
}


