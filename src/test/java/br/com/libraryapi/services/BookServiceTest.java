package br.com.libraryapi.services;

import br.com.libraryapi.api.services.BookService;
import br.com.libraryapi.model.entity.Book;
import br.com.libraryapi.api.services.impl.BookServiceImpl;
import br.com.libraryapi.model.repositories.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    BookService service;

    @MockBean
    BookRepository repository;

    @BeforeEach
    public void setUp() {
        this.service = new BookServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBookTest() {
        Book book = Book.builder().isbn("123").author("Zé").title("Homem de Ferro").build();
        Book mock = Book.builder().id(1L).isbn("123").author("Zé").title("Homem de Ferro").build();
        Mockito.when( repository.save(book) ).thenReturn(mock);

        Book savedBook = service.save(book);

        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getIsbn()).isEqualTo("123");
        assertThat(savedBook.getTitle()).isEqualTo("Homem de Ferro");
        assertThat(savedBook.getAuthor()).isEqualTo("Zé");
    }
}
