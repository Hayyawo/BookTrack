package booktrack.book;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("BookRepository Integration Tests")
class BookRepositoryTest {

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private BookRepository bookRepository;

    private Book testBook;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();

        testBook = Book.builder()
                .title("Clean Code")
                .author("Robert C. Martin")
                .isbn("9780132350884")
                .available(true)
                .build();
    }

    @Test
    @DisplayName("Should save and find book by id")
    void shouldSaveAndFindBookById() {
        Book saved = bookRepository.save(testBook);

        Optional<Book> found = bookRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Clean Code");
        assertThat(found.get().getAuthor()).isEqualTo("Robert C. Martin");
    }

    @Test
    @DisplayName("Should find book by ISBN")
    void shouldFindBookByIsbn() {
        bookRepository.save(testBook);

        Optional<Book> found = bookRepository.findByIsbn("9780132350884");

        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Clean Code");
    }

    @Test
    @DisplayName("Should find only available books")
    void shouldFindOnlyAvailableBooks() {
        bookRepository.save(testBook);

        Book unavailableBook = Book.builder()
                .title("Unavailable Book")
                .author("Some Author")
                .available(false)
                .build();
        bookRepository.save(unavailableBook);

        Page<Book> availableBooks = bookRepository.findByAvailableTrue(PageRequest.of(0, 10));

        assertThat(availableBooks.getContent()).hasSize(1);
        assertThat(availableBooks.getContent().get(0).getTitle()).isEqualTo("Clean Code");
    }

    @Test
    @DisplayName("Should update book availability")
    void shouldUpdateBookAvailability() {
        Book saved = bookRepository.save(testBook);

        saved.setAvailable(false);
        bookRepository.save(saved);

        Optional<Book> updated = bookRepository.findById(saved.getId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getAvailable()).isFalse();
    }

    @Test
    @DisplayName("Should delete book")
    void shouldDeleteBook() {
        Book saved = bookRepository.save(testBook);

        bookRepository.deleteById(saved.getId());

        Optional<Book> deleted = bookRepository.findById(saved.getId());
        assertThat(deleted).isEmpty();
    }
}
