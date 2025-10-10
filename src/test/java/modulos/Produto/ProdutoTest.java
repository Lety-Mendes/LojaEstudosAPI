package Modulos.Produto;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pojo.UsuarioPojo;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

@DisplayName("Testes de API rest do modulo de Produto")
public class ProdutoTest {
    private String token;

    @BeforeEach
    public void beforeEach(){
        //Configurações dos dados da API
        baseURI= "http://165.227.93.41";
        basePath= "/lojinha";

        UsuarioPojo usuario = new UsuarioPojo();
        usuario.setUsuarioLogin("carlos_43@ymail.com");
        usuario.setUsuarioSenha("123456");

        //Obtenção do token
        this.token = given()
                .contentType(ContentType.JSON)
                .body(usuario)

                .when()
                .post("/v2/login")

                .then()
                .extract()
                .path("data.token");

    }

    @Test
    @DisplayName("Validar limites do valor do produto")

    public void testValidarLimiteValor(){


        //Insteri produto com valor zero
        given()
                .contentType(ContentType.JSON)
                .header("token", this.token)
                .body("{\n" +
                        "  \"produtoNome\": \"aprendi\",\n" +
                        "  \"produtoValor\": 0.00,\n" +
                        "  \"produtoCores\": [\n" +
                        "    \"Amarelo\"\n" +
                        "  ],\n" +
                        "  \"produtoUrlMock\": \"\",\n" +
                        "  \"componentes\": [\n" +
                        "    {\n" +
                        "      \"componenteNome\": \"fazendo\",\n" +
                        "      \"componenteQuantidade\": 1\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}")

        .when()
                .post("/v2/produtos")

        .then()
                .assertThat()
                   .body("error", equalToIgnoringCase("O valor do produto deve estar entre R$ 0,01 e R$ 7.000,00"))
                    .statusCode(422);

    }

}
