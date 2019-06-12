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

import java.time.LocalDate;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import ch.xxx.dbimporter.dto.RowDto;
import ch.xxx.dbimporter.entity.Row;
import reactor.core.publisher.Flux;

@Component
public class DtoConverter {
	
	public RowDto createRandomRow(long l) {
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
	
	public String convertRowToCsv(RowDto row) {
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
	
	public Flux<Row> strToRow(String line) {
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
	
	public Flux<Row> dtoToRow(RowDto dto) {
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
}
