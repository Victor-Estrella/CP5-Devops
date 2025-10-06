package br.com.fiap.dunoke.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

import java.util.Objects;

@Entity
@Table(name = "funcao")
public class Funcao {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "nome", nullable = false, length = 30)
	private EnumFuncao nome;

	public Funcao() {

	}

	public Funcao(Long id, EnumFuncao nome) {
		super();
		this.id = id;
		this.nome = nome;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public EnumFuncao getNome() {
		return nome;
	}

	public void setNome(EnumFuncao nome) {
		this.nome = nome;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Funcao funcao = (Funcao) o;
		return Objects.equals(id, funcao.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return "Funcao{" +
				"id=" + id +
				", nome=" + nome +
				'}';
	}

}
