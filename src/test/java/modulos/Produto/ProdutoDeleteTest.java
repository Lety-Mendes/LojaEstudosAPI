package modulos.Produto;

import base.BaseTest;
import builder.ProdutoBuilder;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ProdutoDeleteTest extends BaseTest {
    private Integer produtoId;

    @BeforeEach
    public void setupDeleteTest() {

        produtoId = given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .body(new ProdutoBuilder().comNome("Produto Temporario para Delete").build())
        .when()
                .post("v2/produtos")
        .then()
                .statusCode(201)
                .extract()
                .path("data.produtoId");
    }


    @Test
    @DisplayName("DELETE /produtos/{produtoId}: Deve retornar 204 após exclusão de produto existente")
    void deveExcluirProdutoComSucessoERetornar204() {

        given()
                .header("token", token)
        .when()
                .delete("v2/produtos/" + produtoId)
        .then()
                .statusCode(204);

        given()
                .header("token", token)
        .when()
                .get("v2/produtos/" + produtoId)
        .then()
                .statusCode(404);
    }


    @Test
    @DisplayName("DELETE /produtos/{produtoId}: Deve retornar 404 ao tentar excluir produto que já foi removido")
    void deveRetornar404AoExcluirProdutoInexistente() {

        given()
                .header("token", token)
        .when()
                .delete("v2/produtos/" + produtoId)
        .then()
                .statusCode(204);


        given()
                .header("token", token)
                .when()
                .delete("v2/produtos/" + produtoId)
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("DELETE /produtos/{produtoId}: Deve retornar 401 ao tentar excluir sem token de autenticação")
    void deveRetornar401AoExcluirSemToken() {

        given()
                .when()
                .delete("v2/produtos/" + produtoId)
                .then()
                .statusCode(401);
    }

    @Test
    @DisplayName("DELETE /v2/dados: Deve limpar todos os dados do usuário e retornar 204")
    void deveLimparDadosDoUsuarioComSucesso() {
        given()
                .header("token", token)
        .when()
                .delete("v2/dados")
        .then()
                .statusCode(204);

        given()
                .contentType(ContentType.JSON)
                .header("token", token)

        .when()
                .get("v2/produtos")

        .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("data", empty());
    }
}
