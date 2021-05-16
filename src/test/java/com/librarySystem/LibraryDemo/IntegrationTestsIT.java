package com.librarySystem.LibraryDemo;

import com.librarySystem.LibraryDemo.entityBeans.LibraryBean;
import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@SpringBootTest
public class IntegrationTestsIT {
    // Tests are being written using TestRestTemplate or you
    // can use Rest Assured for the same.
    @Test
    public void getAuthorNamedBooksTest() throws JSONException {
        String expected = "[\n" +
                "    {\n" +
                "        \"book_name\": \"Dummy tutorial\",\n" +
                "        \"id\": \"JJHmdhfJDS12578\",\n" +
                "        \"isbn\": \"JJHmdhfJDS\",\n" +
                "        \"aisle\": 12578,\n" +
                "        \"author\": \"Ram\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"book_name\": \"SQL tutorial\",\n" +
                "        \"id\": \"IUIHNKJG98798\",\n" +
                "        \"isbn\": \"IUIHNKJG\",\n" +
                "        \"aisle\": 98798,\n" +
                "        \"author\": \"Ram\"\n" +
                "    }\n" +
                "]";
        TestRestTemplate restTemplate = new TestRestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8888/getBooks/author?authorName=Ram", String.class);
        int statusCode = response.getStatusCodeValue();
        Assertions.assertEquals(200, statusCode, "Verify Status Code");
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    @Test
    public void addBookIntegrationTest() {
        TestRestTemplate restTemplate = new TestRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LibraryBean> request = new HttpEntity<>(buildLibrary(), headers);
        ResponseEntity response = restTemplate.postForEntity("http://localhost:8888/addBook", request, String.class);
        Assertions.assertEquals(201, response.getStatusCodeValue(), "Verify Status Code");
        Assertions.assertEquals(buildLibrary().getId(),response.getHeaders().get("uniqueId").get(0));
        // You can assert other values as well, like validating response body
    }

    private static LibraryBean buildLibrary() {
        LibraryBean libraryBean = new LibraryBean();
        libraryBean.setBook_name("SampleBook");
        libraryBean.setAuthor("Lee");
        libraryBean.setAisle(24);
        libraryBean.setIsbn("ABCYU");
        libraryBean.setId("ABCYU24");
        return libraryBean;
    }
}
