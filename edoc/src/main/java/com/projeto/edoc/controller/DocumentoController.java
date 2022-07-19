package com.projeto.edoc.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.projeto.edoc.model.Documento;
import com.projeto.edoc.service.DocumentoService;

@Controller // Informa que classe DocumentoController é nosso controller
@ControllerAdvice // Anotação utilizada pelo handler
@RequestMapping("/edoc")
public class DocumentoController {
	@Autowired // Nos comunica com UserRepository
	private DocumentoService documentoService;
	
	// Direciona para página inicial
	@GetMapping("/documentos")
	public String listaDocumentos(Model model) {
		return listaPagina(model, 1);

	}
	
	// Exibição dos documentos de acordo com a página
	@GetMapping("/documentos/pagina/{numeroDaPagina}")
	public String listaPagina(Model model, @PathVariable("numeroDaPagina") int paginaAtual) {
		Page<Documento> pagina = documentoService.encontrarPagina(paginaAtual);
		int totalPaginas = pagina.getTotalPages();
		long totalItens = pagina.getTotalElements();
		List<Documento> documento = pagina.getContent();
		model.addAttribute("paginaAtual", paginaAtual);
		model.addAttribute("totalPaginas", totalPaginas);
		model.addAttribute("totalItens", totalItens);
		model.addAttribute("documento", documento);

		return "index";
	}
	
	// Método para buscar documentos por palavra
	@GetMapping("/documentos/busca")
	public String pesquisaDocumento(Model model, String pesquisa) {		
		if (pesquisa != null) {
			List<Documento> listaDocumentos = documentoService.findNome(pesquisa);
			model.addAttribute("documento", listaDocumentos);
			model.addAttribute("pesquisa", pesquisa);
			return "busca";

		}
		model.addAttribute("documento", documentoService.findAll());

		return "index";
	}

	// Direciona para página de cadastro
	@GetMapping("/cadastro")
	public String cadastraDocumento(@ModelAttribute("documento") Documento documento) {
		return "cadastro";
	}
	
	//Método para salvar documentos
	@PostMapping("/cadastro/salvar")
	public String salvarDocumento(@RequestParam("arquivo") MultipartFile[] arquivo, Model model) {

		try {
			documentoService.upload(arquivo);

		} catch (Exception e) {
			System.out.println(e);
		}
		return "redirect:/edoc/documentos";
	}
	
	//Abre arquivo de acordo com o nome em uma nova aba do navegador
	@GetMapping("/{nome}")
	public ResponseEntity<byte[]> downloadFile(@PathVariable String nome) {
		Documento file = documentoService.findByNome(nome);
		return ResponseEntity
				.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "inline; nome=\"" + file.getNome() + "\"")
				.contentType(MediaType.parseMediaType("application/pdf")).body(file.getData());
	}

	// Deleta documento pelo id
	@GetMapping("/delete/{id}")
	public String deleteDocumento(@PathVariable Integer id, Model model) {
		documentoService.deleteById(id);
		model.addAttribute("documento", documentoService.findAll());
		return "redirect:/edoc/documentos";
	}
	
	// Métedo para validação do tamanho máximo do(s) arquivo(s)
	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public String sizeHandler(RedirectAttributes redirectAttribute) {
		redirectAttribute.addFlashAttribute("error", "Tamanho máximo do(s) arquivo(s) 2MB");
		return "redirect:/edoc/cadastro";
	}

}
