package br.com.libraryapi.api.services;

import br.com.libraryapi.model.entity.Book;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface BookService {
    Book save(Book book);
    Optional<Book> getById(Long id);
    void delete(Book book);
    Book update(Book book);
}
