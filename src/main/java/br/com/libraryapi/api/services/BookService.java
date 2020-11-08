package br.com.libraryapi.api.services;

import br.com.libraryapi.model.entity.Book;
import org.springframework.stereotype.Service;

@Service
public interface BookService {
    Book save(Book book);
}
