package br.com.fiap.dunoke.control;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import br.com.fiap.dunoke.model.Usuario;
import br.com.fiap.dunoke.repository.FornecedorRepository;
import br.com.fiap.dunoke.repository.UsuarioRepository;

@Controller
public class HomeController {

    @Autowired
    private FornecedorRepository repF;

    @Autowired
    private UsuarioRepository repU;

    @GetMapping({"/", "/index"})
    public ModelAndView index() {
        ModelAndView mv = new ModelAndView("/home/index");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<Usuario> op = repU.findByUsername(auth.getName());
        if (op.isPresent()) {
            mv.addObject("usuario", op.get());
        }

        mv.addObject("fornecedores", repF.findAll());
        return mv;
    }

}
