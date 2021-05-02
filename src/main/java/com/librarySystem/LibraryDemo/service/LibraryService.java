package com.librarySystem.LibraryDemo.service;

import com.librarySystem.LibraryDemo.entityBeans.LibraryBean;
import com.librarySystem.LibraryDemo.repository.LibraryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LibraryService {
    @Autowired
    LibraryRepository libraryRepository;

    public boolean isBookAlreadyExists(String bookid) {
        Optional<LibraryBean> libOptional = libraryRepository.findById(bookid);
        if(libOptional.isPresent())
            return true;
        else
            return false;
    }

    public String generateId(String isbn, int aisle) {
        return isbn + aisle;
    }
}
