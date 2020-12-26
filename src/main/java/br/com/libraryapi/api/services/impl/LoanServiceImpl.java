package br.com.libraryapi.api.services.impl;

import br.com.libraryapi.api.services.LoanService;
import br.com.libraryapi.exceptions.BusinessException;
import br.com.libraryapi.model.entity.Book;
import br.com.libraryapi.model.entity.Loan;
import br.com.libraryapi.model.repositories.LoanRepository;

import java.util.Optional;

public class LoanServiceImpl implements LoanService {
    private final LoanRepository repository;

    public LoanServiceImpl(LoanRepository repository) {
        this.repository = repository;
    }

    @Override
    public Loan save(Loan loan) {
        boolean loanExists = repository.existsByBookAndNotReturned(loan.getBook());

        if (loanExists) {
            throw new BusinessException("Book already loaned");
        }

        return repository.save(loan);
    }

    @Override
    public Optional<Loan> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Loan update(Loan loan) {
        return repository.save(loan);
    }
}
