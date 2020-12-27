package br.com.libraryapi.api.resources;

import br.com.libraryapi.api.dto.BookDTO;
import br.com.libraryapi.api.dto.LoanDTO;
import br.com.libraryapi.api.dto.LoanFilterDTO;
import br.com.libraryapi.api.dto.ReturnedLoanDTO;
import br.com.libraryapi.api.services.BookService;
import br.com.libraryapi.api.services.LoanService;
import br.com.libraryapi.model.entity.Book;
import br.com.libraryapi.model.entity.Loan;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/loans")
public class LoanController {
    private final BookService bookService;
    private final LoanService loanService;
    private final ModelMapper modelMapper;

    public LoanController(BookService bookService, LoanService loanService) {
        this.bookService = bookService;
        this.loanService = loanService;
        modelMapper = new ModelMapper();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody LoanDTO dto) {
        Book book = bookService
                .getBookByIsbn(dto.getIsbn())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found for passed isbn"));

        Loan loan = Loan.builder().
                book(book)
                .customer(dto.getCustomer())
                .loanDate(LocalDate.now())
                .build();

        loan = loanService.save(loan);

        return loan.getId();
    }

    @PatchMapping("{id}")
    public void returnBook(@PathVariable Long id, @RequestBody ReturnedLoanDTO dto) {
        Loan loan = loanService.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        loan.setReturned(dto.getReturned());

        loanService.update(loan);
    }

    @GetMapping
    public Page<LoanDTO> find(LoanFilterDTO dto, Pageable pageable) {
        Page<Loan> loans = loanService.find(dto, pageable);

        List<LoanDTO> dtos = loans.getContent().stream()
                .map(entity -> {
                    Book book = entity.getBook();

                    BookDTO bookDTO = modelMapper.map(book, BookDTO.class);
                    LoanDTO loanDTO = modelMapper.map(entity, LoanDTO.class);
                    loanDTO.setBook(bookDTO);

                    return loanDTO;
                }).collect(Collectors.toList());

        return new PageImpl<LoanDTO>(dtos, pageable, loans.getTotalElements());
    }
}
