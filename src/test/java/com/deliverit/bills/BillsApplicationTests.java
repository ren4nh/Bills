package com.deliverit.bills;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.Set;

import com.deliverit.bills.dto.BillDTO;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;

import org.junit.jupiter.api.BeforeAll;
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

	final static ObjectMapper objectMapper = new ObjectMapper();

	@BeforeAll
	public static void setup() {
		objectMapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);

		Configuration.setDefaults(new Configuration.Defaults() {

			private final JsonProvider jsonProvider = new JacksonJsonProvider(objectMapper);
			private final MappingProvider mappingProvider = new JacksonMappingProvider(objectMapper);

			@Override
			public JsonProvider jsonProvider() {
				return jsonProvider;
			}

			@Override
			public MappingProvider mappingProvider() {
				return mappingProvider;
			}

			@Override
			public Set<Option> options() {
				return EnumSet.noneOf(Option.class);
			}
		});
	}

	@Test
	@DisplayName("Create a bill")
	public void testSave() throws Exception {
		String json = objectMapper.writeValueAsString(
				new BillDTO("Bill", new BigDecimal(100), LocalDate.now(), LocalDate.now().plusDays(1l)));

		mockMvc.perform(post("/bills").content(json).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.name", is("Bill")));
	}

	@Test
	@DisplayName("Create a bill with 1 day overdue")
	public void test1DayOverdue() throws Exception {

		BillDTO billDto = new BillDTO("1 day overdue", new BigDecimal(100), LocalDate.now().plusDays(1l),
				LocalDate.now());
		String json = objectMapper.writeValueAsString(billDto);

		BigDecimal fine = billDto.getValue().multiply(BigDecimal.valueOf(2l)).divide(new BigDecimal(100));
		BigDecimal interest = billDto.getValue().add(fine).multiply(BigDecimal.valueOf(0.1))
				.divide(new BigDecimal(100));

		BigDecimal correctValue = billDto.getValue().add(fine).add(interest).setScale(2, RoundingMode.HALF_EVEN);

		mockMvc.perform(post("/bills").content(json).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.correctedValue", is(correctValue), BigDecimal.class))
				.andExpect(jsonPath("$.daysOverdue", is(1)));
	}

	@Test
	@DisplayName("Create a bill with 4 days overdue")
	public void test4DaysOverdue() throws Exception {

		BillDTO billDto = new BillDTO("4 days overdue", new BigDecimal(100), LocalDate.now().plusDays(4l),
				LocalDate.now());
		String json = objectMapper.writeValueAsString(billDto);

		BigDecimal fine = billDto.getValue().multiply(BigDecimal.valueOf(3l)).divide(new BigDecimal(100));
		BigDecimal interest = billDto.getValue().add(fine).multiply(BigDecimal.valueOf(0.8))
				.divide(new BigDecimal(100));

		BigDecimal correctValue = billDto.getValue().add(fine).add(interest).setScale(2, RoundingMode.HALF_EVEN);

		mockMvc.perform(post("/bills").content(json).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.correctedValue", is(correctValue), BigDecimal.class))
				.andExpect(jsonPath("$.daysOverdue", is(4)));
	}

	@Test
	@DisplayName("Create a bill with 6 days overdue")
	public void test6DaysOverdue() throws Exception {

		BillDTO billDto = new BillDTO("6 days overdue", new BigDecimal(100), LocalDate.now().plusDays(6l),
				LocalDate.now());
		String json = objectMapper.writeValueAsString(billDto);

		BigDecimal fine = billDto.getValue().multiply(BigDecimal.valueOf(5l)).divide(new BigDecimal(100));
		BigDecimal interest = billDto.getValue().add(fine).multiply(BigDecimal.valueOf(1.8))
				.divide(new BigDecimal(100));

		BigDecimal correctValue = billDto.getValue().add(fine).add(interest).setScale(2, RoundingMode.HALF_EVEN);

		mockMvc.perform(post("/bills").content(json).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.correctedValue", is(correctValue), BigDecimal.class))
				.andExpect(jsonPath("$.daysOverdue", is(6)));
	}

	@Test
	@DisplayName("Get all bills")
	public void testGetAll() throws Exception {
		mockMvc.perform(get("/bills").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.length()", is(4)));
	}

}
