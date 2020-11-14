package br.com.libraryapi.api.services.impl;

import br.com.libraryapi.api.services.BookService;
import br.com.libraryapi.model.entity.Book;
import br.com.libraryapi.model.repositories.BookRepository;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {
    private final BookRepository repository;

    public BookServiceImpl(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public Book save(Book book) {
        return repository.save(book);
    }
}
