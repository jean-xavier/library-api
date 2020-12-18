package br.com.libraryapi.api.resources;

import br.com.libraryapi.api.dto.BookDTO;
import br.com.libraryapi.api.exceptions.ApiErrors;
import br.com.libraryapi.api.services.BookService;
import br.com.libraryapi.exceptions.BusinessException;
import br.com.libraryapi.model.entity.Book;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public BookDTO create(@RequestBody @Valid BookDTO dto) {
        Book book = modelMapper.map(dto, Book.class);
        book = bookService.save(book);

        return modelMapper.map(book, BookDTO.class);
    }

    @GetMapping("{id}")
    public BookDTO get(@PathVariable Long id) {
        Optional<Book> book = bookService.getById(id);
        return book
                .map(value -> modelMapper.map(value, BookDTO.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PutMapping("{id}")
    public BookDTO update(@PathVariable Long id, @Valid @RequestBody BookDTO dto) {
        Book book = bookService.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        book.setAuthor(dto.getAuthor());
        book.setTitle(dto.getTitle());

        book = bookService.update(book);

        return modelMapper.map(book, BookDTO.class);

    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        Book book = bookService.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        bookService.delete(book);
    }

    @GetMapping
    public Page<BookDTO> find(BookDTO dto, Pageable pageable) {
        Book filter = modelMapper.map(dto, Book.class);
        Page<Book> result = bookService.find(filter, pageable);

        List<BookDTO> books = result.getContent().stream()
                .map(entity -> modelMapper.map(entity, BookDTO.class))
                .collect(Collectors.toList()) ;

        return new PageImpl<BookDTO>(books, pageable, books.size());
    }
}
