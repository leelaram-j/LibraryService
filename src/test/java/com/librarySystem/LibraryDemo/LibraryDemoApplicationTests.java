package com.librarySystem.LibraryDemo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.librarySystem.LibraryDemo.controller.LibraryController;
import com.librarySystem.LibraryDemo.entityBeans.AddResponseBean;
import com.librarySystem.LibraryDemo.entityBeans.ErrorStatusbean;
import com.librarySystem.LibraryDemo.entityBeans.LibraryBean;
import com.librarySystem.LibraryDemo.repository.LibraryRepository;
import com.librarySystem.LibraryDemo.service.LibraryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LibraryDemoApplicationTests {

	@Autowired
	LibraryController libraryController;
	@Autowired
	LibraryService service;
	@MockBean
	LibraryRepository libraryRepository;
	@MockBean
	LibraryService libraryService;

	@Autowired
	MockMvc mockMvc;

	@Test // simple tests
	public void checkBuildIdLogic() {
		LibraryService service = new LibraryService();
		String id = service.generateId("ZMAN", 24);
		Assertions.assertEquals(id, "OLDZMAN24");
		String id1 = service.generateId("MAN", 24);
		Assertions.assertEquals(id1, "MAN24");
	}

	// this below method is used to write unit testcases using mockito
	// here we mock the required dependent data so that we can write unit testcases

	@Test
	public void addBookTest() {

		LibraryBean libraryBean = buildLibrary();

		Mockito.when(libraryService.generateId(libraryBean.getIsbn(), libraryBean.getAisle())).thenReturn(libraryBean.getId());
		//Mockito.when(libraryService.isBookAlreadyExists(libraryBean.getId())).thenReturn(false);
		Mockito.when(libraryService.isBookAlreadyExists(libraryBean.getId())).thenReturn(true);
		Mockito.when(libraryRepository.save(any())).thenReturn(libraryBean);

		ResponseEntity response = libraryController.addBook(buildLibrary());
		System.out.println(response.getStatusCode());
		//Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
		Assertions.assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
		AddResponseBean addResponseBean = (AddResponseBean) response.getBody();
		Assertions.assertEquals(addResponseBean.getId(), libraryBean.getId());
		Assertions.assertEquals("Book already exists", addResponseBean.getMessage());
	}

	// Below method uses mockmvc to write unit testcases. here we hit the endpoint and send data in json.ts
	// MockMVC only on controlled methods.
	@Test
	public void addBookTest1() {
		LibraryBean libraryBean = buildLibrary();
		ObjectMapper obj = new ObjectMapper();
		String requestBody = "";
		try {
			requestBody= obj.writeValueAsString(libraryBean);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		Mockito.when(libraryService.generateId(libraryBean.getIsbn(), libraryBean.getAisle())).thenReturn(libraryBean.getId());
		Mockito.when(libraryService.isBookAlreadyExists(libraryBean.getId())).thenReturn(false);
		Mockito.when(libraryRepository.save(any())).thenReturn(libraryBean);
		try {
			this.mockMvc.perform(post("/addBook").contentType(MediaType.APPLICATION_JSON)
			.content(requestBody))
					.andDo(print())
					.andExpect(status().isCreated())
					.andExpect(jsonPath("$.id").value(libraryBean.getId()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void getBooksWithAuthorTest() throws Exception {
		List<LibraryBean> list = new ArrayList<>();
		list.add(buildLibrary());
		list.add(buildLibrary());
		list.add(buildLibrary());
		Mockito.when(libraryRepository.findAllByAuthor(any())).thenReturn(list);

		this.mockMvc.perform(MockMvcRequestBuilders.get("/getBooks/author").param("authorName", "Lee"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()", is(3)))
				.andExpect(jsonPath("$.[0].id").value("ABCYU24"));
	}

	@Test
	public void updateBookById() throws Exception {
		LibraryBean libraryBean = buildLibrary();
		Mockito.when(libraryService.isBookAlreadyExists(any())).thenReturn(true);
		Mockito.when(libraryService.getBookById(any())).thenReturn(libraryBean);
		//Mockito.when(libraryRepository.save(any())).thenReturn(libraryBean);
		ObjectMapper obj = new ObjectMapper();
		String requestBody = "";
		try {
			requestBody= obj.writeValueAsString(updateLibrary());
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		LibraryBean resultBean = new LibraryBean();
		resultBean.setId(buildLibrary().getId());
		resultBean.setAisle(updateLibrary().getAisle());
		resultBean.setAuthor(updateLibrary().getAuthor());
		resultBean.setBook_name(updateLibrary().getBook_name());
		resultBean.setIsbn(buildLibrary().getIsbn());

		this.mockMvc.perform(MockMvcRequestBuilders.put("/updateBook/" + libraryBean.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andDo(print())
				.andExpect(status().isOk())
				//.andExpect(jsonPath("$.id").value(buildLibrary().getId())); // this is one way of validating
			    .andExpect(content().json(obj.writeValueAsString(resultBean)));
	}

	@Test
	public void updateBookByIdFalseCase() throws Exception {
		LibraryBean libraryBean = buildLibrary();
		Mockito.when(libraryService.isBookAlreadyExists(any())).thenReturn(false);
		Mockito.when(libraryService.getBookById(any())).thenReturn(libraryBean);
		//Mockito.when(libraryRepository.save(any())).thenReturn(libraryBean);
		ObjectMapper obj = new ObjectMapper();
		String requestBody = "";
		try {
			requestBody= obj.writeValueAsString(updateLibrary());
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		this.mockMvc.perform(MockMvcRequestBuilders.put("/updateBook/" + libraryBean.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andDo(print())
				.andExpect(status().isNotFound());
		//.andExpect(jsonPath("$.id").value(buildLibrary().getId()));
	}

	/*
	@Test()
	public void getBookTest() throws Exception {
		//Mockito.when(libraryRepository.findById(any()).isPresent()).thenReturn(true);
		Mockito.doReturn(true).when(libraryRepository).findById(any()).isPresent();
		Mockito.when(libraryRepository.findById(any()).get()).thenReturn(buildLibrary());

		this.mockMvc.perform(MockMvcRequestBuilders.get("/getBook/" + updateLibrary().getId()))
				.andDo(print())
				.andExpect(content().json(new ObjectMapper().writeValueAsString(buildLibrary())))
				.andExpect(status().isOk());
	}
	*/

	@Test
	public void deleteBookTest() throws Exception {
		Mockito.when(libraryService.isBookAlreadyExists(buildLibrary().getId())).thenReturn(true);
		LibraryBean libraryBean = new LibraryBean();
		libraryBean.setId("ABCYU24");
		String requestBody = new ObjectMapper().writeValueAsString(libraryBean);
		ErrorStatusbean errorStatusbean = new ErrorStatusbean();
		errorStatusbean.setMessage("Book is deleted");
		Mockito.doNothing().when(libraryRepository).delete(buildLibrary());

		this.mockMvc.perform(MockMvcRequestBuilders.delete("/deleteBook").contentType(MediaType.APPLICATION_JSON)
		.content(requestBody))
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(content().json(new ObjectMapper().writeValueAsString(errorStatusbean)));
	}

	@Test
	public void deleteBookFalseTest() throws Exception {
		Mockito.when(libraryService.isBookAlreadyExists(buildLibrary().getId())).thenReturn(false);
		LibraryBean libraryBean = new LibraryBean();
		libraryBean.setId("ABCYU24");
		String requestBody = new ObjectMapper().writeValueAsString(libraryBean);
		ErrorStatusbean errorStatusbean = new ErrorStatusbean();
		errorStatusbean.setMessage("Please enter valid book");
		Mockito.doNothing().when(libraryRepository).delete(buildLibrary());

		this.mockMvc.perform(MockMvcRequestBuilders.delete("/deleteBook").contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andDo(print())
				.andExpect(status().isNotFound())
				.andExpect(content().json(new ObjectMapper().writeValueAsString(errorStatusbean)));
	}


	private static LibraryBean buildLibrary() {
		LibraryBean libraryBean = new LibraryBean();
		libraryBean.setBook_name("SampleBook");
		libraryBean.setAuthor("Lee");
		libraryBean.setAisle(24);
		libraryBean.setIsbn("ABCYU");
		libraryBean.setId("ABCYU24");
		return libraryBean;
	}

	private static LibraryBean updateLibrary() {
		LibraryBean libraryBean = new LibraryBean();
		libraryBean.setBook_name("SampleBookTest");
		libraryBean.setAuthor("Ram");
		libraryBean.setAisle(24);
		return libraryBean;
	}
}
