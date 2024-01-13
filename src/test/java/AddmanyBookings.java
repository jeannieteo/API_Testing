import com.jayway.jsonpath.JsonPath;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import net.minidev.json.JSONArray;
import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import utils.FileNameConstants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class AddmanyBookings {

    @Test(dataProvider = "json_booking_data")
    public void testingUsingJson (LinkedHashMap<String, String> testData)  {
        System.out.println("firstname: " + testData.get("firstname"));
        System.out.println("lastname: " + testData.get("lastname"));
    }

    @DataProvider(name="json_booking_data")
    public Object[] getTestDataFromJson() throws IOException {
        Object[] objData = null;
        //open json file and read contents to String
        String json_data = FileUtils.readFileToString(new File(FileNameConstants.json_test_data), "UTF-8");
        //put the String into an JSON ARRAY
        JSONArray jsonArray = JsonPath.read(json_data, "$"); //just dollar is needed
        //put each Json into an object
        objData = new Object[jsonArray.size()];
        for(int i = 0; i <jsonArray.size(); i++)   {
            objData[i] = jsonArray.get(i);
        }
        return objData;
    }

    @Test(dataProvider = "csv_test_booking_data")
    public void test_csv(Map<String,String> testData)  {
        System.out.println(testData.get("firstname"));
        System.out.println(testData.get("lastname"));
    }

    @DataProvider(name="csv_test_booking_data")
    public Object [][] getTestData() {
        //ref variables
        Object[][] ObjArray = null; //from csv
        Map<String, String> map = null; //each row
        List<Map<String, String>> testDataList = null;
        //read csv file
        try {
            CSVReader csvreader = new CSVReader(new FileReader(FileNameConstants.csv_test_data));
            //create this to store the data
            testDataList = new ArrayList<Map<String, String>>();

            String[] lineByline = null; //to scan in line

            lineByline = csvreader.readNext();//skip header
            while((lineByline = csvreader.readNext()) != null) {//not end of file

                map = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
                map.put("firstname", lineByline[0]);
                map.put("lastname", lineByline[1]);
                map.put("totalprice", lineByline[2]);

                testDataList.add(map);
            }//end while
            ObjArray = new Object[testDataList.size()][1];

            for(int i =0; i < testDataList.size(); i++)   {
                ObjArray[i][0] = testDataList.get(i);
            }
        }
        catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
        //end read

        return  ObjArray;
    }


}
