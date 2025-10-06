package br.com.fiap.dunoke.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuario")
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String username;
	private String senha;
	private String imgPerfil;
	private String nomePerfil;
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "usuario_funcao_tab", 
	joinColumns = @JoinColumn(name = "id_usuario"), 
	inverseJoinColumns = @JoinColumn(name = "id_funcao"))
	private Set<Funcao> funcoes = new HashSet<Funcao>();

	public Usuario() {

	}
	
	

	public Usuario(Long id, String username, String senha, String imgPerfil, String nomePerfil, Set<Funcao> funcoes) {
		super();
		this.id = id;
		this.username = username;
		this.senha = senha;
		this.imgPerfil = imgPerfil;
		this.nomePerfil = nomePerfil;
		this.funcoes = funcoes;
	}



	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getImgPerfil() {
		return imgPerfil;
	}

	public void setImgPerfil(String imgPerfil) {
		this.imgPerfil = imgPerfil;
	}

	public String getNomePerfil() {
		return nomePerfil;
	}

	public void setNomePerfil(String nomePerfil) {
		this.nomePerfil = nomePerfil;
	}

	public Set<Funcao> getFuncoes() {
		return funcoes;
	}

	public void setFuncoes(Set<Funcao> funcoes) {
		this.funcoes = funcoes;
	}

}
