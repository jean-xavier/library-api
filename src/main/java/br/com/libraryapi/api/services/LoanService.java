package br.com.libraryapi.api.services;

import br.com.libraryapi.api.resources.BookController;
import br.com.libraryapi.model.entity.Book;
import br.com.libraryapi.model.entity.Loan;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface LoanService {
    Loan save(Loan loan);

    Optional<Loan> getById(Long id);

    Loan update(Loan loan);
}
