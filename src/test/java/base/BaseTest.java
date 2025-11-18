package base;

import builder.LoginBuilder;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.is;

public class BaseTest {
    protected static String token;
    @BeforeAll
    protected static void setupBase(){
        //Configurações dos dados da API
        baseURI= "http://165.227.93.41";
        basePath= "/lojinha";

        //Obtenção do token
        token = given()
                .contentType(ContentType.JSON)
                .body(new LoginBuilder().builder())

                .when()
                .post("/v2/login")

                .then()
                .extract()
                .path("data.token");


    }


    //Metodo utilitário para validar o erro de campos obrigatórios
    protected void validarErroCamposObrigatorios(io.restassured.response.Response response) {
        response.then()
                .statusCode(400)
                .body("error", equalToIgnoringCase("produtoNome, produtoValor e produtoCores são campos obrigatórios"))
                .body("data", empty());
    }

    //Metodo utilitário para validar erro de quantidade mínima de componentes
    protected void validarErroQuantidadeMinimaComponentes(io.restassured.response.Response response){
        response.then()
                .statusCode(422)
                .body("error", containsStringIgnoringCase("A quantidade mínima para os componentes não devem ser inferiores a 1"))
                .body("data", is(empty()));
    }

    //Metodo utilitário para validar erro de limites do valor do produto
    protected void validarLimitesValorProduto(io.restassured.response.Response response){
        response.then()
                .statusCode(422)
                .body("error", equalToIgnoringCase("O valor do produto deve estar entre R$ 0,01 e R$ 7.000,00"))
                .body("data", is(empty()));
    }

    //Metodo para ser reutilizado em qualquer teste que precise de uma base limpa
    protected void limparDadosDoUsuarioPadrao() {
        given()
                .header("token", token)
        .when()
                .delete("v2/dados")
        .then()
                .statusCode(204);
    }
}
