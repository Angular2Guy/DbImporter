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
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.BaseStream;

import javax.transaction.Transactional;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

	public String importFile(String fileName) {
		LOG.info("ImportFile start");
		LocalDateTime start = LocalDateTime.now();		
		this.rowRepository.bulkDelete();
		LOG.info(String.format("ImportFile Db cleaned in %d sec",Duration.between(start,LocalDateTime.now()).getSeconds()));		
		Path csvPath = Paths.get(this.tmpDir + "/" + fileName);
		Flux<String> lineFlux = Flux.using(() -> Files.lines(csvPath), Flux::fromStream, BaseStream::close);
		lineFlux.flatMap(str -> this.strToRow(str)).buffer(1000).parallel().runOn(Schedulers.parallel()).subscribe(rows -> this.storeRows(rows,start));
		return "Done";
	}

	@Transactional
	private void storeRows(List<Row> rows, LocalDateTime start) {		
		this.rowRepository.saveAll(rows);
		LOG.info(String.format("Rows stored in %d sec", Duration.between(start, LocalDateTime.now()).getSeconds()));
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
	
	public String generateFile(long rows) throws FileNotFoundException {
		File csvFile = new File(this.tmpDir + "/import.csv");
		try (PrintWriter pw = new PrintWriter(csvFile)) {
			for (long l = 0; l < rows; l++) {
				Row row = createRandomRow(l);
				String csvStr = convertRowToCsv(row);
				pw.println(csvStr);
			}
		}
		return "import.csv";
	}

	private String convertRowToCsv(Row row) {
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

	private Row createRandomRow(long l) {
		Row row = new Row();
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
