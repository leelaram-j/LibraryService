package com.librarySystem.LibraryDemo.repository;

import com.librarySystem.LibraryDemo.entityBeans.LibraryBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LibraryRepository extends JpaRepository<LibraryBean, String>, LibraryRepositoryCustom {
}
