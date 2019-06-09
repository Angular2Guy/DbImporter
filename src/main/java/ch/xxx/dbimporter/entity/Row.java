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
package ch.xxx.dbimporter.entity;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity
public class Row {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO,generator = "row_seq")
	@SequenceGenerator(allocationSize = 1000, name = "row_seq")
	private Long id;
	private String str1;
	private String str2;
	private String str3;
	private String str4;
	private String str5;
	private String str6;
	private String str7;
	private String str8;
	private String str9;
	private LocalDate date1;
	private LocalDate date2;
	private LocalDate date3;
	private LocalDate date4;
	private Long long1;
	private Long long2;
	private Long long3;
	private Long long4;
	private Long long5;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getStr1() {
		return str1;
	}
	public void setStr1(String str1) {
		this.str1 = str1;
	}
	public String getStr2() {
		return str2;
	}
	public void setStr2(String str2) {
		this.str2 = str2;
	}
	public String getStr3() {
		return str3;
	}
	public void setStr3(String str3) {
		this.str3 = str3;
	}
	public String getStr4() {
		return str4;
	}
	public void setStr4(String str4) {
		this.str4 = str4;
	}
	public String getStr5() {
		return str5;
	}
	public void setStr5(String str5) {
		this.str5 = str5;
	}
	public String getStr6() {
		return str6;
	}
	public void setStr6(String str6) {
		this.str6 = str6;
	}
	public String getStr7() {
		return str7;
	}
	public void setStr7(String str7) {
		this.str7 = str7;
	}
	public String getStr8() {
		return str8;
	}
	public void setStr8(String str8) {
		this.str8 = str8;
	}
	public String getStr9() {
		return str9;
	}
	public void setStr9(String str9) {
		this.str9 = str9;
	}	
	public Long getLong1() {
		return long1;
	}
	public void setLong1(Long long1) {
		this.long1 = long1;
	}
	public Long getLong2() {
		return long2;
	}
	public void setLong2(Long long2) {
		this.long2 = long2;
	}
	public Long getLong3() {
		return long3;
	}
	public void setLong3(Long long3) {
		this.long3 = long3;
	}
	public Long getLong4() {
		return long4;
	}
	public void setLong4(Long long4) {
		this.long4 = long4;
	}
	public Long getLong5() {
		return long5;
	}
	public void setLong5(Long long5) {
		this.long5 = long5;
	}
	public LocalDate getDate1() {
		return date1;
	}
	public void setDate1(LocalDate date1) {
		this.date1 = date1;
	}
	public LocalDate getDate2() {
		return date2;
	}
	public void setDate2(LocalDate date2) {
		this.date2 = date2;
	}
	public LocalDate getDate3() {
		return date3;
	}
	public void setDate3(LocalDate date3) {
		this.date3 = date3;
	}
	public LocalDate getDate4() {
		return date4;
	}
	public void setDate4(LocalDate date4) {
		this.date4 = date4;
	}
	
}
