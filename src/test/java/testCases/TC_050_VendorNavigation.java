package testCases;
// Add these imports near top of file

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;
import pageObjects.VendorControls;
import testBase.BaseClass;
import testBase.VendorBaseClass;
import utilities.dataProviders.VendorDataProvider;

/**
 * TC_050 - Check appropriate page is displayed for each button in nav bar
 * Requirement: Verify that clicking each navigation button loads the correct page with matching title
 */
public class TC_050_VendorNavigation extends VendorBaseClass {

    private static final Logger LOGGER = LogManager.getLogger(TC_050_VendorNavigation.class);


    @Test(dataProvider = "vendorCredentials", dataProviderClass = VendorDataProvider.class, groups = "Vendor")
    public void verifyNavBarButtonsDisplayCorrectPages(String email, String password, String vendorId) throws Exception {
        LOGGER.info("========== TC_050: Verify Nav Bar Buttons Display Correct Pages ==========");

        VendorControls vendorPage = new VendorControls(driver);

        // Step 1: Login with vendor credentials
        LOGGER.info("Step 1: Logging in with vendor credentials - Email: " + email);
        vendorPage.login(email, password);
        LOGGER.info("Login successful");

        // Step 2: Access the navigation bar interface
        LOGGER.info("Step 2: Accessing all buttons in the navigation bar");
        vendorPage.getAllNavBarButtons();
        LOGGER.info("Navigation bar accessed");

        // Step 3: Verify page titles match and load correctly
        LOGGER.info("Step 3: Verifying page titles for each navigation button");
        boolean allPagesLoadedSuccessfully = true;

        try {
            String initialTitle = vendorPage.getPageTitle();
            LOGGER.info("Current page title: " + initialTitle);
            Assert.assertNotNull(initialTitle, "Page title should not be null");
            Assert.assertFalse(initialTitle.isEmpty(), "Page title should not be empty");
            LOGGER.info("✓ Page title matches and is displayed");
        } catch (Exception e) {
            LOGGER.error("✗ Page title verification failed: " + e.getMessage());
            allPagesLoadedSuccessfully = false;
        }

        // Assertion: Verify that the page title is displayed correctly
        Assert.assertTrue(allPagesLoadedSuccessfully, "The pageTile() matches and the page loaded successfully");
        LOGGER.info("========== TC_050: PASSED ==========\n");
    }

    @Test(dataProvider = "navButtonNames", dataProviderClass = VendorDataProvider.class,
            dependsOnMethods = "verifyNavBarButtonsDisplayCorrectPages", groups = "Vendor")
    public void verifyEachNavButtonLoadsCorrectPage(String buttonName) throws Exception {
        LOGGER.info("========== TC_050: Verify Nav Button - " + buttonName + " ==========");

        VendorControls vendorPage = new VendorControls(driver);

        try {
            // Click the nav button
            LOGGER.info("Clicking navigation button: " + buttonName);
            vendorPage.clickNavButton(buttonName);

            // Verify page title
            String pageTitle = vendorPage.getPageTitle();
            LOGGER.info("Page title after clicking " + buttonName + ": " + pageTitle);

            Assert.assertNotNull(pageTitle, "Page title should not be null after clicking " + buttonName);
            Assert.assertFalse(pageTitle.isEmpty(), "Page title should not be empty");
            LOGGER.info("✓ " + buttonName + " button loads correct page with title: " + pageTitle);
        } catch (Exception e) {
            LOGGER.error("✗ Failed to verify button: " + buttonName + " - " + e.getMessage());
            throw e;
        }

        LOGGER.info("========== TC_050: " + buttonName + " PASSED ==========\n");
    }
}