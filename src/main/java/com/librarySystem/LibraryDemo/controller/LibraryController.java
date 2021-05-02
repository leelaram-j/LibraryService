package com.librarySystem.LibraryDemo.controller;

import com.librarySystem.LibraryDemo.entityBeans.AddResponseBean;
import com.librarySystem.LibraryDemo.entityBeans.ErrorStatusbean;
import com.librarySystem.LibraryDemo.entityBeans.LibraryBean;
import com.librarySystem.LibraryDemo.repository.LibraryRepository;
import com.librarySystem.LibraryDemo.service.LibraryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class LibraryController {

    @Autowired
    LibraryRepository repository;

    @Autowired
    LibraryService libraryService;

    private static final Logger logger = LoggerFactory.getLogger(LibraryController.class);

    @PostMapping("/addBook")
    public ResponseEntity addBook(@RequestBody LibraryBean libraryBean) {
        AddResponseBean addResponseBean = new AddResponseBean();
        String id = libraryService.generateId(libraryBean.getIsbn(), libraryBean.getAisle());
        if(!libraryService.isBookAlreadyExists(id)) {
            logger.info("Creating New Book as Book is not present");
            libraryBean.setId(id);
            repository.save(libraryBean);

            HttpHeaders headers = new HttpHeaders();
            headers.add( "uniqueId",id);
            addResponseBean.setMessage("Success Book is Added");
            addResponseBean.setId(libraryBean.getId());
            return new ResponseEntity<AddResponseBean>(addResponseBean, headers, HttpStatus.CREATED);
        } else {
            logger.info("Book already exists");
            addResponseBean.setMessage("Book already exists");
            addResponseBean.setId(id);
            return new ResponseEntity<AddResponseBean>(addResponseBean, HttpStatus.ACCEPTED);
        }
    }

    @GetMapping("/getBook/{id}")
    public LibraryBean getBook(@PathVariable(value = "id") String id) {
        try{
            LibraryBean libraryBean = repository.findById(id).get();
            logger.info("Book is present");
            return libraryBean;
        } catch (Exception e) {
            logger.info("Book id is not present");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/getBooks/author")
    public List<LibraryBean> getBooksUsingAuthorName(@RequestParam(value = "authorName")String authorName) {
        return repository.findAllByAuthor(authorName);
    }

    @PutMapping("/updateBook/{id}")
    public ResponseEntity<? extends Object> updateBook(@PathVariable(value = "id")String id, @RequestBody LibraryBean libraryBean) {
        ErrorStatusbean errorStatusbean = new ErrorStatusbean();
        if(libraryService.isBookAlreadyExists(id)) {
            LibraryBean record = repository.findById(id).get();
            record.setAisle(libraryBean.getAisle());
            record.setAuthor(libraryBean.getAuthor());
            record.setBook_name(libraryBean.getBook_name());
            repository.save(record);
            return new ResponseEntity<LibraryBean>(record, HttpStatus.OK);
        } else {
            errorStatusbean.setMessage("Please Enter Correct Book Id");
            return new ResponseEntity<ErrorStatusbean>(errorStatusbean, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/deleteBook")
    public ResponseEntity<ErrorStatusbean> deleteBookById(@RequestBody LibraryBean libraryBean) {
        ErrorStatusbean errorStatusbean = new ErrorStatusbean();
        if(libraryService.isBookAlreadyExists(libraryBean.getId())) {
            repository.deleteById(libraryBean.getId());
            errorStatusbean.setMessage("Book is deleted");
            return new ResponseEntity<ErrorStatusbean>(errorStatusbean, HttpStatus.OK);
            // return new ResponseEntity<>("book is deleted", HttpStatus.OK);
        } else {
            errorStatusbean.setMessage("Please enter valid book");
            return new ResponseEntity<ErrorStatusbean>(errorStatusbean, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/getAllBooks")
    public List<LibraryBean> getAllBooks() {
        return repository.findAll();
    }
}
