package builder;

import model.Componente;
import model.Produto;
import java.util.ArrayList;
import java.util.List;

public class ProdutoBuilder {
    private Produto produto;

    public ProdutoBuilder(){
        this.produto = new Produto();
        this.produto.setProdutoNome("Produto Padrão");
        this.produto.setProdutoValor (100.00);

        List<String> cores = new ArrayList<>();
        cores.add("Preto");
        cores.add("Branco");
        this.produto.setProdutoCores(cores);
        this.produto.setProdutoUrlMock("");



        List <Componente> componentes = new ArrayList<>();


        Componente componente1 = new Componente();
        componente1.setComponenteNome("controle");
        componente1.setComponenteQuantidade(1);
        componentes.add(componente1);

        Componente componente2 = new Componente();
        componente2.setComponenteNome("Cabo");
        componente2.setComponenteQuantidade(2);
        componentes.add(componente2);

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