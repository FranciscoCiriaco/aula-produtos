package br.senac.sp.produto.controller.api;

import br.senac.sp.produto.controller.ProdutoRequest;
import br.senac.sp.produto.model.Produto;
import br.senac.sp.produto.repository.ProdutoRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;


@RestController
@RequestMapping("produtos")
@Tag(name = "API - Produto Controller API",
        description = "Controller para tratar requisições de Produtos na API")

public class ProdutoControllerApi {
    private final ProdutoRepository produtoRepository;

    public ProdutoControllerApi(ProdutoRepository repository) {
        this.produtoRepository = repository;

    }
    @GetMapping("/get-produtos")
    @Operation(summary = "Recuperar Todos",
            description = "recuperar um produto da lista")
    public ResponseEntity<List<Produto>> recuperarTodos() {
        List<Produto> produtos = produtoRepository.findAll();
        System.out.println("Total de Produtos" + produtos.size());

        return ResponseEntity.ok(produtos);

    }

    @GetMapping("/get-produto/{idProduto}")
    @Operation(summary = "Recuperar id dos produtos",
            description = "recuperar ids ")
    public ResponseEntity<Produto> recuperarPorid(@PathVariable(name = "idProduto") Long id) {
        Produto produto = produtoRepository.findById(id).orElseThrow(() -> new RuntimeException("ID NAO LOCALIZADO"));
        System.out.println(produto);

        return ResponseEntity.ok(produto);

    }

    @PostMapping("/cadastrar")
    @Operation(summary = "cadastrar todos os produtos",
            description = "cadastrar produtos")
    public ResponseEntity<Produto> cadastrar(@RequestBody ProdutoRequest request) {
        var p = new Produto().setDescricao(request.getDescricao())
                .setPreco(request.getPreco())
                .setCodigoBarra(request.getCodigoBarra())
                .setLote(request.getLote())
                .setQuantidade(request.getQuantidade());

        var produtoCriado = produtoRepository.save(p);

        System.out.println(produtoCriado);

    return ResponseEntity.ok(produtoCriado);
    }

    @PutMapping("/atualizar/{idProduto}")
    @Operation(summary = "atualiza os produtos",
            description = "atualizar os ids dos produtos")
    public ResponseEntity<Produto> alterarProdutoTotal(@PathVariable(name = "idProduto") Long id, @RequestBody ProdutoRequest request) {

        if (Objects.isNull(request.getDescricao()) ||
                Objects.isNull(request.getPreco()) ||
                Objects.isNull(request.getQuantidade()) ||
                Objects.isNull(request.getLote()) ||
                Objects.isNull(request.getCodigoBarra())
        ) {
            throw new RuntimeException("OS ATRIBUTOS NAO PODEM SER NULOS");
        }

        Produto p = new Produto();


        var produtoOptional = produtoRepository.findById(id);
        if (produtoOptional.isEmpty()){
            throw new RuntimeException("PRODUTO NAO EXISTE");


        }
        p.setId(id);
        p.setDescricao(request.getDescricao());
        p.setLote(request.getLote());
        p.setPreco(request.getPreco());
        p.setQuantidade(request.getQuantidade());
        p.setCodigoBarra(request.getCodigoBarra());



        Produto produtoSalvo = produtoRepository.save(p);

        return ResponseEntity.ok().body(produtoSalvo);

    }
    @PatchMapping("/atualizar/{idProduto}")
    @Operation(summary = "atualiza os produtos",
            description = "atualizar lista de produtos")
    public ResponseEntity<Produto> alterarProdutoParcial
            (@PathVariable(name = "idProduto") Long id,
             @RequestBody ProdutoRequest request) {

        Produto produtoEntidade = new Produto();

        var produtoOptional = produtoRepository.findById(id);
        if (produtoOptional.isEmpty()){
            throw new RuntimeException("PRODUTO NAO EXISTE");


        }
        var produtoBancoDados = produtoOptional.get();

        produtoEntidade.setId(id);

        produtoEntidade.setDescricao(Objects.isNull(request.getDescricao())?
                produtoBancoDados.getDescricao() : request.getDescricao());

        produtoEntidade.setLote(Objects.isNull(request.getLote())?
                produtoBancoDados.getLote() : request.getLote());

        produtoEntidade.setPreco(Objects.isNull(request.getPreco())?
                produtoBancoDados.getPreco() : request.getPreco());

        produtoEntidade.setQuantidade(Objects.isNull(request.getQuantidade())?
                produtoBancoDados.getQuantidade() : request.getQuantidade());


        produtoEntidade.setCodigoBarra(Objects.isNull(request.getCodigoBarra())?
                produtoBancoDados.getCodigoBarra() : request.getCodigoBarra());



        Produto produtoatualizado = produtoRepository.save(produtoEntidade);

        return ResponseEntity.ok().body(produtoatualizado);
    }
    @DeleteMapping("/deletar/{idProduto}")
    @Operation(summary = "deletar produto",
            description = "deleta os ids dos produtos ")
    public ResponseEntity<Void> deletar(@PathVariable(name = "idProduto") Long id) {
        var produtoOptional = produtoRepository.findById(id);
        if (produtoOptional.isEmpty()) {
            throw new RuntimeException("PRODUTO NAO EXISTE");

        }

        produtoRepository.delete(produtoOptional.get());

        return ResponseEntity.noContent().build();


    }

    @DeleteMapping("/deleteall")
    @Operation(summary = "deleta toda a lista",
            description = "deletar todos os produtos")
    public ResponseEntity<Void> deleteall()

    {

        produtoRepository.deleteAll();

        return ResponseEntity.noContent().build();
    }
    @GetMapping("paginador")
    @Operation(summary = "paginar todos",
            description = "pagina as paginas")
    public ResponseEntity<Page<Produto>> getProdutosPaginado(
            @Parameter(description = "Numero da pagina", example = "0")
            @RequestParam(defaultValue = "0") int pagina,
            @Parameter(description = "Quantidade de itens na pagina", example = "10")
            @RequestParam(defaultValue = "10") int itens,
            @Parameter(description = "Atributo que sera ordenado", example = "descricao")
            @RequestParam(defaultValue = "id") String ordenarPor,
            @Parameter(description = "Ordem da ordenação", example = "asc")
            @RequestParam(defaultValue = "asc")String ordem
    ){
        var ordenaçao = ordem.equalsIgnoreCase("asc") ? Sort.by(ordenarPor).ascending():Sort.by(ordenarPor).descending();

        var paginador = PageRequest.of(pagina,itens,ordenaçao);

        var produtosPaginados = produtoRepository.findAll(paginador);

        return ResponseEntity.ok().body(produtosPaginados);

    }

    @GetMapping("/somar-precos/{lote}")
    @Operation(summary = "somar Todos",
            description = "soma os preços dos produtos")
    public ResponseEntity<Object> somarPrecosPorLote(@PathVariable(name = "lote") String lote) {

        List<Produto> produtosDoLote = produtoRepository.findByLote(lote);


        if (produtosDoLote.isEmpty()) {
            return ResponseEntity.notFound().build();
        }


        BigDecimal somaPrecos = produtosDoLote.stream()
                .map(Produto::getPreco)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        return ResponseEntity.ok(somaPrecos);
    }

    }