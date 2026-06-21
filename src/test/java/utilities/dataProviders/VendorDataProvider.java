package utilities.dataProviders;

import org.testng.annotations.DataProvider;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class VendorDataProvider {

    /**
     * Load config.properties from classpath (preferred) with a filesystem fallback.
     */
    private static Properties loadConfigProperties() throws Exception {
        Properties properties = new Properties();

        // Try classpath first (works in IDE and Maven runs when file is in src/test/resources)
        InputStream is = VendorDataProvider.class.getClassLoader().getResourceAsStream("config.properties");
        if (is != null) {
            try (InputStream in = is) {
                properties.load(in);
                return properties;
            }
        }

        // Fallback to known filesystem path (useful in some IDE/workdir setups)
        try (FileInputStream fis = new FileInputStream("src/test/resources/config.properties")) {
            properties.load(fis);
            return properties;
        }
    }

    /**
     * Data provider for vendor login credentials
     * Returns: email, password, vendorId
     */
    @DataProvider(name = "vendorCredentials")
    public static Object[][] vendorCredentials() throws Exception {
        Properties properties = loadConfigProperties();

        String email = properties.getProperty("vendor.email", "vendor@company.com");
        String password = properties.getProperty("vendor.password", "password123");
        String vendorId = properties.getProperty("vendor.id", "VENDOR001");

        return new Object[][] {
                { email, password, vendorId }
        };
    }

    /**
     * Data provider with multiple vendor credentials (example rows).
     * Returns multiple sets of email, password, vendorId.
     */
    @DataProvider(name = "multipleVendorCredentials")
    public static Object[][] multipleVendorCredentials() throws Exception {
        // Use primary credentials as one of the rows, plus an additional sample row.
        Object[][] primary = vendorCredentials();

        return new Object[][] {
                { primary[0][0], primary[0][1], primary[0][2] },
                { "vendor2@example.com", "password2", "VENDOR002" }
        };
    }

    /**
     * Data provider for nav button names to test
     */
    @DataProvider(name = "navButtonNames")
    public static Object[][] navButtonNames() {
        return new Object[][] {
                { "Dashboard" },
                { "Orders" },
                { "Revenue" },
                { "Settings" },
                { "Profile" }
        };
    }
}