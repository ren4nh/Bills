package com.deliverit.bills;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.deliverit.bills.dto.BillDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class BillsApplicationTests {

	@Autowired
	MockMvc mockMvc;

	@Test
	@DisplayName("Create a bill")
	public void testSave() throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(
				new BillDTO("Conta 1", new BigDecimal(100), LocalDate.now(), LocalDate.now().plusDays(1l)));

		mockMvc.perform(post("/bills").content(json).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.name", is("Conta 1")));
	}

	@Test
	@DisplayName("Create an overdue bill")
	public void testOverdueBill() throws Exception {

		BillDTO billDto = new BillDTO("Conta Vencida 1 dia", new BigDecimal(100), LocalDate.now().plusDays(1l),
				LocalDate.now());
		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(billDto);

		mockMvc.perform(post("/bills").content(json).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.correctedValue", greaterThan(billDto.getValue()), BigDecimal.class));
	}

	@Test
	@DisplayName("Get all bills")
	public void testGetAll() throws Exception {
		mockMvc.perform(get("/bills").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.length()", is(2)));
	}

}
