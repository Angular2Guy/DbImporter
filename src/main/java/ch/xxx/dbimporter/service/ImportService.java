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
package ch.xxx.dbimporter.service;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.BaseStream;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import ch.xxx.dbimporter.dto.RowDto;
import ch.xxx.dbimporter.entity.Row;
import ch.xxx.dbimporter.entity.RowRepository;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Service
public class ImportService {
	Logger LOG = LoggerFactory.getLogger(ImportService.class);
	@Value("${dbimporter.tempdir}")
	private String tmpDir;
	@Autowired
	private RowRepository rowRepository;
	@Autowired
	private EntityManager entityManager;
	private static final int MB = 1024 * 1024;

	public String importFile(String fileType, boolean multifile) throws JsonProcessingException, IOException {
		LOG.info("ImportFile start");
		LocalDateTime start = LocalDateTime.now();
		this.rowRepository.bulkDelete();
		LOG.info(String.format("ImportFile Db cleaned in %d sec",
				Duration.between(start, LocalDateTime.now()).getSeconds()));
		File dir = new File(this.tmpDir);
		File[] oneFile = new File[1];
		oneFile[0] = new File(this.tmpDir + "/import." + fileType);
		File[] files = multifile ? dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File arg0, String arg1) {
				return arg1.endsWith("." + fileType);
			}
		}) : oneFile;
		if (fileType.contains("csv")) {
			List<Flux<String>> resultFluxList = new ArrayList<>();
			Arrays.stream(files).forEach(file -> {
				resultFluxList.add(Flux.using(() -> readFiles(file), Flux::fromStream, BaseStream::close));
			});
			Flux<String> lineFlux = Flux.concat(resultFluxList);
			lineFlux.flatMap(str -> this.strToRow(str)).buffer(200).parallel().runOn(Schedulers.parallel())
					.subscribe(rows -> this.storeRows(rows, start));
		}
		if (fileType.contains("json")) {
			List<Flux<RowDto>> resultFluxList = new ArrayList<>();
			Arrays.stream(files).forEach(file -> {
				resultFluxList.add(Flux.using(() -> this.readRowDto(file), Flux::fromStream, BaseStream::close));
			});
			Flux<RowDto> rowFlux = Flux.concat(resultFluxList);
			rowFlux.flatMap(rowDto -> this.dtoToRow(rowDto)).buffer(200).parallel().runOn(Schedulers.parallel())
					.subscribe(rows -> this.storeRows(rows, start));
		}
		return "Done";
	}

	private Stream<String> readFiles(File file) throws IOException {
		return Files.lines(Paths.get(file.getPath()));
	}

	private Flux<Row> dtoToRow(RowDto dto) {
		Row row = new Row();
		row.setDate1(dto.getDate1());
		row.setDate2(dto.getDate2());
		row.setDate3(dto.getDate3());
		row.setDate4(dto.getDate4());
		row.setLong1(dto.getLong1());
		row.setLong2(dto.getLong2());
		row.setLong3(dto.getLong3());
		row.setLong4(dto.getLong4());
		row.setLong5(dto.getLong5());
		row.setStr1(dto.getStr1());
		row.setStr2(dto.getStr2());
		row.setStr3(dto.getStr3());
		row.setStr4(dto.getStr4());
		row.setStr5(dto.getStr5());
		row.setStr6(dto.getStr6());
		row.setStr7(dto.getStr7());
		row.setStr8(dto.getStr8());
		row.setStr9(dto.getStr9());
		return Flux.just(row);
	}

	private JsonParser createParser(File file) {
		JsonParser parser = null;
		try {
			parser = new JsonFactory().createParser(file);
			parser.setCodec(this.createObjectMapper());
			parser.nextToken();
			while (parser.getCurrentName() == null || !parser.getCurrentName().equals("rows")) {
				parser.nextToken();
			}
			parser.nextToken();
			parser.nextToken();
		} catch (Exception e) {
			LOG.error("parser exeption", e);
		}
		return parser;
	}

	private Stream<RowDto> readRowDto(File file) throws IOException {
		JsonParser parser = this.createParser(file);
		RowDto rowDto = this.readRowDto(parser);
		return Stream.iterate(rowDto, x -> x != null, (x) -> this.readRowDto(parser)).onClose(() -> {
			closeParser(parser);
		});
	}

	private RowDto readRowDto(JsonParser parser) {
		try {
			return parser.readValueAs(RowDto.class);
		} catch (IOException e) {
			LOG.error("parsing error", e);
			return null;
		}
	}

	private void closeParser(JsonParser parser) {
		try {
			parser.close();
		} catch (IOException e) {
			LOG.error("parsing error", e);
		}
	}

	@Transactional
	private void storeRows(List<Row> rows, LocalDateTime start) {
		LocalDateTime begin = LocalDateTime.now();
		this.entityManager.clear();
		this.rowRepository.saveAll(rows);				
		LOG.info(String.format("%d Rows flushed in %d millis, Mem %d mb, total Time %d sec",rows.size(),
				Duration.between(begin, LocalDateTime.now()).getNano() / 1000000,
				((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / MB),
				Duration.between(start, LocalDateTime.now()).getSeconds()));
	}

	private ObjectMapper createObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false);
		return objectMapper;
	}

	private Flux<Row> strToRow(String line) {
		Row row = new Row();
		String[] properties = line.split(",");
		row.setDate1(LocalDate.parse(properties[0]));
		row.setDate2(LocalDate.parse(properties[1]));
		row.setDate3(LocalDate.parse(properties[2]));
		row.setDate4(LocalDate.parse(properties[3]));
		row.setStr1(properties[4]);
		row.setStr2(properties[5]);
		row.setStr3(properties[6]);
		row.setStr4(properties[7]);
		row.setStr5(properties[8]);
		row.setStr6(properties[9]);
		row.setStr7(properties[10]);
		row.setStr8(properties[11]);
		row.setStr9(properties[12]);
		row.setLong1(Long.parseLong(properties[13]));
		row.setLong2(Long.parseLong(properties[14]));
		row.setLong3(Long.parseLong(properties[15]));
		row.setLong4(Long.parseLong(properties[16]));
		row.setLong5(Long.parseLong(properties[17]));
		return Flux.just(row);
	}

	public String generateFile(long rows, String type) throws IOException {
		File outputFile = new File(this.tmpDir + "/import." + type);
		if ("csv".equals(type)) {
			try (PrintWriter pw = new PrintWriter(outputFile)) {
				for (long l = 0; l < rows; l++) {
					RowDto row = createRandomRow(l);
					String csvStr = convertRowToCsv(row);
					pw.println(csvStr);
				}
			}
		}
		if ("json".equals(type)) {
			try (JsonGenerator jsonGen = new JsonFactory().createGenerator(outputFile, JsonEncoding.UTF8)) {
				jsonGen.setCodec(this.createObjectMapper());
				jsonGen.setPrettyPrinter(new DefaultPrettyPrinter());
				jsonGen.writeStartObject();
				jsonGen.writeFieldName("rows");
				jsonGen.writeStartArray();
				for (long l = 0; l < rows; l++) {
					RowDto row = createRandomRow(l);
					jsonGen.writeObject(row);
				}
				jsonGen.writeEndArray();
				jsonGen.writeEndObject();
			}
		}
		return "import." + type;
	}

	private String convertRowToCsv(RowDto row) {
		StringBuilder csvRow = new StringBuilder();
		csvRow.append(row.getDate1()).append(",");
		csvRow.append(row.getDate2()).append(",");
		csvRow.append(row.getDate3()).append(",");
		csvRow.append(row.getDate4()).append(",");
		csvRow.append(row.getStr1()).append(",");
		csvRow.append(row.getStr2()).append(",");
		csvRow.append(row.getStr3()).append(",");
		csvRow.append(row.getStr4()).append(",");
		csvRow.append(row.getStr5()).append(",");
		csvRow.append(row.getStr6()).append(",");
		csvRow.append(row.getStr7()).append(",");
		csvRow.append(row.getStr8()).append(",");
		csvRow.append(row.getStr9()).append(",");
		csvRow.append(row.getLong1()).append(",");
		csvRow.append(row.getLong2()).append(",");
		csvRow.append(row.getLong3()).append(",");
		csvRow.append(row.getLong4()).append(",");
		csvRow.append(row.getLong5());
		return csvRow.toString();
	}

	private RowDto createRandomRow(long l) {
		RowDto row = new RowDto();
		row.setDate1(LocalDate.now().plusDays((long) (Math.random() * 30)));
		row.setDate2(LocalDate.now().plusDays((long) (Math.random() * 30)));
		row.setDate3(LocalDate.now().plusDays((long) (Math.random() * 30)));
		row.setDate4(LocalDate.now().plusDays((long) (Math.random() * 30)));
		row.setLong1(l + 1);
		row.setLong2(l + 2);
		row.setLong3(l + 3);
		row.setLong4(l + 4);
		row.setLong5(l + 5);
		row.setStr1(RandomStringUtils.random(25, true, false));
		row.setStr2(RandomStringUtils.random(25, true, false));
		row.setStr3(RandomStringUtils.random(25, true, false));
		row.setStr4(RandomStringUtils.random(25, true, false));
		row.setStr5(RandomStringUtils.random(25, true, false));
		row.setStr6(RandomStringUtils.random(25, true, false));
		row.setStr7(RandomStringUtils.random(25, true, false));
		row.setStr8(RandomStringUtils.random(25, true, false));
		row.setStr9(RandomStringUtils.random(25, true, false));
		return row;
	}
}
