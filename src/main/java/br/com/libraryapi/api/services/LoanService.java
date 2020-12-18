package br.com.libraryapi.api.services;

import br.com.libraryapi.model.entity.Loan;
import org.springframework.stereotype.Service;

@Service
public interface LoanService {
    Loan save(Loan loan);
}
