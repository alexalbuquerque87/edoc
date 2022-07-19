package com.projeto.edoc.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.projeto.edoc.model.Documento;
import com.projeto.edoc.repository.DocumentoRepository;
	
	// Classe de serviços utilizados pelo controller
	@Service
	public class DocumentoService {
		
	// Número de documentos que serão exibidos por página
	private static final int itensPorPagina = 5;

	@Autowired
	private DocumentoRepository documentoRepository;
	
	// Localiza todos os documentos
	public List<Documento> findAll() {
		return documentoRepository.findAll();

	}
	
	// Busca nome do documento pela palavra pesquisada
	public List<Documento> findNome(String pesquisa) {
		return documentoRepository.findNome(pesquisa);

	}
	
	// Busca pela página clicada na paginação
	public Page<Documento> encontrarPagina(int paginaNumero) {
		Sort sort = Sort.by("nome").ascending();
		Pageable pageable = PageRequest.of(paginaNumero - 1, itensPorPagina, sort);
		return documentoRepository.findAll(pageable);
	}
	
	// Método utilizado para deletar documento baseado no ID
	public void deleteById(Integer id) {
		documentoRepository.deleteById(id);
	}
	
	// Método utilizado para salvar arquivo no banco de dados
	public List<Documento> upload(MultipartFile[] arquivo) throws IOException {
		List<String> fileNames = new ArrayList<String>();
		List<Documento> storedFile = new ArrayList<Documento>();

		for (MultipartFile file : arquivo) {
			
			// Busca se já existe um documento salvo
			Documento fileModel = documentoRepository.findByNome(file.getOriginalFilename());
			// Caso já exista, substitui o atual
			if (fileModel != null) {
				fileModel.setData(file.getBytes());
				
			// Caso não exista, cria um novo 
			} else {
				fileModel = new Documento(file.getOriginalFilename(), file.getContentType(), file.getBytes());
			}

			fileNames.add(file.getOriginalFilename());
			storedFile.add(fileModel);
		}

		// Salva o(s) arquivo(s)
		return documentoRepository.saveAll(storedFile);
	}
	
	// Busca nome do arquivo no banco ao clicar em abrir
	public Documento findByNome(String nome) {
		return documentoRepository.findByNome(nome);
	}

}
