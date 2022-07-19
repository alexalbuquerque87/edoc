package com.projeto.edoc.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.projeto.edoc.model.Documento;

public interface DocumentoRepository extends JpaRepository<Documento, Integer> {
	public Documento findByNome(String nome);
	
	// Query utilizada para busca no banco de dados
	@Query(value = "SELECT * FROM documento WHERE nome LIKE %?1%",  nativeQuery = true)
	public List<Documento> findNome(String pesquisa);
}
