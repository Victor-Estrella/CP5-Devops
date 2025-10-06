package br.com.fiap.dunoke.control;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import br.com.fiap.dunoke.model.Funcao;
import br.com.fiap.dunoke.model.Usuario;
import br.com.fiap.dunoke.repository.FuncaoRepository;
import br.com.fiap.dunoke.repository.UsuarioRepository;

@Controller
public class UsuarioController {
	
	@Autowired
	private FuncaoRepository repF;
	
	@Autowired
	private PasswordEncoder encoder;
	
	@Autowired
	private UsuarioRepository repU;

	@GetMapping("/usuario/listar")
	public ModelAndView listarUsuarios() {
		ModelAndView mv = new ModelAndView("usuario/listar");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		repU.findByUsername(auth.getName()).ifPresent(u -> mv.addObject("usuario_logado", u));
		mv.addObject("usuarios", repU.findAll());
		mv.addObject("lista_funcoes", repF.findAll());
		return mv;
	}
	
	@GetMapping("/usuario/novo")
	public ModelAndView retornarCadUsuario() {
		// Removido slash inicial da view para manter padrão
		ModelAndView mv = new ModelAndView("usuario/novo");
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		Optional<Usuario> op = repU.findByUsername(auth.getName());
		
		if(op.isPresent()) {
			mv.addObject("usuario_logado", op.get());
		}
		
		mv.addObject("usuario", new Usuario());
		mv.addObject("lista_funcoes", repF.findAll());
		
		return mv;
		
	}
	
	@PostMapping("/insere_usuario")
	public ModelAndView inserirUsuario(Usuario usuario, @RequestParam(name = "id_funcao", required = false) Long id_funcao) {

		usuario.setSenha(encoder.encode(usuario.getSenha()));

		Set<Funcao> funcoes = new HashSet<Funcao>();

		if (id_funcao != null) {
			repF.findById(id_funcao).ifPresent(funcoes::add);
		}

		usuario.setFuncoes(funcoes);

		repU.save(usuario);

		return new ModelAndView("redirect:/index");

	}

	@GetMapping("/usuario/editar")
	public ModelAndView editarRedirect(){
		return new ModelAndView("redirect:/usuario/listar");
	}

	@GetMapping("/usuario/editar/{id}")
	public ModelAndView editarForm(@PathVariable Long id){
		Optional<Usuario> op = repU.findById(id);
		if(op.isEmpty()) return new ModelAndView("redirect:/usuario/listar");
		ModelAndView mv = new ModelAndView("usuario/edicao");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		repU.findByUsername(auth.getName()).ifPresent(u -> mv.addObject("usuario_logado", u));
		mv.addObject("usuario", op.get());
		mv.addObject("lista_funcoes", repF.findAll());
		return mv;
	}

	@PostMapping("/usuario/editar/{id}")
	public ModelAndView editarSalvar(@PathVariable Long id, Usuario usuario, @RequestParam(name="id_funcao", required = false) Long idFuncao){
		Optional<Usuario> op = repU.findById(id);
		if(op.isPresent()){
			Usuario atual = op.get();
			atual.setUsername(usuario.getUsername());
			if(usuario.getSenha()!=null && !usuario.getSenha().isBlank()){
				atual.setSenha(encoder.encode(usuario.getSenha()));
			}
			atual.setImgPerfil(usuario.getImgPerfil());
			atual.setNomePerfil(usuario.getNomePerfil());
			Set<Funcao> funcoes = new HashSet<>();
			if(idFuncao!=null) {
				repF.findById(idFuncao).ifPresent(funcoes::add);
			}
			atual.setFuncoes(funcoes);
			repU.save(atual);
		}
		return new ModelAndView("redirect:/usuario/listar");
	}

	@GetMapping("/usuario/remover/{id}")
	public ModelAndView remover(@PathVariable Long id){
		if(repU.findById(id).isPresent()){
			repU.deleteById(id);
		}
		return new ModelAndView("redirect:/usuario/listar");
	}
	
	
	

}
