package builder;

import model.Componente;
import model.Produto;

import java.util.ArrayList;
import java.util.List;

public class ProdutoBuilder {
    private static Produto produto;

    public ProdutoBuilder(){

        Produto produto = new Produto();
        this.produto.setProdutoNome("nomeProduto");
        this.produto.setProdutoValor (100.00);

        List<String> cores = new ArrayList<>();
        cores.add("Preto");
        cores.add("Branco");

        this.produto.setProdutoCores(cores);
        this.produto.setProdutoUrlMock("");



        List <Componente> componentes = new ArrayList<>();

        Componente componente = new Componente();
        componente.setComponenteNome("controle");
        componente.setComponenteQuantidade(1);

        componentes.add(componente);

        Componente segundoComponente = new Componente();
        segundoComponente.setComponenteNome("Cabo");
        segundoComponente.setComponenteQuantidade(2);

        componentes.add(segundoComponente);

        this.produto.setComponentes(componentes);

    }

    public ProdutoBuilder comNome(String nome){
        this.produto.setProdutoNome(nome);
        return this;
    }

    public ProdutoBuilder comValor(double valor){
        this.produto.setProdutoValor(valor);
        return this;
    }

    public ProdutoBuilder comComponentes(List<Componente> componentes){
        this.produto.setComponentes(componentes);
        return this;
    }

    public Produto build(){
        return this.produto;
    }


}
