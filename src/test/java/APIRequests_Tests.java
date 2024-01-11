import com.jayway.jsonpath.JsonPath;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.apache.commons.io.FileUtils;
import utils.BaseTest;
import utils.FileNameConstants;

import java.io.File;
import java.io.IOException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import utils.RestAssuredListener;


public class APIRequests_Tests extends BaseTest {

    private static final Logger logger = LogManager.getLogger(APIRequests_Tests.class);


    @Test
    public void get_all_bookings()   {
        logger.info("get_all_bookings Test Start");
        Response response =
        RestAssured
                .given().filter(new RestAssuredListener())
                    .contentType(ContentType.JSON)
                    .baseUri(testUrl)
                    //.log().headers() //log body to console
                .when()
                    .get()
                .then()
                    .assertThat()
                    .statusCode(200)
                    .statusLine("HTTP/1.1 200 OK")
                    .header("Content-Type", "application/json; charset=utf-8")
                    .extract()
                        .response();
        Assert.assertTrue(response.getBody().asString().contains("bookingid"));

    }

    @Test
    public void post_booking()  {
        logger.info("post_booking Test Start");
        //prepare request body
        //1.use json object
        JSONObject booking = new JSONObject();
        JSONObject bookingDates = new JSONObject();

        booking.put("firstname","Jason");
        booking.put("lastname","Bourne");
        booking.put("totalprice",999);
        booking.put("depositpaid","true");
        booking.put("additionalneeds", "killer looks");
        booking.put("bookingdates", bookingDates);

        bookingDates.put("checkin","2024-01-04");
        bookingDates.put("checkout","2024-01-08");

        //do rest
        Response response =
        RestAssured
                .given().filter(new RestAssuredListener())
                    .contentType(ContentType.JSON)
                    .body(booking.toString())
                    .baseUri(testUrl)
                .when()
                    .post()
                .then()
                    .assertThat()
                    //.log().ifValidationFails() //log body to console
                    .statusCode(200)
                    .body("booking.firstname", Matchers.equalTo("Jason"))
                    .body("booking.totalprice", Matchers.equalTo(999))
                    .body("booking.bookingdates.checkin", Matchers.equalTo("2024-01-04"))
                .extract()
                    .response();

        int bookingId = response.path("bookingid");
        //pass the bookid from the response body to another API
        RestAssured
                .given().filter(new RestAssuredListener())
                    .contentType(ContentType.JSON)
                    .pathParams("bookingID", bookingId)
                    .baseUri(testUrl)
                .when()
                    .get("{bookingID}")
                .then()
                    .assertThat()
                    .statusCode(200)
                    .body("firstname", Matchers.equalTo("Jason")); //response


    }

    @Test
    public void post_booking_file() throws IOException {
        logger.info("post_booking_file Test Start");
        String post_body = FileUtils.readFileToString(new File(FileNameConstants.post_api_request_body), "UTF-8");

        Response response =
        RestAssured
                .given().filter(new RestAssuredListener())
                    .contentType(ContentType.JSON)
                    .body(post_body)
                    .baseUri(testUrl)
                .when()
                    .post()
                .then()
                    .assertThat()
                    .statusCode(200)
                    .body("booking.firstname",Matchers.equalTo("Gandalf"))
                .extract()
                    .response();

        JSONArray jsonArray = JsonPath.read(response.body().asString(), "$.booking..firstname");
        String firstName = (String) jsonArray.get(0);
        Assert.assertEquals(firstName, "Gandalf");

        JSONArray jsonArray2 = JsonPath.read(response.body().asString(), "$.booking..lastname");
        String lastName = (String) jsonArray2.get(0);
        Assert.assertEquals(lastName, "theGrey");

        JSONArray jsonArray3 = JsonPath.read(response.body().asString(), "$.booking.bookingdates..checkin");
        String checkinDate = (String) jsonArray3.get(0);
        Assert.assertEquals(checkinDate, "2024-01-01");

        //take the id from the response
        int bookingIDRetrieved = JsonPath.read(response.body().asString(),"$.bookingid");
        //get the schema from schema file
        String jsonSchema = FileUtils.readFileToString(new File(FileNameConstants.booking_schema), "UTF-8");

        RestAssured
                .given().filter(new RestAssuredListener())
                    .contentType(ContentType.JSON)
                    .baseUri(testUrl)
                .when()
                    .get("/{bookingId}", bookingIDRetrieved)
                .then()
                    .assertThat()
                    .statusCode(200)
                    .body(JsonSchemaValidator.matchesJsonSchema(jsonSchema));
    }

    @Test
    public void put_booking_request() throws IOException {
        logger.info("put_booking_request Test Start");
        String post_body = FileUtils.readFileToString(new File(FileNameConstants.post_api_request_body), "UTF-8");
        String put_body = FileUtils.readFileToString(new File(FileNameConstants.put_api_request_body), "UTF-8");
        String token_body = FileUtils.readFileToString(new File(FileNameConstants.token_request), "UTF-8");
        //POST FIRST
        Response response =
                RestAssured
                        .given().filter(new RestAssuredListener())
                        .contentType(ContentType.JSON)
                        .body(post_body)
                        .baseUri(testUrl)
                        .when()
                        .post()
                        .then()
                        .assertThat()
                        .statusCode(200)
                        .body("booking.firstname",Matchers.equalTo("Gandalf"))
                        .extract()
                        .response();
        //Get the ID from the response
        Integer bookingIDRetrieved = JsonPath.read(response.body().asString(),"$.bookingid");
        //GET REQUEST
        RestAssured
                .given().filter(new RestAssuredListener())
                    .contentType(ContentType.JSON)
                    .baseUri(testUrl)
                .when()
                    .get("/{bookingId}", bookingIDRetrieved)
                .then()
                    .assertThat()
                        .statusCode(200);
        //generate Token
        String tokenId = get_token(token_body);
        //PUT API request

        RestAssured
                .given().filter(new RestAssuredListener())
                    .baseUri(testUrl)
                    .contentType(ContentType.JSON)
                    .header("Cookie", "token=" +tokenId)
                    .body(put_body)
                .when()
                    .put("{bookingId}", bookingIDRetrieved)
                .then()
                    .assertThat()
                    .statusCode(200)
                    .body("firstname", Matchers.equalTo("Frodo"))
                    .body("lastname", Matchers.equalTo("Baggins"));

    }

    @Test
    public void patch_booking_request ()  throws IOException {
        logger.info("patch_booking_file Test Start");
        String post_body = FileUtils.readFileToString(new File(FileNameConstants.post_api_request_body), "UTF-8");
        //String put_body = FileUtils.readFileToString(new File(FileNameConstants.put_api_request_body), "UTF-8");
        String patch_body = FileUtils.readFileToString(new File(FileNameConstants.patch_api_request_body), "UTF-8");
        String token_body = FileUtils.readFileToString(new File(FileNameConstants.token_request), "UTF-8");
        //POST FIRST
        Response response =
                RestAssured
                        .given().filter(new RestAssuredListener())
                        .contentType(ContentType.JSON)
                        .body(post_body)
                        .baseUri(testUrl)
                        .when()
                        .post()
                        .then()
                        .assertThat()
                        .statusCode(200)
                        .body("booking.firstname",Matchers.equalTo("Gandalf"))
                        .extract()
                        .response();
        //Get the ID from the response
        Integer bookingIDRetrieved = JsonPath.read(response.body().asString(),"$.bookingid");

        //GET REQUEST
        RestAssured
                .given().filter(new RestAssuredListener())
                .contentType(ContentType.JSON)
                .baseUri(testUrl)
                .when()
                .get("/{bookingId}", bookingIDRetrieved)
                .then()
                .assertThat()
                .statusCode(200);
        //generate Token

        String tokenId = get_token(token_body);

        //PUT API request

        RestAssured
                .given().filter(new RestAssuredListener())
                .baseUri(testUrl)
                .contentType(ContentType.JSON)
                .header("Cookie", "token=" + tokenId)
                .body(patch_body)
                .when()
                .patch("{bookingId}", bookingIDRetrieved)
                .then()
                .assertThat()
                .statusCode(200)
                .body("lastname", Matchers.equalTo("the Grey"));

    }
    @Test
    public void delete_booking() throws IOException{
        logger.info("delete_booking_file Test Start");
        String token_body = FileUtils.readFileToString(new File(FileNameConstants.token_request), "UTF-8");
        String post_body = FileUtils.readFileToString(new File(FileNameConstants.post_api_request_body), "UTF-8");
        //POst first
        Response response =
                RestAssured
                        .given().filter(new RestAssuredListener())
                        .contentType(ContentType.JSON)
                        .body(post_body)
                        .baseUri(testUrl)
                        .when()
                        .post()
                        .then()
                        .assertThat()
                        .statusCode(200)
                        .body("booking.firstname",Matchers.equalTo("Gandalf"))
                        .extract()
                        .response();
        //Get the ID from the response
        Integer bookingIDRetrieved = JsonPath.read(response.body().asString(),"$.bookingid");

        //GET REQUEST
        RestAssured
                .given().filter(new RestAssuredListener())
                .contentType(ContentType.JSON)
                .baseUri(testUrl)
                .when()
                .get("/{bookingId}", bookingIDRetrieved)
                .then()
                .assertThat()
                .statusCode(200);
        //generate Token

        String token = get_token(token_body);
        RestAssured
                .given().filter(new RestAssuredListener())
                .contentType(ContentType.JSON)
                .header("Cookie", "token="+token)
                .baseUri(testUrl)
                .when()
                .delete("/{bookingId}", bookingIDRetrieved)
                .then()
                .assertThat()
                .statusCode(201);
    }

    public String get_token(String token_body) {
        //generate Token
        Response token_response =
                RestAssured
                        .given().filter(new RestAssuredListener())
                        .contentType(ContentType.JSON)
                        .body(token_body)
                        .baseUri("https://restful-booker.herokuapp.com/auth")
                        .when()
                        .post()
                        .then()
                        .assertThat()
                        .statusCode(200)
                        .extract()
                        .response();
        return JsonPath.read(token_response.body().asString(), "$.token");

    }
}
