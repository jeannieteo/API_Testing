package utils;

import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

public class BaseTest {

    @BeforeMethod
    public void setUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }
}
