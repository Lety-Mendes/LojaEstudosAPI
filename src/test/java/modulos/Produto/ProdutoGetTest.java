package modulos.Produto;

import base.BaseTest;
import builder.LoginBuilder;
import builder.ProdutoBuilder;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import model.Login;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;



@DisplayName("Testes de API rest do modulo de Produto")

public class ProdutoGetTest extends BaseTest {
    private static Integer produtoId1;
    private static Integer produtoId2;

    @DisplayName("Testes de API de Consulta (GET) do módulo de Produto")
     @BeforeAll
      public static void setupGetProduto() {


         produtoId1 = given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .body(new ProdutoBuilder().comNome("Produto 01").build())

        .when()
                .post("v2/produtos")

        .then()
                .statusCode(201)
                .extract()
                .path("data.produtoId");

        produtoId2 = given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .body(new ProdutoBuilder().comNome("Produto 02").build())

        .when()
                .post("v2/produtos")

        .then()
                .statusCode(201)
                .extract()
                .path("data.produtoId");
      }

    @Test
    @DisplayName("GET /produtos: Deve retornar 401 ao tentar listar produtos sem o token de autenticação")
    void deveRetornarErro404AoListarSemToken() {
        given()
                .contentType(ContentType.JSON)
        .when()
                .get("v2/produtos")
        .then()
                .statusCode(401);
    }

    @Test
    @DisplayName("GET /produtos: Deve retornar 200 e incluir o produto cadastrado na lista")
    void deveListarProdutosCadastradosComSucesso() {

        given()
                .contentType(ContentType.JSON)
                .header("token", token)

        .when()
                .get("v2/produtos")

        .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("data.produtoId", hasItems(produtoId1, produtoId2));
    }

    @Test
    @DisplayName("GET /produtos/{produtoId}: Deve retornar 200 e os dados corretos de um produto pelo ID")
    void deveRetornarDadosDoProdutoAoBuscarPorId() {

        given()
                .contentType(ContentType.JSON)
                .header("token", token)
        .when()
                .get("v2/produtos/" + produtoId1)
        .then()
            .assertThat()
                .statusCode(200)
                .body("data.produtoId", equalTo(produtoId1))
                .body("data.produtoNome", is(notNullValue()))
                .body("data.produtoValor", is(notNullValue()))
                .body("data.produtoCores", is(not(empty())))
                .body("data.componentes", is(not(emptyArray())));
    }


   @Test
    @DisplayName("GET /produtos: Deve retornar 200 e lista vazia para um usuário sem produtos")
    void deveRetornarListaVaziaComLimpeza() {

        limparDadosDoUsuarioPadrao();

        given()
                .contentType(ContentType.JSON)
               .header("token", token)
        .when()
               .get("v2/produtos")
       .then()
                .statusCode(200)
                .body("data", empty());
    }


}



