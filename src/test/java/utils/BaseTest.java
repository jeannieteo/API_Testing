package utils;

import io.restassured.RestAssured;

import org.testng.annotations.BeforeMethod;

public class BaseTest {
    public String testUrl = "https://restful-booker.herokuapp.com/booking";
    @BeforeMethod
    public void setUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }


}
