package modulos.Produto;

import base.BaseTest;
import builder.ProdutoBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import model.Componente;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;



@DisplayName("Testes de API rest do modulo de Produto")

public class ProdutoTest extends BaseTest {


    @Test
    @DisplayName("Deve retornar erro quando o valor informado for igual a zero, status code 422")
    public void deveRetornarErroQuandoValorForZero(){




        //Insteri produto com valor zero
        Response response= given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .body(new ProdutoBuilder().comNome("play station 5").comValor (0.00).build())

        .when()
                .post("/v2/produtos");

        validarLimitesValorProduto(response);

    }

   @Test
   @DisplayName("Deve retornar erro quando o valor do produto for maior que 7001, status code 422")
    public void deveRetornarErroQuandoValorMaiorQue7001(){

        //inserir produto com valor maio que 7001
        Response response= given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .body(new ProdutoBuilder().comNome("play station 5").comValor (7001d).build())

        .when()
                .post("/v2/produtos");

        validarLimitesValorProduto(response);


   }

    @Test
    @DisplayName("Deve ser adicionado um novo produto com o valor limite máximo de 7000, status code 201")
    public void deveAdicionarNovoProdutoAoInformarValor7000(){

        given()
                .contentType(ContentType.JSON)
                .header("token", token)
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
                .body("data.componentes.find { it.componenteNome == 'controle' }.componenteQuantidade", equalTo(1))
                .body("data.componentes.find { it.componenteNome == 'Cabo' }.componenteQuantidade", equalTo(2))
                .body("data.componentes.componenteId", everyItem(notNullValue()));


    }

    @Test
    @DisplayName("Deve ser adicionado um novo produto com o valor limite minimo de 0.01, status code 201")
    public void deveAdicionarNovoProdutoAoInformarValor01(){

        given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .body(new ProdutoBuilder().comNome("play station 5").comValor(0.01).build())

                .when()
                .post("/v2/produtos")

                .then()
                .assertThat()
                .statusCode(201)
                .body("message", equalToIgnoringCase("Produto adicionado com sucesso"))
                .body("data.produtoId", is(notNullValue()))
                .body("data.produtoValor", equalTo(0.01f))
                .body("data.produtoNome", equalToIgnoringCase("play station 5"))
                .body("data.produtoCores", contains("Preto", "Branco"))
                .body("data.componentes.componenteId", is(notNullValue()))
                .body("data.componentes", hasSize(2))
                .body("data.componentes.componenteId", is(notNullValue()))
                .body("data.componentes.find { it.componenteNome == 'controle' }.componenteQuantidade", equalTo(1))
                .body("data.componentes.find { it.componenteNome == 'Cabo' }.componenteQuantidade", equalTo(2))
                .body("data.componentes.componenteId", everyItem(notNullValue()));


    }

    @Test
    @DisplayName("Dever permitir o cadastro de mais de um produto com nome e informações duplicadas ")
    public void permitirCadastroDeProdutoDuplicado(){
        Integer primeiroProdutoId;

       primeiroProdutoId=given()
                .contentType(ContentType.JSON)
                .header("token", token)
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
                .header("token", token)
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
                .header("token", token)
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
    @Disabled("BUG REPORTADO: Limite excedido no produtoNome retorna 201 (Deveria ser 422). Desabilitado até a correção.")
    @DisplayName("Não deve permitir cadastro com nome acima do limite de caracteres. Status 400 esperado.")
     public void deveFalharComNomeAcimaDoLimite(){
        String acimaDoLimite= "B".repeat(101);



        given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .body(new ProdutoBuilder().comNome(acimaDoLimite).comValor(555d).build())

        .when()
                .post("/v2/produtos")

        .then()
                .assertThat()
                .statusCode(422)
                .body("data", is(empty()));

    }

    @Test
    @Disabled("BUG REPORTADO: Deve falhar ao cadastrar produto com nome vazio, mas API retorna 201. Desabilitado até a correção")
    @DisplayName("Não deve permitir cadastro ao tentar cadastrar produto com nome vazio. Status 400 esperado")
    public void deveFalharComNomeVazio(){
        Response response = given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .body(new ProdutoBuilder().comNome("").build())

        .when()
                .post("/v2/produtos");

        validarErroCamposObrigatorios(response);


    }

    @Test
    @DisplayName("Deve retornar erro 400 ao tentar cadastrar produto com nome nulo/ausente")
    public void deveRetornarErroAoCadastrarComNomeNulo(){
        Response response= given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .body(new ProdutoBuilder().comNome(null).build())

        .when()
                .post("/v2/produtos");

        validarErroCamposObrigatorios(response);
    }

    @Test
    @DisplayName("Deve falhar ao cadastrar produto com valor nulo, status code 400")
    public void deveFalharAoCadastrarProdutoComValorVazio(){
        Response response = given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .body(new ProdutoBuilder().semValor().build())

        .when()
                .post("/v2/produtos");

        validarErroCamposObrigatorios(response);
    }


    @Test
    @Disabled("BUG REPORTADO: Deve falhar ao cadastrar produto com produtoCores vazio, mas API retorna 201. Desabilitado até a correção")
    @DisplayName("Não deve ser possível cadastrar produto com produtoCores vazio. Status 400 esperado")
    public void deveFalharComProdutoCoresVazio(){
        Response response = given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .body(new ProdutoBuilder().comCores(new ArrayList<>()).build())

        .when()
                .post("/v2/produtos");

        validarErroCamposObrigatorios(response);
    }

    @Test
    @DisplayName("Deve falhar ao cadastrar produto com produtoCores nulo/ausente, status 400")
    public void deveFalharAoCadastrarComprodutoCoresAusente(){
        Response response= given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .body(new ProdutoBuilder().semCores().build())
        .when()
                .post("/v2/produtos");

        validarErroCamposObrigatorios(response);

    }


    @Test
    @Disabled("BUG REPORTADO:Deve falhar ao cadastrar produto com produtoCores acima de 100 caracteres")
    @DisplayName("Não deve ser possíver cadastrar nome de produtoCores com mais de 100 caracteres")
    public void deveFalharComNomeCorAcimaDoLimite(){
        String corColisao = "C".repeat(100+1);

        given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .body(new ProdutoBuilder().comCores(List.of(corColisao)).build())
        .when()
                .post("/v2/produtos")
        .then()
                .assertThat()
                .statusCode(400)
                .body("data", empty())
                .body("error", not(emptyString()));
    }


    @Test
    @DisplayName("Deve falhar ao tentar cadastrar produtoValor com uma string, status code 422")
    public void deveFalharAoCadastrarProdutoComValorIgualString(){

        Map<String, Object> payloadInvalido = Map.of(
                "produtoNome", "Produto com String no Valor",
                "produtoValor", "ABC", // String no lugar do Double
                "produtoCores", List.of("Branco", "Preto")
        );

        Response response= given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .body(payloadInvalido)
        .when()
                .post("/v2/produtos");

        validarLimitesValorProduto(response);

    }



    @Test
    @Disabled("BUG REPORTADO: Deve falhar ao cadastrar componente com nome nulo/ausente e qunatidade acima de >1, mas API retorna 201 mesmo com dado incompleto")
    @DisplayName("Não deve ser possível cadastrar a quantidade de um componente sem o nome do componente")
    public void deveRetornarErroComComponenteNomeNulo(){

        Componente componenteIncompleto = new Componente();
        componenteIncompleto.setComponenteNome(null);
        componenteIncompleto.setComponenteQuantidade(1);

        List<Componente> componentes = List.of(componenteIncompleto);

        given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .body(new ProdutoBuilder().comComponentes(componentes).build())
        .when()
                .post("/v2/produtos")

        .then()
                .assertThat()
                .statusCode(400)
                .body("data", empty())
                .body("error", not(emptyString()));

    }

    @Test
    @DisplayName("Deve retornar erro 422 ao cadastrar componente com componenteQuantidade nula/ausente")
    public void deveRetornarErroComComponenteQuantidadeNula(){

        Componente componenteIncompleto = new Componente();
        componenteIncompleto.setComponenteNome("controle");
        componenteIncompleto.setComponenteQuantidade(null);

        List<Componente> componentes = new ArrayList<>();
        componentes.add(componenteIncompleto);

        Response response= given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .body(new ProdutoBuilder().comComponentes(componentes).build())
        .when()
                .post("/v2/produtos");

        validarErroQuantidadeMinimaComponentes(response);
    }

    @Test
    @DisplayName("Deve retornar erro 422 ao cadastrar componente com componenteQuantidade com valor inferior a 1")
    public void deveRetornarErroComComponenteQuantidadeInferiorAUm(){

        Componente componenteIncompleto = new Componente();
        componenteIncompleto.setComponenteNome("controle");
        componenteIncompleto.setComponenteQuantidade(0);

        List<Componente> componentes = new ArrayList<>();
        componentes.add(componenteIncompleto);

        Response response = given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .body(new ProdutoBuilder().comComponentes(componentes).build())
        .when()
                .post("/v2/produtos");

        validarErroQuantidadeMinimaComponentes(response);
    }

    @Test
    @Disabled("BUG REPORTADO: Limite superior de componenteQuantidade não é validado. Aceita 300000 e retorna 201")
    @DisplayName("Deve existir um limite maximo e minimo para componenteQuantidade")
    public void deveExistirValidacaoDeLimiteDeComponenteQuantidade(){

        final int QUANTIDADE_EXTREMA = 300000;

        Componente componenteExtremo = new Componente();
        componenteExtremo.setComponenteNome("caixa");
        componenteExtremo.setComponenteQuantidade(QUANTIDADE_EXTREMA);

        List<Componente> componentes = List.of(componenteExtremo);

        given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .body(new ProdutoBuilder().comComponentes(componentes).build())
        .when()
                .post("/v2/produtos")
        .then()
                .assertThat()
                // Confirma o BUG (Aceita o valor extremo com Sucesso)
                .statusCode(201)
                .body("data.componentes[0].componenteQuantidade", equalTo(QUANTIDADE_EXTREMA))
                .body("message", equalToIgnoringCase("Produto adicionado com sucesso"));
    }


    //metodo auxiliar
    private List<Map<String, Object>> criarComponenteComStringNaQuantidade(String quantidadeInvalida) {
        return List.of(
                Map.of(
                        "componenteNome", "controle",
                        "componenteQuantidade", quantidadeInvalida
                )
        );
    }
    @Test
    @Disabled("BUG - UX/Validação] Mensagem de erro incorreta ao enviar tipo de dado inválido para componenteQuantidade")
    @DisplayName("Não deve ser possível cadasrtrar componenteQuantidade com uma string")
    public void deveRetornarErroComComponenteQuantidadeString(){

        List<Map<String, Object>> payloadInvalido = criarComponenteComStringNaQuantidade("dois");

        Response response= given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .body(Map.of(
                        "produtoNome", "String Quantidade",
                        "produtoValor", 100.00,
                        "produtoCores", List.of("Azul"),
                        "componentes", payloadInvalido
                ))
        .when()
                .post("/v2/produtos");

        validarErroQuantidadeMinimaComponentes(response);
    }

    @Test
    @DisplayName("Deve retornar erro 422 ao cadastrar componente com componenteQuantidade negativa")
    public void deveRetornarErroComComponenteQuantidadeNegativa(){

        Componente componenteNegativo = new Componente();
        componenteNegativo.setComponenteNome("controle");
        componenteNegativo.setComponenteQuantidade(-1);

        List<Componente> componentes = List.of(componenteNegativo);

        Response response = given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .body(new ProdutoBuilder().comComponentes(componentes).build())
        .when()
                .post("/v2/produtos");

        validarErroQuantidadeMinimaComponentes(response);
    }

    @Test
    @DisplayName("Deve ser adicionado produto com UrlMock válida, status code 201")
    public void deveAdicionarProdutoComUrlMockValida(){

        given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .body(new ProdutoBuilder().build())
        .when()
                .post("/v2/produtos")
        .then()
                .assertThat()
                .statusCode(201)
                .body("message", equalToIgnoringCase("Produto adicionado com sucesso"))
                .body("data.produtoUrlMock", equalTo("http://www.teste-mock.com.br"));
    }

    @Test
    @DisplayName("Deve ser adicionado produto mesmo sem informar o UrlMock, status code 201")
    public void deveAdicionarProdutoSemUrlMock(){

        given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .body(new ProdutoBuilder().semUrlMock().build())
        .when()
                .post("/v2/produtos")
        .then()
                .assertThat()
                .statusCode(201)
                .body("message", equalToIgnoringCase("Produto adicionado com sucesso"))
                .body("data.produtoUrlMock", anyOf(nullValue(), emptyString()));
    }




    }
