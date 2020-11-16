package br.com.libraryapi.api.resources;

import br.com.libraryapi.api.dto.BookDTO;
import br.com.libraryapi.api.exceptions.ApiErrors;
import br.com.libraryapi.api.services.BookService;
import br.com.libraryapi.exceptions.BusinessException;
import br.com.libraryapi.model.entity.Book;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Optional;

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

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        Book book = bookService.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        bookService.delete(book);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleValidationExceptions(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        return new ApiErrors(bindingResult);
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleValidationExceptions(BusinessException e) {
        return new ApiErrors(e);
    }
}
