package com.librarySystem.LibraryDemo.repository;

import com.librarySystem.LibraryDemo.entityBeans.LibraryBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class LibearyRepositoryImpl implements LibraryRepositoryCustom {
    @Autowired
    LibraryRepository libraryRepository;

    @Override
    public List<LibraryBean> findAllByAuthor(String authorName) {
        List<LibraryBean> allRecords = libraryRepository.findAll();
        List<LibraryBean> resultSet = new ArrayList<>();
        for (LibraryBean record : allRecords) {
            if(record.getAuthor().equalsIgnoreCase(authorName)) {
                resultSet.add(record);
            }
        }
        return  resultSet;
    }
}
