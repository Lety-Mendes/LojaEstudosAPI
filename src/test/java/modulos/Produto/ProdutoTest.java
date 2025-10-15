package modulos.Produto;

import base.BaseTest;
import builder.ProdutoBuilder;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import org.hamcrest.Matcher;



@DisplayName("Testes de API rest do modulo de Produto")

public class ProdutoTest extends BaseTest {

    @Test
    @DisplayName("Deve retornar erro quando o valor informado for igual a zero, status code 422")

    public void deveRetornarErroQuandoValorForZero(){




        //Insteri produto com valor zero
        given()
                .contentType(ContentType.JSON)
                .header("token", this.token)
                .body(ProdutoBuilder.produtoComumComValorIgualA("play station 5",0.00))

        .when()
                .post("/v2/produtos")

        .then()
                .assertThat()
                   .body("error", equalToIgnoringCase("O valor do produto deve estar entre R$ 0,01 e R$ 7.000,00"))
                    .statusCode(422);

    }
@Test
@DisplayName("Deve retornar erro quando o valor do produto for maior que 7001, status code 422")
    public void deveRetornarErroQuandoValorMaiorQue7001(){

        //inserir produto com valor maio que 7001
        given()
                .contentType(ContentType.JSON)
                .header("token", this.token)
                .body(ProdutoBuilder.produtoComumComValorIgualA("play station 5",7001))

        .when()
                .post("/v2/produtos")

        .then()
                .assertThat()
                .body("error", equalToIgnoringCase("O valor do produto deve estar entre R$ 0,01 e R$ 7.000,00"))
                .statusCode(422);

    }

@Test
@DisplayName("Deve ser adicionado um novo produto com o valor limite máximo de 7000, status code 201")
    public void deveAdicionarNovoProdutoAoInformarValor7000(){

        given()
                .contentType(ContentType.JSON)
                .header("token", this.token)
                .body(ProdutoBuilder.produtoComumComValorIgualA("play station 5",7000))

        .when()
                .post("/v2/produtos")

        .then()
                .assertThat()
                .statusCode(201)
                .body("message", equalToIgnoringCase("Produto adicionado com sucesso"))
                .body("data.produtoId", is(notNullValue()))
                .body("data.produtoValor", equalTo(7000))
                .body("data.produtoNome", equalToIgnoringCase("play station 5"))
                .body("data.produtoCores", contains("Preto", "Branco"))
                .body("data.componentes.componenteId", is(notNullValue()))
                .body("data.componentes", hasSize(2))
                .body("data.componentes", hasItems(
                        allOf(
                                (Matcher) hasEntry("componenteNome", "controle"),
                                (Matcher) hasEntry("componenteQuantidade", 1)
                        ),
                        allOf(
                                (Matcher) hasEntry("componenteNome", "Cabo"),
                                (Matcher)  hasEntry("componenteQuantidade", 2)
                        )
                ));


    }

    @Test
    @DisplayName("Deve ser adicionado um novo produto com o valor limite minimo de 0.01, status code 201")
    public void deveAdicionarNovoProdutoAoInformarValor01(){

        given()
                .contentType(ContentType.JSON)
                .header("token", this.token)
                .body(ProdutoBuilder.produtoComumComValorIgualA("play station 5", 0.01))

                .when()
                .post("/v2/produtos")

                .then()
                .assertThat()
                .statusCode(201)
                .body("message", equalToIgnoringCase("Produto adicionado com sucesso"))
                .body("data.produtoId", is(notNullValue()))
                .body("data.produtoValor", equalTo(Float.valueOf("0.01")))
                .body("data.produtoNome", equalToIgnoringCase("play station 5"))
                .body("data.produtoCores", contains("Preto", "Branco"))
                .body("data.componentes.componenteId", is(notNullValue()))
                .body("data.componentes", hasSize(2))
                .body("data.componentes", hasItems(
                        allOf(
                                (Matcher) hasEntry("componenteNome", "controle"),
                                (Matcher) hasEntry("componenteQuantidade", 1)
                        ),
                        allOf(
                                (Matcher) hasEntry("componenteNome", "Cabo"),
                                (Matcher)  hasEntry("componenteQuantidade", 2)
                        )
                ));


    }

    @Test
    @DisplayName("Dever permitir o cadastro de mais de um produto com nome e informações duplicadas ")
    public void permitirCadastroDeProdutoDuplicado(){
        Integer primeiroProdutoId;

       primeiroProdutoId=given()
                .contentType(ContentType.JSON)
                .header("token", this.token)
                .body(ProdutoBuilder.produtoComumComValorIgualA("Televisão",600))

        .when()
                .post("/v2/produtos")

        .then()
                .assertThat()
                .statusCode(201)
                .body("data.produtoId", is(notNullValue()))
                .body("data.produtoNome", equalTo("Televisão"))
                .body("data.produtoValor", equalTo(600))
                .body("message", equalToIgnoringCase("Produto adicionado com sucesso"))
                .extract().path("data.produtoId");




        given()
                .contentType(ContentType.JSON)
                .header("token", this.token)
                .body(ProdutoBuilder.produtoComumComValorIgualA("Televisão", 600))

        .when()
                .post("/v2/produtos")

        .then()
                .body("data.produtoId", is(notNullValue()))
                .body("data.produtoNome", equalTo("Televisão"))
                .body("data.produtoValor", equalTo(600))
                .body("message", equalToIgnoringCase("Produto adicionado com sucesso"))
                .body("data.produtoId", not(equalTo(primeiroProdutoId)));


    }

    @Test
    @DisplayName("Deve permitir o cadastro do produto com o limite maximo de 100 caracteres, status code 201")

    public void limiteMaximo(){
      String limiteCaracters= "A".repeat(100);
        given()
                .contentType(ContentType.JSON)
                .header("token", this.token)
                .body(ProdutoBuilder.produtoComumComValorIgualA(limiteCaracters, 600))

        .when()
                .post("/v2/produtos")

        .then()
                .assertThat()
                .statusCode(equalTo(201))
                .body("data.produtoNome", equalTo(limiteCaracters))
                .body("data.produtoValor", equalTo(600));


    }

    @Test
    @DisplayName("BUG REPORTADO: Limite excedido retorna 201 e dados incorretos (Deveria ser 422)")

     public void deveFalharComNomeAcimaDoLimiteMasRetornaBug(){
        String dentroDoLimite= "A".repeat(100);
        String acimaDoLimite= "B".repeat(100+1);

        given()
                .contentType(ContentType.JSON)
                .header("token", this.token)
                .body(ProdutoBuilder.produtoComumComValorIgualA(acimaDoLimite, 544))

        .when()
                .post("/v2/produtos")

        .then()
                .assertThat()
                //Validação do Bug: a API retorna 201 (sucesso)
                .statusCode(201)
                //Validação da resposta incorreta: O nome retornado não é o enviado
                .body("data.produtoNome", not(equalTo(acimaDoLimite)))
                //Validação de que um nome de um outro produto já cadastrado anteriormente é retornado
                .body("data.produtoNome", equalTo(dentroDoLimite));


    }
}
