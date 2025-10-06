package br.com.fiap.dunoke.control;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;

import br.com.fiap.dunoke.model.Fornecedor;
import br.com.fiap.dunoke.model.Produto;
import br.com.fiap.dunoke.repository.FornecedorRepository;
import br.com.fiap.dunoke.repository.ProdutoRepository;
import jakarta.validation.Valid;

@Controller
public class ProdutoController {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private FornecedorRepository fornecedorRepository;

    @GetMapping("/fornecedor/{idFornecedor}/produto/novo")
    public ModelAndView novo(@PathVariable Long idFornecedor){
        ModelAndView mv = new ModelAndView("produto/novo");
        Optional<Fornecedor> f = fornecedorRepository.findById(idFornecedor);
        if (f.isEmpty()) {
            return new ModelAndView("redirect:/fornecedor/listar");
        }
        Produto p = new Produto();
        p.setFornecedor(f.get());
        mv.addObject("fornecedor", f.get());
        mv.addObject("produto", p);
        return mv;
    }

    @PostMapping("/fornecedor/{idFornecedor}/produto/salvar")
    public ModelAndView salvar(@PathVariable Long idFornecedor, @Valid Produto produto, BindingResult br){
        Optional<Fornecedor> f = fornecedorRepository.findById(idFornecedor);
        if (f.isEmpty()) {
            return new ModelAndView("redirect:/fornecedor/listar");
        }
        if (br.hasErrors()) {
            ModelAndView mv = new ModelAndView("produto/novo");
            mv.addObject("fornecedor", f.get());
            mv.addObject("produto", produto);
            return mv;
        }
        produto.setFornecedor(f.get());
        produtoRepository.save(produto);
        return new ModelAndView("redirect:/fornecedor/detalhes/" + idFornecedor);
    }

    // FORMULÁRIO DE EDIÇÃO (FRONT)
    @GetMapping("/fornecedor/{idFornecedor}/produto/editar/{idProduto}")
    public ModelAndView editarProdutoForm(@PathVariable Long idFornecedor, @PathVariable Long idProduto){
        Optional<Fornecedor> f = fornecedorRepository.findById(idFornecedor);
        if(f.isEmpty()) return new ModelAndView("redirect:/fornecedor/listar");
        Optional<Produto> p = produtoRepository.findById(idProduto);
        if(p.isEmpty()) return new ModelAndView("redirect:/fornecedor/detalhes/" + idFornecedor);
        ModelAndView mv = new ModelAndView("produto/edicao");
        mv.addObject("fornecedor", f.get());
        mv.addObject("produto", p.get());
        return mv;
    }

    @PostMapping("/fornecedor/{idFornecedor}/produto/editar/{idProduto}")
    public ModelAndView editarProdutoSalvar(@PathVariable Long idFornecedor, @PathVariable Long idProduto, @Valid Produto produto, BindingResult br){
        Optional<Fornecedor> f = fornecedorRepository.findById(idFornecedor);
        if(f.isEmpty()) return new ModelAndView("redirect:/fornecedor/listar");
        Optional<Produto> pOp = produtoRepository.findById(idProduto);
        if(pOp.isEmpty()) return new ModelAndView("redirect:/fornecedor/detalhes/" + idFornecedor);
        if(br.hasErrors()){
            ModelAndView mv = new ModelAndView("produto/edicao");
            mv.addObject("fornecedor", f.get());
            mv.addObject("produto", produto);
            return mv;
        }
        Produto atual = pOp.get();
        atual.setNome(produto.getNome());
        atual.setDescricao(produto.getDescricao());
        atual.setPreco(produto.getPreco());
        // dataCadastro permanece a original
        produtoRepository.save(atual);
        return new ModelAndView("redirect:/fornecedor/detalhes/" + idFornecedor);
    }

    @GetMapping("/fornecedor/{idFornecedor}/produto/remover/{idProduto}")
    public ModelAndView removerProduto(@PathVariable Long idFornecedor, @PathVariable Long idProduto){
        Optional<Produto> p = produtoRepository.findById(idProduto);
        if(p.isPresent()){
            produtoRepository.deleteById(idProduto);
        }
        return new ModelAndView("redirect:/fornecedor/detalhes/" + idFornecedor);
    }

    // --- API REST PUT e DELETE ---
    @PutMapping("/api/produto/{id}")
    public ModelAndView atualizarProduto(@PathVariable Long id, @RequestBody Produto produto) {
        Optional<Produto> op = produtoRepository.findById(id);
        if (op.isEmpty()) {
            return new ModelAndView("redirect:/fornecedor/listar");
        }
        Produto atual = op.get();
        atual.setNome(produto.getNome());
        atual.setDescricao(produto.getDescricao());
        atual.setPreco(produto.getPreco());
        atual.setDataCadastro(produto.getDataCadastro());
        // Não permite trocar fornecedor por API
        produtoRepository.save(atual);
        return new ModelAndView("redirect:/fornecedor/detalhes/" + atual.getFornecedor().getId());
    }

    @DeleteMapping("/api/produto/{id}")
    public ModelAndView deletarProduto(@PathVariable Long id) {
        Optional<Produto> op = produtoRepository.findById(id);
        if (op.isPresent()) {
            Long idFornecedor = op.get().getFornecedor().getId();
            produtoRepository.deleteById(id);
            return new ModelAndView("redirect:/fornecedor/detalhes/" + idFornecedor);
        }
        return new ModelAndView("redirect:/fornecedor/listar");
    }
}
