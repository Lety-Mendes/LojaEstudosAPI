package base;

import builder.LoginBuilder;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;

import static io.restassured.RestAssured.*;

public class BaseTest {
    protected String token;
    @BeforeEach
    public void beforeEach(){
        //Configurações dos dados da API
        baseURI= "http://165.227.93.41";
        basePath= "/lojinha";

        //Obtenção do token
        this.token = given()
                .contentType(ContentType.JSON)
                .body(new LoginBuilder().builder())

                .when()
                .post("/v2/login")

                .then()
                .extract()
                .path("data.token");


    }
}
