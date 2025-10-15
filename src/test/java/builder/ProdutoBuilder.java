package builder;

import model.Componente;
import model.Produto;

import java.util.ArrayList;
import java.util.List;

public class ProdutoBuilder {

    public static Produto produtoComumComValorIgualA(String nomeProduto, double valor){

        Produto produto = new Produto();
        produto.setProdutoNome(nomeProduto);
        produto.setProdutoValor (valor);

        List<String> cores = new ArrayList<>();
        cores.add("Preto");
        cores.add("Branco");

        produto.setProdutoCores(cores);
        produto.setProdutoUrlMock("");



        List <Componente> componentes = new ArrayList<>();

        Componente componente = new Componente();
        componente.setComponenteNome("controle");
        componente.setComponenteQuantidade(1);

        componentes.add(componente);

        Componente segundoComponente = new Componente();
        segundoComponente.setComponenteNome("Cabo");
        segundoComponente.setComponenteQuantidade(2);

        componentes.add(segundoComponente);

        produto.setComponentes(componentes);




        return produto;
    }


}
