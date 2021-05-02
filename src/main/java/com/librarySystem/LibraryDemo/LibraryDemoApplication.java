package com.librarySystem.LibraryDemo;

import com.librarySystem.LibraryDemo.repository.LibraryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LibraryDemoApplication  {//implements CommandLineRunner {

	@Autowired
	LibraryRepository repository;

	public static void main(String[] args) {
		SpringApplication.run(LibraryDemoApplication.class, args);
	}

	/*
	@Override
	public void run(String[] args) {

		//sample crud operations using JPA;

		LibraryBean libraryBean = repository.findById("A123").get();
		System.out.println(libraryBean.getAuthor());;
		LibraryBean lib = new LibraryBean();
		lib.setAisle(123);
		lib.setAuthor("Lee");
		lib.setBook_name("Devops");
		lib.setIsbn("asdnb");
		lib.setId("asdnb123");
		//repository.save(lib);
		List<LibraryBean> bookNames =  repository.findAll();
		for(LibraryBean bookName:bookNames) {
			System.out.println(bookName.getBook_name());
		}
		repository.delete(lib);
	}

	 */
}
