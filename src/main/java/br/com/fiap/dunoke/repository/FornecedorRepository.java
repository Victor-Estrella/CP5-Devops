package br.com.fiap.dunoke.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import br.com.fiap.dunoke.model.Fornecedor;

public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {

}
