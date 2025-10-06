package br.com.fiap.dunoke.service;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.fiap.dunoke.model.Usuario;
import br.com.fiap.dunoke.repository.UsuarioRepository;

@Service
public class UsuarioDetailsService implements UserDetailsService {
	
	@Autowired
	private UsuarioRepository repU;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Usuario usuario = repU.findByUsername(username)
				.orElseThrow( () -> new UsernameNotFoundException("Usuário não encontrado") );
		
		return new User(usuario.getUsername(), usuario.getSenha(),
				usuario.getFuncoes().stream()
				.map(funcao -> new SimpleGrantedAuthority(funcao.getNome().toString()))
				.collect(Collectors.toList()));
	}
	
	
	
	

}
