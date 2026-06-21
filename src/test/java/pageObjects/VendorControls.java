package pageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import testBase.BaseClass;
import testBase.BasePage;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class VendorControls extends BasePage {

    private WebDriver driverRef;
    private WebDriverWait wait;

    // Login Page Locators
    private By emailInput = By.xpath("//input[@type='email' or @name='email']");
    private By passwordInput = By.xpath("//input[@type='password' or @name='password']");
    private By loginButton = By.xpath("//button[ contains(text(),'Sign In') or @type='submit']");

    // Navigation & Dashboard Locators
    private By navBarButtons = By.xpath("//nav//button | //nav//a | //div[contains(@class,'nav')]//button");
    private By pageTitle = By.xpath("//h1 | //h2[contains(@class,'title')] | //div[contains(@class,'page-title')]");

    // Revenue Panel Locators
    private By revenueLink = By.xpath("//a[contains(text(),'Revenue')] | //button[contains(text(),'Revenue')]");
    private By revenuePanel = By.xpath("//div[contains(@class,'revenue') or contains(@class,'Revenue')]");
    private By consolidatedEarnings = By.xpath("//div[contains(text(),'Earnings') or contains(text(),'Total')]//following-sibling::div | //span[contains(@class,'earnings-value')] | //div[contains(@class,'amount')]");

    // Revenue Table/Details Locators
    private By revenueTable = By.xpath("//table[contains(@class,'revenue')] | //div[contains(@class,'revenue-table')]");
    private By revenueRows = By.xpath("//table//tbody//tr | //div[contains(@class,'revenue-row')]");
    private By yearColumn = By.xpath("//td[contains(text(),'202')] | //div[contains(@class,'year')]");
    private By amountColumn = By.xpath("//td[contains(@data-column,'amount')] | //span[contains(@class,'amount')]");

    // Order Locators
    private By orderRows = By.xpath("//table//tbody//tr | //div[contains(@class,'order-row')]");
    private By completedOrders = By.xpath("//tr[contains(., 'Completed')] | //div[contains(@class,'completed')]");
    private By orderAmount = By.xpath(".//td[last()] | .//span[contains(@class,'amount')]");

    public VendorControls(WebDriver driver) {
        super(driver != null ? driver : BaseClass.driver);
        this.driverRef = driver != null ? driver : BaseClass.driver;
        this.wait = new WebDriverWait(this.driverRef, Duration.ofSeconds(15));
    }

    // ============ TC_050: Navigation & Page Title Verification ============

    /**
     * Login with vendor credentials
     */
    public void login(String email, String password) {
        try {
            WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(emailInput));
            emailField.clear();
            emailField.sendKeys(email);

            WebElement passwordField = driverRef.findElement(passwordInput);
            passwordField.clear();
            passwordField.sendKeys(password);

            WebElement loginBtn = driverRef.findElement(loginButton);
            loginBtn.click();

            // Wait for dashboard to load
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(pageTitle),
                    ExpectedConditions.visibilityOfElementLocated(navBarButtons)
            ));
        } catch (Exception e) {
            throw new RuntimeException("Login failed: " + e.getMessage(), e);
        }
    }

    /**
     * Get all navigation bar buttons
     */
    public List<WebElement> getAllNavBarButtons() {
        return driverRef.findElements(navBarButtons);
    }

    /**
     * Click a specific nav button by text
     */
    public void clickNavButton(String buttonText) {
        By buttonLocator = By.xpath("//nav//button[contains(text(),'" + buttonText + "')] | //nav//a[contains(text(),'" + buttonText + "')]");
//        a[@class='mat-mdc-list-item mdc-list-item nav-item mat-mdc-list-item-interactive mdc-list-item--with-leading-icon mat-mdc-list-item-single-line mdc-list-item--with-one-line ng-star-inserted active-link']//span[@class='mdc-list-item__content']
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(buttonLocator));
        button.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(pageTitle));
    }

    /**
     * Get the current page title
     */
    public String getPageTitle() {
        WebElement titleElement = wait.until(ExpectedConditions.visibilityOfElementLocated(pageTitle));
        return titleElement.getText();
    }

    /**
     * Verify page title matches expected value
     */
    public boolean verifyPageTitle(String expectedTitle) {
        String actualTitle = getPageTitle();
        return actualTitle.equalsIgnoreCase(expectedTitle);
    }

    // ============ TC_051: Revenue Verification ============

    /**
     * Navigate to Revenue Details panel
     */
    public void navigateToRevenueDetails() {
        try {
            WebElement revenueBtn = wait.until(ExpectedConditions.elementToBeClickable(revenueLink));
            revenueBtn.click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(revenuePanel));
        } catch (Exception e) {
            throw new RuntimeException("Failed to navigate to revenue details: " + e.getMessage(), e);
        }
    }

    /**
     * Get consolidated earnings from dashboard
     */
    public double getConsolidatedEarnings() {
        try {
            WebElement earningsElement = wait.until(ExpectedConditions.visibilityOfElementLocated(consolidatedEarnings));
            String earningsText = earningsElement.getText();
            return parseMoneyFromString(earningsText);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get consolidated earnings: " + e.getMessage(), e);
        }
    }

    /**
     * Get sum of all revenue from the years table
     */
    public double getSumOfRevenueFromYearsTable() {
        try {
            List<WebElement> rows = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(revenueRows));
            double totalRevenue = 0.0;

            for (WebElement row : rows) {
                try {
                    // Find the amount in each row
                    List<WebElement> cells = row.findElements(By.xpath(".//td | .//div[contains(@class,'cell')]"));
                    if (cells.size() >= 2) {
                        String amountText = cells.get(cells.size() - 1).getText();
                        double amount = parseMoneyFromString(amountText);
                        totalRevenue += amount;
                    }
                } catch (Exception rowEx) {
                    // Skip rows that can't be parsed
                }
            }

            return totalRevenue;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get sum of revenue from table: " + e.getMessage(), e);
        }
    }

    /**
     * Get sum of completed orders revenue
     */
    public double getSumOfCompletedOrdersForVendor(String vendorId) {
        try {
            List<WebElement> rows = driverRef.findElements(completedOrders);
            double sum = 0.0;

            for (WebElement row : rows) {
                try {
                    String rowText = row.getText().toLowerCase();

                    // If vendorId provided, check if this row belongs to the vendor
                    if (vendorId != null && !vendorId.isEmpty() && !rowText.contains(vendorId.toLowerCase())) {
                        continue;
                    }

                    // Extract the last monetary value from the row (typically the order amount)
                    List<WebElement> cells = row.findElements(By.xpath(".//td | .//div[contains(@class,'cell')]"));
                    if (!cells.isEmpty()) {
                        String lastCell = cells.get(cells.size() - 1).getText();
                        double amount = parseMoneyFromString(lastCell);
                        sum += amount;
                    }
                } catch (Exception rowEx) {
                    // Skip rows that can't be parsed
                }
            }

            return sum;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get sum of completed orders: " + e.getMessage(), e);
        }
    }

    /**
     * Iterate through all nav buttons and verify page titles load correctly
     */
    public List<String> verifyAllNavButtonsLoadCorrectPages() {
        List<String> results = new ArrayList<>();
        List<WebElement> buttons = getAllNavBarButtons();

        for (WebElement button : buttons) {
            try {
                String buttonText = button.getText();
                if (buttonText.isEmpty()) continue;

                button.click();
                Thread.sleep(1000); // Wait for page transition

                String pageTitle = getPageTitle();
                results.add(buttonText + " -> " + pageTitle);
            } catch (Exception e) {
                results.add(button.getText() + " -> ERROR: " + e.getMessage());
            }
        }

        return results;
    }

    /**
     * Get recent order timestamps
     */
    public List<LocalDateTime> getRecentOrderTimestamps(DateTimeFormatter formatter) {
        List<LocalDateTime> timestamps = new ArrayList<>();
        List<WebElement> rows = driverRef.findElements(orderRows);

        for (WebElement row : rows) {
            String rowText = row.getText();
            if (rowText == null || rowText.isEmpty()) continue;

            String[] tokens = rowText.split("\\n|\\s{2,}|\\t");
            for (String token : tokens) {
                try {
                    LocalDateTime dt = LocalDateTime.parse(token.trim(), formatter);
                    timestamps.add(dt);
                    break;
                } catch (Exception ignored) {
                    // Token is not a date
                }
            }
        }

        return timestamps;
    }

    /**
     * Parse currency string to double
     * Handles formats like: $100.50, €99.99, £50, 1000, etc.
     */
    private double parseMoneyFromString(String s) {
        if (s == null || s.trim().isEmpty()) {
            return 0.0;
        }

        // Remove currency symbols and extra whitespace
        String cleaned = s.replaceAll("[^0-9.,\\-]", "").trim();

        if (cleaned.isEmpty()) {
            return 0.0;
        }

        try {
            // Handle both comma and dot as decimal separator
            cleaned = cleaned.replace(",", ".");
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    /**
     * Wait for revenue panel to be visible
     */
    public void waitForRevenuePanel() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(revenuePanel));
    }

    /**
     * Check if revenue is greater than or equal to zero
     */
    public boolean isRevenueGreaterOrEqualToZero() {
        double earnings = getConsolidatedEarnings();
        return earnings >= 0;
    }
}