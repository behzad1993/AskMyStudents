package de.htw.ai.web.askmystudents.config;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SecuredControllerRestTemplateIntegrationTest {

    @Autowired
    private TestRestTemplate template;

    @LocalServerPort
    int randomServerPort;


    private static final String WRONG_PASSWORD = "wrongPassword";
    private static final String VALID_PASSWORD = "user";
    private static final String VALID_USERNAME = "user@user.com";
    private static final String WRONG_USERNAME = "wrongUsername";
    private static final String INDEX_URL = "/teacher/courses";
    private static final String WRONG_URL = "/teacher/tes";


    @Test
    public void givenAuthRequestOnPrivateService_shouldFailWith401() throws Exception {
        final ResponseEntity<String> result = this.template.withBasicAuth(WRONG_USERNAME, WRONG_PASSWORD)
                .getForEntity(INDEX_URL, String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
    }

    @Test
    public void givenAuthRequestOnPrivateService_shouldSucceedWith200() throws Exception {
        final ResponseEntity<String> result = this.template.withBasicAuth(VALID_USERNAME, VALID_PASSWORD)
                .getForEntity(INDEX_URL, String.class);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void givenAuthRequestOnPrivateService_shouldFailWith404() throws Exception {
        final ResponseEntity<String> result = this.template.withBasicAuth(VALID_USERNAME, VALID_PASSWORD)
                .getForEntity(WRONG_URL, String.class);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    public void getRootURI() throws Exception {
        final String port = "http://localhost:" + this.randomServerPort;
        final String rootUri = this.template.getRootUri();
        assertEquals(port, rootUri);
    }
}