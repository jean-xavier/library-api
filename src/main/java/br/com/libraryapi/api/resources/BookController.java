package br.com.libraryapi.api.resources;

import br.com.libraryapi.api.dto.BookDTO;
import br.com.libraryapi.api.services.BookService;
import br.com.libraryapi.model.entity.Book;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final ModelMapper modelMapper;
    private final BookService bookService;

    public BookController(BookService service, ModelMapper modelMapper) {
        this.bookService = service;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO create(@RequestBody BookDTO dto) {
        Book book = modelMapper.map(dto, Book.class);
        book = bookService.save(book);

        return modelMapper.map(book, BookDTO.class);
    }
}
