package modulos.Produto;

import base.BaseTest;
import builder.ProdutoBuilder;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;




@DisplayName("Testes de API rest do modulo de Produto")

public class ProdutoTest extends BaseTest {

    @Test
    @DisplayName("Deve retornar erro quando o valor informado for igual a zero, status code 422")

    public void deveRetornarErroQuandoValorForZero(){




        //Insteri produto com valor zero
        given()
                .contentType(ContentType.JSON)
                .header("token", this.token)
                .body(new ProdutoBuilder().comNome("play station 5").comValor (0.00).build())

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
                .body(new ProdutoBuilder().comNome("play station 5").comValor (7001d))

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
                .body(new ProdutoBuilder().comNome("play station 5").comValor (7000d).build())

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
                .body("data.componentes", containsInAnyOrder( // Mantenha containsInAnyOrder
                        allOf(
                                hasEntry("componenteNome", equalTo("controle")), // Chave como String, Valor como Matcher
                                hasEntry("componenteQuantidade", equalTo("1"))
                        ),
                        allOf(
                                hasEntry("componenteNome", equalTo("Cabo")),
                                hasEntry("componenteQuantidade", equalTo("2"))
                        )
                ));


    }

    @Test
    @DisplayName("Deve ser adicionado um novo produto com o valor limite minimo de 0.01, status code 201")
    public void deveAdicionarNovoProdutoAoInformarValor01(){

        given()
                .contentType(ContentType.JSON)
                .header("token", this.token)
                .body(new ProdutoBuilder().comNome("play station 5").comValor (0.01).build())

                .when()
                .post("/v2/produtos")

                .then()
                .assertThat()
                .statusCode(201)
                .body("message", equalToIgnoringCase("Produto adicionado com sucesso"))
                .body("data.produtoId", is(notNullValue()))
                .body("data.produtoValor", closeTo(0.01, 0.001 ))
                .body("data.produtoNome", equalToIgnoringCase("play station 5"))
                .body("data.produtoCores", contains("Preto", "Branco"))
                .body("data.componentes.componenteId", is(notNullValue()))
                .body("data.componentes", hasSize(2))
                .body("data.componentes", containsInAnyOrder(
                        allOf(
                                 hasEntry("componenteNome", "controle"),
                                hasEntry("componenteQuantidade", "1")
                        ),
                        allOf(
                                hasEntry("componenteNome", "Cabo"),
                                 hasEntry("componenteQuantidade", "2")
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
                .body(new ProdutoBuilder().comNome("Televisão").comValor(600d).build())

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
                .body(new ProdutoBuilder().comNome("Televisão").comValor(600d).build())

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
    @DisplayName("Deve permitir o cadastro do nome do produto com o limite maximo de 100 caracteres, status code 201")

    public void limiteMaximo(){
      String limiteCaracters= "A".repeat(100);
        given()
                .contentType(ContentType.JSON)
                .header("token", this.token)
                .body(new ProdutoBuilder ().comNome(limiteCaracters).comValor(600d).build())

        .when()
                .post("/v2/produtos")

        .then()
                .assertThat()
                .statusCode(equalTo(201))
                .body("data.produtoNome", equalTo(limiteCaracters))
                .body("data.produtoValor", equalTo(600));


    }

    @Test
    @DisplayName("BUG REPORTADO: Limite excedido no produtoNome retorna 201 e dados incorretos (Deveria ser 422)")

     public void deveFalharComNomeAcimaDoLimiteMasRetornaBug(){
        String dentroDoLimite= "A".repeat(100);
        String acimaDoLimite= "B".repeat(100+1);

        given()
                .contentType(ContentType.JSON)
                .header("token", this.token)
                .body(new ProdutoBuilder().comNome(acimaDoLimite).comValor(555d).build())

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

    @Test
    @DisplayName("BUG REPORTADO: Deve falhar ao cadastrar produto com nome vazio, mas API retorna 201")
    public void deveFalharComNomeVazioMasRetornaBug(){
        given()
                .contentType(ContentType.JSON)
                .header("token", this.token)
                .body(new ProdutoBuilder().comNome("").build())

        .when()
                .post("/v2/produtos")

        .then()
                .assertThat()
                // Confirma o BUG: A API retorna 201 (Sucesso)
                .statusCode(201)
                // Valida que o nome foi aceito como vazio ("")
                .body("data.produtoNome", emptyString())
                .body("message", equalToIgnoringCase("Produto adicionado com sucesso"));
    }

    @Test
    @DisplayName("Deve retornar erro 400 ao tentar cadastrar produto com nome nulo/ausente")
    public void deveRetornarErroAoCadastrarComNomeNulo(){
        given()
                .contentType(ContentType.JSON)
                .header("token", this.token)
                .body(new ProdutoBuilder().comNome(null).build())

                .when()
                .post("/v2/produtos")

                .then()
                .assertThat()
                .statusCode(400)
                .body("error", equalToIgnoringCase("produtoNome, produtoValor e produtoCores são campos obrigatórios"))
                .body("data", is(empty()));
    }

    @Test
    @DisplayName("Deve falhar ao cadastrar produto com valor nulo, status code 400")
    public void deveFalharAoCadastrarProdutoComValorVazio(){
        given()
                .contentType(ContentType.JSON)
                .header("token", this.token)
                .body(new ProdutoBuilder().semValor().build())

        .when()
                .post("/v2/produtos")

        .then()
                .statusCode(400)
                .body("error", equalToIgnoringCase("produtoNome, produtoValor e produtoCores são campos obrigatórios"))
                .body("data", is(empty()));
    }

       @Test
    @DisplayName("BUG REPORTADO: Deve falhar ao cadastrar produto com produtoCores vazio, mas API retorna 201")
    public void deveFalharComProdutoCoresVazioMasRetornaBug(){
        given()
                .contentType(ContentType.JSON)
                .header("token", this.token)
                .body(new ProdutoBuilder().comCores(new ArrayList<>()).build())

                .when()
                .post("/v2/produtos")

                .then()
                .assertThat()
                // Confirma o BUG: A API retorna 201 (Sucesso)
                .statusCode(201)
                //Confirma o outro BUG: O array de cores contém uma string vazia
                .body("data.produtoCores", contains(""))// Asserção CORRETA: Espera um array com a string vazia
                .body("message", equalToIgnoringCase("Produto adicionado com sucesso"));
    }

    @Test
    @DisplayName("Deve falhar ao cadastrar produto com produtoCores nulo/ausente, status 400")
    public void deveFalharAoCadastrarComprodutoCoresAusente(){
        given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .body(new ProdutoBuilder().semCores().build())
        .when()
                .post("/v2/produtos")
        .then()
                .assertThat()
                .statusCode(400)
                .body("error", equalToIgnoringCase("produtoNome, produtoValor e produtoCores são campos obrigatórios"))
                .body("data", empty());

    }


    @Test
    @DisplayName("BUG REPORTADO:Deve falhar ao cadastrar produto com produtoCores acima de 100 caracteres, mas API retona o ultimo produto cadastrado com produtoCores com até 100 caracteres")
    public void deveFalharComNomeDeCorAcimaDoLimiteMasRetornaProdutoAnterior(){


        String corTruncada = "C".repeat(100);
        String corColisao = "C".repeat(101);

        //Cadastrar o Produto de 100 caracteres
        Integer produtoIdEsperado;

        String nomeUnico = "Produto Colisao " + System.currentTimeMillis();

        produtoIdEsperado = given()
                .contentType(ContentType.JSON)
                .header("token", this.token)
                .body(new ProdutoBuilder().comNome(nomeUnico).comCores(List.of(corTruncada)).build())
                .when()
                .post("/v2/produtos")
                .then()
                .statusCode(201)
                .extract().path("data.produtoId");

        // Tentar cadastrar um produto com produtoCor com mais de 100 caracteres
        given()
                .contentType(ContentType.JSON)
                .header("token", this.token)
                .body(new ProdutoBuilder().comNome(nomeUnico).comCores(List.of(corColisao)).build())
                .when()
                .post("/v2/produtos")
                .then()
                .assertThat()
                // Confirma o BUG: A API retorna 201
                .statusCode(201)
                // O ID retornado é o ID do produto cadastrado anteriormente com produtoCor com o limite de 100 caracters
                .body("data.produtoId", equalTo(produtoIdEsperado))
                .body("data.produtoCores", contains(corTruncada))
                .body("message", equalToIgnoringCase("Produto adicionado com sucesso"));
    }



    // Metodo auxiliar para criar um payload inválido
    private Map<String, Object> criarPayloadComValorInvalido(String valorInvalido) {
        return Map.of(
                "produtoNome", "Produto com String no Valor",
                "produtoValor", valorInvalido,
                "produtoCores", List.of("Branco", "Preto"),
                "produtoUrlMock", "",
                "componentes", List.of(
                        Map.of("componenteNome", "controle", "componenteQuantidade", 1)
                )
        );
    }

    @Test
    @DisplayName("Deve falhar ao tentar cadastrar produtoValor com uma string, status code 422")
    public void deveFalharAoCadastrarProdutoComValorIgualString(){

        Map<String, Object> payloadInvalido = criarPayloadComValorInvalido("ABC");

        given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .body(payloadInvalido)
        .when()
                .post("/v2/produtos")
        .then()
                .assertThat()
                .statusCode(422)
                .body("error", equalToIgnoringCase("O valor do produto deve estar entre R$ 0,01 e R$ 7.000,00"))
                .body("data", empty());
    }

}
