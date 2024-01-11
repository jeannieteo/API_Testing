package utils;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RestAssuredListener implements Filter {
    //ceate logger object
    private static final Logger logger = LogManager.getLogger(RestAssuredListener.class);
    @Override
    public Response filter(FilterableRequestSpecification requestSpecification, FilterableResponseSpecification responseSpecification, FilterContext context) {
        Response response = context.next(requestSpecification,responseSpecification);

        //log when response code means failure
        if(response.getStatusCode() != 200 & response.getStatusCode() != 201)    {
            logger.error("\n Request Method: " + requestSpecification.getMethod() +
                    "\n UrI: "  + requestSpecification.getURI() +
                    "\n Request Body: "  + requestSpecification.getBody() +
                    "\n Response Body: "  + response.getBody() .prettyPrint()
            );
        }
        //when you want log even the passing requests
        //logger.info("\n Request Method: " + requestSpecification.getMethod() +
        //        "\n UrI: "  + requestSpecification.getURI() +
        //        "\n Request Body: "  + requestSpecification.getBody() +
        //        "\n Response Body: "  + response.getBody() .prettyPrint()
        //);
        return response;
    }
}
