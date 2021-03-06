/**
 *    Copyright 2019 Sven Loesekann
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
       http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package ch.xxx.dbimporter.controller;

import java.io.IOException;

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
	public Mono<String> importSingleFile(@RequestParam String type) {
		try {
			return Mono.just(this.service.importFile(type, false));
		} catch (IOException e) {
			return Mono.empty();
		}
	}
	
	@GetMapping("/multi")
	public Mono<String> importMultiFile(@RequestParam String type) {
		try {
			return Mono.just(this.service.importFile(type, true));		
		} catch (IOException e) {
			return Mono.empty();
		}
	}
	
	@GetMapping("/generate")
	public Mono<String> generateFile(@RequestParam Long rows, @RequestParam String type)  {
		try {
			return Mono.just(this.service.generateFile(rows, type));
		} catch (IOException e) {
			return Mono.empty();
		}
	}
}
