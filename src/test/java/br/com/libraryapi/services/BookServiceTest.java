package br.com.libraryapi.services;

import br.com.libraryapi.api.services.BookService;
import br.com.libraryapi.api.services.impl.BookServiceImpl;
import br.com.libraryapi.exceptions.BusinessException;
import br.com.libraryapi.model.entity.Book;
import br.com.libraryapi.model.repositories.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

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
        Book book = createValidBook();
        Book mock = Book.builder().id(1L).isbn("123").author("Zé").title("Homem de Ferro").build();
        Mockito.when( repository.existsByIsbn(Mockito.anyString()) ).thenReturn(false);
        Mockito.when( repository.save(book) ).thenReturn(mock);

        Book savedBook = service.save(book);

        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getIsbn()).isEqualTo("123");
        assertThat(savedBook.getTitle()).isEqualTo("Homem de Ferro");
        assertThat(savedBook.getAuthor()).isEqualTo("Zé");
    }

    @Test
    @DisplayName("Deve lançar erro de negocio ao tentar salvar um livro com isbn duplicado")
    public void shouldNotSaveBookWithDuplicatedISBN() {
        Book book = createValidBook();
        Mockito.when( repository.existsByIsbn(Mockito.anyString()) ).thenReturn(true);

        Throwable exception = catchThrowable( () -> service.save(book) );

        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Isbn já cadastrado.");
        Mockito.verify(repository, Mockito.never()).save(book);
    }

    @Test
    @DisplayName("Deve obter um livro por ID")
    public void getByIDTest() {
        Long id = 1L;
        Book book = createValidBook();
        book.setId(id);
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(book));

        Optional<Book> foundBook = service.getById(id);

        assertThat(foundBook.isPresent()).isTrue();
        assertThat(foundBook.get().getId()).isEqualTo(book.getId());
        assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
        assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
        assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
    }

    @Test
    @DisplayName("Deve retornar vazio ao quando não econtrado livro com id passado")
    public void bookNotFoundByIDTest() {
        Long id = 1L;
        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        Optional<Book> book = service.getById(id);

        assertThat(book.isPresent()).isFalse();
    }

    @Test
    @DisplayName("Deve atualizar livro com sucesso")
    public void updateBookTest() {
        Book bookWillBeUpdated = createValidBook();
        bookWillBeUpdated.setId(1L);
        Book bookAfterUpdated = Book.builder().id(1L).title("Title Fake").author("Author Fake").isbn("4857").build();
        Mockito.when(repository.save(bookWillBeUpdated)).thenReturn(bookAfterUpdated);

        Book updatedBook = service.update(bookWillBeUpdated);

        assertThat(updatedBook.getId()).isEqualTo(bookAfterUpdated.getId());
        assertThat(updatedBook.getAuthor()).isEqualTo(bookAfterUpdated.getAuthor());
        assertThat(updatedBook.getTitle()).isEqualTo(bookAfterUpdated.getTitle());
        assertThat(updatedBook.getIsbn()).isEqualTo(bookAfterUpdated.getIsbn());
    }

    @Test
    @DisplayName("Deve lançar erro quando passar um livro null para atualizar")
    public void updateInvalidBookTest() {
        Book book = Book.builder().build();

        Throwable exception = catchThrowable( () -> service.update(book) );

        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Book id can't be null");
        Mockito.verify(repository, Mockito.never()).save(book);
    }

    @Test
    @DisplayName("Deve excluir livro com sucesso")
    public void deleteBookTest() {
        Book book = Book.builder().id(1L).isbn("123").title("Vingadores").author("Fulado").build();

        assertDoesNotThrow( () -> service.delete(book) ); ;

        Mockito.verify(repository, Mockito.times(1)).delete(book);
    }

    @Test
    @DisplayName("Deve lançar erro quando passar um livro vazio para deleção")
    public void deleteInvalidBookTest() {
        Book book = Book.builder().build();

        Throwable exception = catchThrowable( () -> service.delete(book) );

        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Book id can't be null");
        Mockito.verify(repository, Mockito.never()).delete(book);
    }

    private Book createValidBook() {
        return Book.builder().isbn("123").author("Zé").title("Homem de Ferro").build();
    }
}
