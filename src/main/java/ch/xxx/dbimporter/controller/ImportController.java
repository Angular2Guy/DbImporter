package ch.xxx.dbimporter.controller;

import java.io.FileNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.xxx.dbimporter.service.ImportService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/rest/import")
public class ImportController {
	@Autowired
	private ImportService service;

	@GetMapping("/single")
	public Mono<String> importSingleFile(@RequestParam String filename) {
		return Mono.just(this.service.importFile(filename));
	}
	
	@GetMapping("/multi")
	public Mono<Long> importMultiFile() {
		return Mono.just(0L);
	}
	
	@GetMapping("/generate")
	public Mono<String> generateFile(@RequestParam Long rows)  {
		try {
			return Mono.just(this.service.generateFile(rows));
		} catch (FileNotFoundException e) {
			return Mono.empty();
		}
	}
}
