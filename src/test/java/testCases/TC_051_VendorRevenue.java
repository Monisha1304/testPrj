package testCases;

import org.testng.Assert;
import org.testng.annotations.Test;
import pageObjects.VendorControls;
import testBase.BaseClass;
import utilities.dataProviders.VendorDataProvider;

/**
 * TC_051 - Check the revenue of the vendor
 * Requirement: Verify that revenue is ≥ 0 and that total dashboard revenue matches sum of year table revenues
 */
public class TC_051_VendorRevenue extends BaseClass {

    @Test(dataProvider = "vendorCredentials", dataProviderClass = VendorDataProvider.class, groups = "Vendor")
    public void verifyVendorRevenue(String email, String password, String vendorId) throws Exception {
        logger.info("========== TC_051: Verify Vendor Revenue ==========");

        VendorControls vendorPage = new VendorControls(driver);

        // Step 1: Login with vendor credentials
        logger.info("Step 1: Logging in with vendor credentials - Email: " + email);
        vendorPage.login(email, password);
        logger.info("Login successful");

        // Step 2: Access the Revenue Details panel
        logger.info("Step 2: Accessing the 'Revenue Details' panel interface");
        vendorPage.navigateToRevenueDetails();
        logger.info("Revenue Details panel accessed");

        // Step 3: Verify revenue >= 0
        logger.info("Step 3: Verifying revenue is greater than or equal to zero");
        boolean isRevenueValid = vendorPage.isRevenueGreaterOrEqualToZero();
        logger.info("Revenue validation result: " + isRevenueValid);
        Assert.assertTrue(isRevenueValid, "Revenue must be greater than or equal to zero");
        logger.info("✓ Revenue is valid (≥ 0)");

        // Step 4: Get dashboard earnings and sum of revenue from years table
        logger.info("Step 4: Getting consolidated earnings from dashboard");
        double dashboardEarnings = vendorPage.getConsolidatedEarnings();
        logger.info("Dashboard consolidated earnings: " + dashboardEarnings);

        logger.info("Step 5: Calculating sum of revenue from years table");
        double sumRevenueFromTable = vendorPage.getSumOfRevenueFromYearsTable();
        logger.info("Sum of revenue from years table: " + sumRevenueFromTable);

        // Step 5: Verify that dashboard total equals sum of year table revenues
        logger.info("Step 6: Verifying dashboard revenue matches sum of year table revenues");
        double tolerance = 0.5; // Allow 0.5 currency unit difference due to rounding

        logger.info("Expected (Dashboard): " + dashboardEarnings);
        logger.info("Actual (Sum from Table): " + sumRevenueFromTable);
        logger.info("Tolerance: " + tolerance);

        Assert.assertEquals(dashboardEarnings, sumRevenueFromTable, tolerance,
                "The revenue details must add the revenue from the years table and check it equals total value");
        logger.info("✓ Dashboard revenue matches sum of year table revenues");

        // Additional Assertion: Verify consolidated earnings
        logger.info("Step 7: Additional verification - consolidated earnings");
        Assert.assertTrue(dashboardEarnings >= 0, "Consolidated earnings must be greater than or equal to zero");
        logger.info("✓ Consolidated earnings verified");

        logger.info("========== TC_051: PASSED ==========\n");
    }

    @Test(dataProvider = "vendorCredentials", dataProviderClass = VendorDataProvider.class,
            dependsOnMethods = "verifyVendorRevenue", groups = "Vendor")
    public void verifyMultipleTransactionsExist(String email, String password, String vendorId) throws Exception {
        logger.info("========== TC_051: Verify Multiple Customer Transactions ==========");

        VendorControls vendorPage = new VendorControls(driver);

        // Verify that multiple customer transaction tokens exist
        logger.info("Verifying multiple customer transaction tokens exist");

        double revenueSum = vendorPage.getSumOfCompletedOrdersForVendor(vendorId);
        logger.info("Revenue from completed orders: " + revenueSum);

        Assert.assertTrue(revenueSum > 0,
                "Multiple customer transaction tokens should exist (revenue from completed orders > 0)");
        logger.info("✓ Multiple customer transactions verified");

        logger.info("========== TC_051: Multiple Transactions PASSED ==========\n");
    }
}