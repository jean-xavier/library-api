package br.com.libraryapi.api.resources;

import br.com.libraryapi.api.dto.BookDTO;
import br.com.libraryapi.api.services.BookService;
import br.com.libraryapi.api.services.LoanService;
import br.com.libraryapi.exceptions.BusinessException;
import br.com.libraryapi.model.entity.Book;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = BookController.class)
@AutoConfigureMockMvc
public class BookControllerTest {

    static final String BOOK_API = "/api/books";

    @Autowired
    MockMvc mvc;

    @MockBean
    BookService service;

    @MockBean
    LoanService loanService;

    @Test
    @DisplayName("Deve criar um livro com sucesso")
    public void createBookTest() throws Exception {
        BookDTO dto = createNewBook();
        Book book = Book.builder().id(1L).author("Jorge").title("Vingadores").isbn("152478").build();
        BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(book);

        String json = new ObjectMapper().writeValueAsString(dto);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
            .andExpect(status().isCreated())
            .andExpect(jsonPath("id").value(1L))
            .andExpect(jsonPath("title").value(dto.getTitle()))
            .andExpect(jsonPath("author").value(dto.getAuthor()))
            .andExpect(jsonPath("isbn").value(dto.getIsbn()));
    }

    @Test
    @DisplayName("Deve lançar erro quando não houver dados suficiente para criar um livro")
    public void createInvalidBookTest() throws Exception {
        String json = new ObjectMapper().writeValueAsString(new BookDTO());
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect( status().isBadRequest() )
                .andExpect( jsonPath("errors", hasSize(3)));
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar cadastrar um livro com isbn já utilizado")
    public void createBookWithDuplicatedIsbn() throws Exception {
        BookDTO dto = createNewBook();
        String json = new ObjectMapper().writeValueAsString(dto);
        String msg = "Isbn já cadastrado.";
        BDDMockito.given(service.save(Mockito.any(Book.class))).willThrow(new BusinessException(msg));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect( status().isBadRequest() )
                .andExpect( jsonPath("errors", hasSize(1)) )
                .andExpect( jsonPath("errors[0]").value(msg) ) ;
    }

    @Test
    @DisplayName("Deve obter informações de um livro")
    public void getBookDetailsTest() throws Exception {
        Long id = 1L;
        Book book = createBookWithID(id);
        BDDMockito.given(service.getById(id)).willReturn(Optional.of(book));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
            .andExpect(status().isOk())
            .andExpect( jsonPath("id").value(book.getId()) )
            .andExpect( jsonPath("title").value(book.getTitle()) )
            .andExpect( jsonPath("author").value(book.getAuthor()) )
            .andExpect( jsonPath("isbn").value(book.getIsbn()) );
    }

    @Test
    @DisplayName("Deve retornar resource not found quando o livro não for encontrado")
    public void bookNotFoundTest() throws Exception {
        long id = 1;

        BDDMockito.given(service.getById(Mockito.any())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteBookTest() throws Exception {
        Book book = Book.builder().id(1L).build();
        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.of(book));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + 1));

        mvc.perform(request)
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve retornar resource not found quando não encontrar o livro")
    public void deleteInexistentBookTest() throws Exception {
        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + 1));

        mvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve atualizar livro")
    public void updateBookTest() throws Exception {
        // Given
        Long id = 1L;
        String json = new ObjectMapper().writeValueAsString(createNewBook());
        Book bookWillBeUpdated = Book.builder().id(id).title("Fake news").author("Fake Autor").isbn("321").build();
        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.of(bookWillBeUpdated));
        Book bookUpdated = createBookWithID(id);
        BDDMockito.given(service.update(bookWillBeUpdated)).willReturn(bookUpdated);

        // When
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // Then
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect( jsonPath("id").value(bookUpdated.getId()) )
                .andExpect( jsonPath("title").value(bookUpdated.getTitle()) )
                .andExpect( jsonPath("author").value(bookUpdated.getAuthor()) )
                .andExpect( jsonPath("isbn").value(bookUpdated.getIsbn()) );
    }

    @Test
    @DisplayName("Deve retornar 404 ao tentar atualizar um livro inexistente")
    public void updateInexistentBookTest() throws Exception {
        // Given
        String json = new ObjectMapper().writeValueAsString(createNewBook());
        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

        // When
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // Then
        mvc.perform(request).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve filtrar livros")
    public void findBooksTest() throws Exception {
        Long id = 1L;
        Book book = Book.builder().id(id).isbn("123").author("Ele").title("Os estranhos").build();
        BDDMockito.given( service.find(Mockito.any(Book.class), Mockito.any(Pageable.class)) )
                .willReturn( new PageImpl<Book>(Collections.singletonList(book), PageRequest.of(0, 100), 1) );

        String query = String.format("?title=%s&author=%s&page=0&size=100", book.getTitle(), book.getAuthor());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat(query))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect( status().isOk() )
                .andExpect( jsonPath("content", hasSize(1)) )
                .andExpect( jsonPath("totalElements").value(1) )
                .andExpect( jsonPath( "pageable.pageSize" ).value(100) )
                .andExpect( jsonPath( "pageable.pageNumber" ).value(0) );
    }

    private BookDTO createNewBook() {
        return BookDTO.builder().author("Jorge").title("Vingadores").isbn("152478").build();
    }

    private Book createBookWithID(Long id) {
        return Book.builder().id(id).author("Jorge").title("Vingadores").isbn("152478").build();
    }
}
