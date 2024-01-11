package utils;

import io.restassured.RestAssured;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.PrintWriter;
import java.io.StringWriter;

public class BaseTest {
    //create logger reference variable
    private static final Logger logger = LogManager.getLogger(BaseTest.class);

    public String testUrl = "https://restful-booker.herokuapp.com/booking";
    @BeforeMethod
    public void setUp() {

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        //Write error stack to log file if test fails
        if(result.getStatus() == ITestResult.FAILURE)    {
            //if result is fail
            Throwable t = result.getThrowable(); //get the throwable from the result

            StringWriter error = new StringWriter(); //convert stack into string
            t.printStackTrace(new PrintWriter(error));

            logger.info(error.toString());
        }
    }

}
