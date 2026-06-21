package utilities;

import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.DataProvider;

import java.io.IOException;

public class DataProviders {
    ExcelUtility excelUtility;
    String filePath="";
    String sheetName="";

    @DataProvider(name = "LoginData")
    public Object[][] getLoginData() throws IOException {
        excelUtility=new ExcelUtility(filePath);
        int rows=excelUtility.getRowCount(sheetName);
        int cells=excelUtility.getCellCount(sheetName,0);
        String[][] loginData=new String[rows][cells];
        for(int i=1;i<rows;i++){
            for(int j=0;j<cells;j++){
                loginData[i-1][j]=excelUtility.getCellData(sheetName,i,j);
            }
        }
        return loginData;
    }
}
