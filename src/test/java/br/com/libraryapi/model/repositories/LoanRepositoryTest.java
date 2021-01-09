package br.com.libraryapi.model.repositories;

import br.com.libraryapi.model.entity.Book;
import br.com.libraryapi.model.entity.Loan;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;

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

    @Test
    @DisplayName("Deve buscar empréstimo pelo isbn do livro ou customer")
    public void findByBookIsbnOrCustomerTest() {
        final String isbn = "123654";
        final String customer = "Maria";

        Book book = Book.builder().title("Essencialismo").author("João").isbn(isbn).build();
        entityManager.persist(book);

        Loan loan = Loan.builder().book(book).customer(customer).loanDate(LocalDate.now()).build();
        entityManager.persist(loan);

        Page<Loan> result = repository.findByBookIsbnOrCustomer(isbn, customer, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent()).contains(loan);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Deve obter empréstimos cuja data do emprestimo seja menor ou igual a três dias atrás e não retornados")
    public void findByLoanDateLessThanAndNotReturnedTest() {
        Book book = Book.builder().isbn("123456").author("Fulano").title("Em busca de Fulado").build();
        entityManager.persist(book);

        Loan loan = Loan.builder().loanDate(LocalDate.now().minusDays(5)).customerEmail("fulano@gmail.com").customer("Fulano").book(book).build();
        entityManager.persist(loan);

        List<Loan> loans = repository.findByLoanDateLessThanAndNotReturned(LocalDate.now().minusDays(4));

        assertThat(loans).hasSize(1).contains(loan);
    }

    @Test
    @DisplayName("Deve retornar vazio quando não houver emprestimos atrasados.")
    public void notFindByLoanDateLessThanAndNotReturnedTest() {
        Book book = Book.builder().isbn("123456").author("Fulano").title("Em busca de Fulado").build();
        entityManager.persist(book);

        Loan loan = Loan.builder().loanDate(LocalDate.now()).customerEmail("fulano@gmail.com").customer("Fulano").book(book).build();
        entityManager.persist(loan);

        List<Loan> loans = repository.findByLoanDateLessThanAndNotReturned(LocalDate.now().minusDays(4));

        assertThat(loans).isEmpty();
    }
}
