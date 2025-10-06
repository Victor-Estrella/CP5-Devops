package br.com.fiap.dunoke.control;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;


@Controller
public class TesteController {
	
	//1
	@GetMapping("/teste1")
	public String testar1() {
		return "/testes/teste";
	}
	
	//2
	@GetMapping("/teste2")
	public String testar2(HttpServletRequest req){
		req.setAttribute("var", "FIAP!!!");
		return "/testes/teste";
	}
	
	//3
	@GetMapping("/teste3")
	public String testar3(Model md) {
		md.addAttribute("var", "ALURA!!!");
		return "/testes/teste";
	}

	//4
	@GetMapping("/teste4")
	public ModelAndView testar4(){
		ModelAndView mv = new ModelAndView("/testes/teste");
		mv.addObject("var","PM3!!!");
		return mv;
	}

}
