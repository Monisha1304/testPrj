package testBase;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.Properties;


public class BaseClass {

    public static WebDriver driver;
    public Logger logger;
    public Properties properties;
    public WebDriverWait wait;
    private static final int PAGE_LOAD_TIMEOUT = 30;
    private static final int IMPLICIT_WAIT = 20;

    @BeforeClass(alwaysRun = true)
    @Parameters({"os","browser"})
    void setup(String os, String browser){
        logger = LogManager.getLogger(this.getClass());
        logger.info("============ STARTING TEST SETUP ============");
        logger.info("OS: " + os + ", Browser: " + browser);

        try {
            // Load configuration properties
            properties = new Properties();
            
            // Try multiple paths for config file
            InputStream input = null;
            try {
                // First try: relative path from project root
                input = new FileInputStream("./src/test/resources/config.properties");
            } catch (Exception e) {
                logger.info("Trying alternative config path...");
                // Second try: classpath
                input = this.getClass().getClassLoader().getResourceAsStream("config.properties");
            }
            
            if (input != null) {
                properties.load(input);
                logger.info("✓ Configuration loaded successfully");
            } else {
                throw new Exception("Could not find config.properties file");
            }

            // Initialize WebDriver based on execution environment
            if(properties.getProperty("execution_env").equalsIgnoreCase("remote")){
                String hubUrl = "";
                DesiredCapabilities desiredCapabilities = getDesiredCapabilities(os, browser);
                driver = new RemoteWebDriver(new URL(hubUrl), desiredCapabilities);
                logger.info("✓ Remote WebDriver initialized");
            } else {
                switch (browser.toLowerCase()) {
                    case "chrome":
                        driver = new ChromeDriver();
                        logger.info("✓ Chrome browser initialized");
                        break;
                    case "edge":
                        driver = new EdgeDriver();
                        logger.info("✓ Edge browser initialized");
                        break;
                    default:
                        logger.error("Invalid Browser: " + browser);
                        throw new Exception("Unsupported browser: " + browser);
                }
            }

            // Set timeouts
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(IMPLICIT_WAIT));
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(PAGE_LOAD_TIMEOUT));
            wait = new WebDriverWait(driver, Duration.ofSeconds(PAGE_LOAD_TIMEOUT));
            logger.info("✓ Timeouts configured (Implicit: " + IMPLICIT_WAIT + "s, Page Load: " + PAGE_LOAD_TIMEOUT + "s)");

            // Clear cookies and navigate to URL
            driver.manage().deleteAllCookies();
            logger.info("✓ Cookies cleared");
            
            String baseUrl = properties.getProperty("baseUrl");
            logger.info("Loading URL: " + baseUrl);
            driver.get(baseUrl);
            logger.info("✓ Page loaded successfully");
            
            driver.manage().window().maximize();
            logger.info("✓ Browser window maximized");
            
            logger.info("============ TEST SETUP COMPLETED SUCCESSFULLY ============");
            
        } catch (Exception e) {
            logger.error("❌ ERROR DURING SETUP: " + e.getMessage(), e);
            e.printStackTrace();
            throw new RuntimeException("Setup failed: " + e.getMessage(), e);
        }
    }

    private DesiredCapabilities getDesiredCapabilities(String os, String browser) {
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();

        if(os.equalsIgnoreCase("windows")){
            desiredCapabilities.setPlatform(Platform.WIN11);
        } else if(os.equalsIgnoreCase("mac")){
            desiredCapabilities.setPlatform(Platform.MAC);
        }

        switch (browser.toLowerCase()) {
            case "chrome":
                desiredCapabilities.setBrowserName("chrome");
                break;
            case "edge":
                desiredCapabilities.setBrowserName("MicrosoftEdge");
                break;
            default:
                logger.error("No matching Browser for: " + browser);
        }
        return desiredCapabilities;
    }

    @AfterClass
    void tearDown(){
        try {
            if (driver != null) {
                logger.info("Closing browser...");
                driver.quit();
                logger.info("✓ Browser closed successfully");
            }
        } catch (Exception e) {
            logger.error("Error during tearDown: " + e.getMessage(), e);
        }
    }

    public String captureScreen(String name) {
        try {
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            File source = takesScreenshot.getScreenshotAs(OutputType.FILE);
            String destPath = System.getProperty("user.dir") + "\\screenshots\\" + name + "_" + timeStamp + ".png";
            File target = new File(destPath);
            source.renameTo(target);
            logger.info("Screenshot saved: " + destPath);
            return destPath;
        } catch (Exception e) {
            logger.error("Failed to capture screenshot: " + e.getMessage(), e);
            return null;
        }
    }
}
