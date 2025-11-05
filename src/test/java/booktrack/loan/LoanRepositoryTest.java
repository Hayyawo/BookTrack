package booktrack.loan;

import booktrack.book.Book;
import booktrack.book.BookRepository;
import booktrack.user.Role;
import booktrack.user.User;
import booktrack.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("LoanRepository Integration Tests")
class LoanRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16")
                    .withDatabaseName("booktrack")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void setDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    private User testUser;
    private Book testBook;

    @BeforeEach
    void setUp() {
        loanRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();

        testUser = User.builder()
                .email("test@test.com")
                .password("password")
                .firstName("Test")
                .lastName("User")
                .role(Role.USER)
                .build();
        testUser = userRepository.save(testUser);

        testBook = Book.builder()
                .title("Clean Code")
                .author("Robert C. Martin")
                .available(true)
                .build();
        testBook = bookRepository.save(testBook);
    }

    @Test
    @DisplayName("Should count active loans for user")
    void shouldCountActiveLoansForUser() {
        // Given
        createLoan(testUser, testBook, LoanStatus.ACTIVE);
        createLoan(testUser, testBook, LoanStatus.ACTIVE);
        createLoan(testUser, testBook, LoanStatus.RETURNED);

        // When
        long count = loanRepository.countActiveLoansForUser(testUser.getId());

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Should find overdue loans")
    void shouldFindOverdueLoans() {
        // Given
        Loan overdueLoan = Loan.builder()
                .user(testUser)
                .book(testBook)
                .loanDate(LocalDate.now().minusDays(20))
                .dueDate(LocalDate.now().minusDays(5))
                .status(LoanStatus.ACTIVE)
                .build();
        loanRepository.save(overdueLoan);

        Loan activeLoan = Loan.builder()
                .user(testUser)
                .book(testBook)
                .loanDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(14))
                .status(LoanStatus.ACTIVE)
                .build();
        loanRepository.save(activeLoan);

        // When
        List<Loan> overdueLoans = loanRepository.findOverdueLoans(LocalDate.now());

        // Then
        assertThat(overdueLoans).hasSize(1);
        assertThat(overdueLoans.get(0).getDueDate()).isBefore(LocalDate.now());
    }

    @Test
    @DisplayName("Should find loans by user id")
    void shouldFindLoansByUserId() {
        // Given
        createLoan(testUser, testBook, LoanStatus.ACTIVE);
        createLoan(testUser, testBook, LoanStatus.RETURNED);

        // When
        var loans = loanRepository.findByUserId(testUser.getId(),
                org.springframework.data.domain.PageRequest.of(0, 10));

        // Then
        assertThat(loans.getContent()).hasSize(2);
    }

    private Loan createLoan(User user, Book book, LoanStatus status) {
        Loan loan = Loan.builder()
                .user(user)
                .book(book)
                .loanDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(14))
                .status(status)
                .build();
        return loanRepository.save(loan);
    }
}
