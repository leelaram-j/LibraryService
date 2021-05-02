package com.librarySystem.LibraryDemo.repository;

import com.librarySystem.LibraryDemo.entityBeans.LibraryBean;

import java.util.List;

public interface LibraryRepositoryCustom {
    List<LibraryBean> findAllByAuthor(String authorName);
}
