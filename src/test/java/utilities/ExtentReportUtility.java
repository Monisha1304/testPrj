package utilities;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import testBase.BaseClass;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ExtentReportUtility implements ITestListener {
    public ExtentSparkReporter extentSparkReporter;
    public ExtentReports extentReports;
    public ExtentTest extentTest;
    public String filePath;

    public void onStart(ITestContext context){
        String timeStamp=new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        filePath="Test-Report-"+timeStamp+".html";
        extentSparkReporter=new ExtentSparkReporter(".\\reports\\"+filePath);
        extentSparkReporter.config().setDocumentTitle("");
        extentSparkReporter.config().setReportName("");
        extentSparkReporter.config().setTheme(Theme.DARK);

        extentReports=new ExtentReports();
        extentReports.attachReporter(extentSparkReporter);
        extentReports.setSystemInfo("OS",context.getCurrentXmlTest().getParameter("os"));
        extentReports.setSystemInfo("Browser",context.getCurrentXmlTest().getParameter("browser"));
        extentReports.setSystemInfo("Application","");

        List<String> groupsList=context.getCurrentXmlTest().getIncludedGroups();
        if(!groupsList.isEmpty()){
            extentReports.setSystemInfo("Group Tests",groupsList.toString());
        }

    }

    public void onTestSuccess(ITestResult testResult){
        extentTest=extentReports.createTest(testResult.getTestClass().getName());
        extentTest.assignCategory(testResult.getMethod().getGroups());
        extentTest.log(Status.PASS,testResult+" got Passed");
    }

    public void onTestFailure(ITestResult testResult){
        extentTest=extentReports.createTest(testResult.getTestClass().getName());
        extentTest.assignCategory(testResult.getMethod().getGroups());
        extentTest.log(Status.FAIL,testResult.getName()+" got Failed");
        if (testResult.getThrowable() != null) {
            extentTest.log(Status.INFO, testResult.getThrowable().getMessage());
        }

        try {
            String imgPath = null;
            Object testInstance = testResult.getInstance();
            if (testInstance instanceof testBase.BaseClass) {
                testBase.BaseClass base = (testBase.BaseClass) testInstance;
                // Only attempt screenshot if driver is available
                if (testBase.BaseClass.driver != null) {
                    try {
                        imgPath = base.captureScreen(testResult.getName());
                    } catch (Exception e) {
                        // log to console - keep listener robust
                        e.printStackTrace();
                    }
                }
            } else {
                // Fallback: if BaseClass.driver (static) is set, try to use it via a new BaseClass instance
                if (testBase.BaseClass.driver != null) {
                    try {
                        imgPath = new testBase.BaseClass().captureScreen(testResult.getName());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            if (imgPath != null) {
                extentTest.addScreenCaptureFromPath(imgPath);
            } else {
                extentTest.log(Status.INFO, "Screenshot not available");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onFinish(ITestContext testContext){
        extentReports.flush();
        String pathOfReport=System.getProperty("user.dir")+"\\reports\\"+filePath;
        File report=new File(pathOfReport);
        try {
            Desktop.getDesktop().browse(report.toURI());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
