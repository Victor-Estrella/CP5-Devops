package br.com.fiap.dunoke.control;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

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
}
