package br.com.fiap.dunoke.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.fiap.dunoke.model.Funcao;

@Repository
public interface FuncaoRepository extends JpaRepository<Funcao, Long> {

}
