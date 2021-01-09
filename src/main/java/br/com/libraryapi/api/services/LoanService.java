package br.com.libraryapi.api.services;

import br.com.libraryapi.api.dto.LoanFilterDTO;
import br.com.libraryapi.model.entity.Book;
import br.com.libraryapi.model.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface LoanService {
    Loan save(Loan loan);

    Optional<Loan> getById(Long id);

    Loan update(Loan loan);

    Page<Loan> find(LoanFilterDTO filter, Pageable page);

    Page<Loan> getLoansByBook(Book book, Pageable pageable);
}
