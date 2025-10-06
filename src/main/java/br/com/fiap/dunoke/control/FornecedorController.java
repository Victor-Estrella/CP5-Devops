package br.com.fiap.dunoke.control;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import br.com.fiap.dunoke.model.Fornecedor;
import br.com.fiap.dunoke.model.Usuario;
import br.com.fiap.dunoke.repository.FornecedorRepository;
import br.com.fiap.dunoke.repository.UsuarioRepository;
import jakarta.validation.Valid;

@Controller
public class FornecedorController {

    @Autowired
    private FornecedorRepository repF;

    @Autowired
    private UsuarioRepository repU;

    @GetMapping("/fornecedor/listar")
    public ModelAndView listar() {
        ModelAndView mv = new ModelAndView("fornecedor/listar");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<Usuario> op = repU.findByUsername(auth.getName());
        if (op.isPresent()) {
            mv.addObject("usuario", op.get());
        }

        mv.addObject("fornecedores", repF.findAll());
        return mv;
    }

    @GetMapping("/fornecedor/novo")
    public ModelAndView novo() {
        ModelAndView mv = new ModelAndView("fornecedor/novo");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<Usuario> op = repU.findByUsername(auth.getName());
        if (op.isPresent()) {
            mv.addObject("usuario", op.get());
        }

        mv.addObject("fornecedor", new Fornecedor());
        return mv;
    }

    @PostMapping("/fornecedor/salvar")
    public ModelAndView salvar(@Valid Fornecedor fornecedor, BindingResult bd) {
        if (bd.hasErrors()) {
            ModelAndView mv = new ModelAndView("fornecedor/novo");
            mv.addObject("fornecedor", fornecedor);
            return mv;
        }

        if (fornecedor.getDataCadastro() == null) {
            fornecedor.setDataCadastro(LocalDate.now());
        }

        repF.save(fornecedor);
        return new ModelAndView("redirect:/index");
    }

    @GetMapping("/fornecedor/detalhes/{id}")
    public ModelAndView detalhes(@PathVariable Long id) {
        Optional<Fornecedor> op = repF.findById(id);
        if (op.isPresent()) {
            ModelAndView mv = new ModelAndView("fornecedor/detalhes");
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Optional<Usuario> opU = repU.findByUsername(auth.getName());
            if (opU.isPresent()) {
                mv.addObject("usuario", opU.get());
            }
            mv.addObject("fornecedor", op.get());
            return mv;
        } else {
            return new ModelAndView("redirect:/index");
        }
    }

    @GetMapping("/fornecedor/editar/{id}")
    public ModelAndView editarPagina(@PathVariable Long id) {
        Optional<Fornecedor> op = repF.findById(id);
        if (op.isPresent()) {
            ModelAndView mv = new ModelAndView("fornecedor/edicao");
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Optional<Usuario> opU = repU.findByUsername(auth.getName());
            if (opU.isPresent()) {
                mv.addObject("usuario", opU.get());
            }
            mv.addObject("fornecedor", op.get());
            return mv;
        } else {
            return new ModelAndView("redirect:/index");
        }
    }

    @PostMapping("/fornecedor/editar/{id}")
    public ModelAndView editar(@PathVariable Long id, @Valid Fornecedor fornecedor, BindingResult bd) {
        if (bd.hasErrors()) {
            ModelAndView mv = new ModelAndView("fornecedor/edicao");
            mv.addObject("fornecedor", fornecedor);
            return mv;
        }

        Optional<Fornecedor> op = repF.findById(id);
        if (op.isPresent()) {
            Fornecedor atual = op.get();
            atual.setNome(fornecedor.getNome());
            atual.setCnpj(fornecedor.getCnpj());
            atual.setContato(fornecedor.getContato());
            atual.setEmail(fornecedor.getEmail());
            atual.setTelefone(fornecedor.getTelefone());
            atual.setEndereco(fornecedor.getEndereco());
            repF.save(atual);
        }

        return new ModelAndView("redirect:/index");
    }

    @GetMapping("/fornecedor/remover/{id}")
    public ModelAndView remover(@PathVariable Long id) {
        Optional<Fornecedor> op = repF.findById(id);
        if (op.isPresent()) {
            repF.deleteById(id);
        }
        return new ModelAndView("redirect:/index");
    }

}
