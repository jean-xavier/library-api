package br.com.libraryapi.model.repositories;

import br.com.libraryapi.model.entity.Book;
import br.com.libraryapi.model.entity.Loan;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class LoanRepositoryTest {

    @Autowired
    LoanRepository repository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    @DisplayName("Deve verificar se existe empréstimo não devolvido para o livro")
    public void existsByBookAndNotReturnedTest() {
        Book book = Book.builder().title("Essencialismo").author("João").isbn("123654").build();
        entityManager.persist(book);

        Loan loan = Loan.builder().book(book).customer("Maria").loanDate(LocalDate.now()).build();
        entityManager.persist(loan);

        Boolean exists = repository.existsByBookAndNotReturned(book);

        assertThat(exists).isTrue();
    }
}
