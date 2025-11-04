package booktrack.loan;

import booktrack.book.Book;
import booktrack.book.BookRepository;
import booktrack.exception.BookNotAvailableException;
import booktrack.exception.InvalidLoanOperationException;
import booktrack.exception.LoanLimitExceededException;
import booktrack.exception.ResourceNotFoundException;
import booktrack.loan.dto.CreateLoanRequest;
import booktrack.loan.dto.LoanDto;
import booktrack.user.Role;
import booktrack.user.User;
import booktrack.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoanService Unit Tests")
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private LoanMapper loanMapper;

    @InjectMocks
    private LoanService loanService;

    private User testUser;
    private Book testBook;
    private Loan testLoan;
    private LoanDto testLoanDto;
    private CreateLoanRequest createRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@test.com")
                .password("password")
                .firstName("Test")
                .lastName("User")
                .role(Role.USER)
                .build();

        testBook = Book.builder()
                .id(1L)
                .title("Clean Code")
                .author("Robert C. Martin")
                .available(true)
                .build();

        testLoan = Loan.builder()
                .id(1L)
                .user(testUser)
                .book(testBook)
                .loanDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(14))
                .status(LoanStatus.ACTIVE)
                .build();

        testLoanDto = new LoanDto();
        testLoanDto.setId(1L);
        testLoanDto.setStatus(LoanStatus.ACTIVE);

        createRequest = new CreateLoanRequest();
        createRequest.setUserId(1L);
        createRequest.setBookId(1L);
        createRequest.setLoanDate(LocalDate.now());
    }

    @Test
    @DisplayName("Should create loan successfully")
    void shouldCreateLoanSuccessfully() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(loanRepository.countActiveLoansForUser(1L)).thenReturn(0L);
        when(loanRepository.save(any(Loan.class))).thenReturn(testLoan);
        when(loanMapper.toDto(testLoan)).thenReturn(testLoanDto);

        // When
        LoanDto result = loanService.createLoan(createRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(LoanStatus.ACTIVE);

        verify(bookRepository).save(testBook);
        assertThat(testBook.getAvailable()).isFalse();
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        createRequest.setUserId(999L);

        // When & Then
        assertThatThrownBy(() -> loanService.createLoan(createRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found with id 999");

        verifyNoInteractions( loanRepository);
    }

    @Test
    @DisplayName("Should throw exception when book not found")
    void shouldThrowExceptionWhenBookNotFound() {
        // Given
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());
        createRequest.setBookId(999L);

        // When & Then
        assertThatThrownBy(() -> loanService.createLoan(createRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Book not found with id 999");

        verifyNoInteractions(loanRepository);
    }

    @Test
    @DisplayName("Should throw exception when loan limit exceeded")
    void shouldThrowExceptionWhenLoanLimitExceeded() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(loanRepository.countActiveLoansForUser(1L)).thenReturn(3L);

        // When & Then
        assertThatThrownBy(() -> loanService.createLoan(createRequest))
                .isInstanceOf(LoanLimitExceededException.class)
                .hasMessageContaining("User already has 3 active loans");

        verify(loanRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when book not available")
    void shouldThrowExceptionWhenBookNotAvailable() {
        // Given
        testBook.setAvailable(false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(loanRepository.countActiveLoansForUser(1L)).thenReturn(0L);

        // When & Then
        assertThatThrownBy(() -> loanService.createLoan(createRequest))
                .isInstanceOf(BookNotAvailableException.class)
                .hasMessageContaining("Book is not available");

        verify(loanRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return book successfully")
    void shouldReturnBookSuccessfully() {
        // Given
        when(loanRepository.findById(1L)).thenReturn(Optional.of(testLoan));
        when(loanRepository.save(testLoan)).thenReturn(testLoan);
        when(loanMapper.toDto(testLoan)).thenReturn(testLoanDto);

        // When
        LoanDto result = loanService.returnBook(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(testLoan.getStatus()).isEqualTo(LoanStatus.RETURNED);
        assertThat(testLoan.getReturnDate()).isEqualTo(LocalDate.now());
        assertThat(testBook.getAvailable()).isTrue();

        verify(bookRepository).save(testBook);
    }

    @Test
    @DisplayName("Should throw exception when due date before loan date")
    void shouldThrowExceptionWhenDueDateBeforeLoanDate() {
        // Given
        createRequest.setLoanDate(LocalDate.now());
        createRequest.setDueDate(LocalDate.now().minusDays(1));

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(loanRepository.countActiveLoansForUser(1L)).thenReturn(0L);

        // When & Then
        assertThatThrownBy(() -> loanService.createLoan(createRequest))
                .isInstanceOf(InvalidLoanOperationException.class)
                .hasMessageContaining("Due date cannot be before loan date");
    }

    @Test
    @DisplayName("Should return paginated user loans when user exists")
    void shouldReturnUserLoansWhenUserExists() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Loan> loanPage = new PageImpl<>(List.of(testLoan));

        when(userRepository.existsById(1L)).thenReturn(true);
        when(loanRepository.findByUserId(1L, pageable)).thenReturn(loanPage);
        when(loanMapper.toDto(testLoan)).thenReturn(testLoanDto);

        // When
        Page<LoanDto> result = loanService.getUserLoans(1L, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(1L);

        verify(loanRepository).findByUserId(1L, pageable);
        verify(loanMapper).toDto(testLoan);
    }

    @Test
    @DisplayName("Should throw exception when user not found in getUserLoans")
    void shouldThrowExceptionWhenUserNotFoundInGetUserLoans() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        when(userRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> loanService.getUserLoans(999L, pageable))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found with id 999");

        verifyNoInteractions(loanRepository);
    }

    @Test
    @DisplayName("Should return loan by id successfully")
    void shouldReturnLoanByIdSuccessfully() {
        // Given
        when(loanRepository.findById(1L)).thenReturn(Optional.of(testLoan));
        when(loanMapper.toDto(testLoan)).thenReturn(testLoanDto);

        // When
        LoanDto result = loanService.getLoanById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(loanRepository).findById(1L);
        verify(loanMapper).toDto(testLoan);
    }

    @Test
    @DisplayName("Should throw exception when loan not found by id")
    void shouldThrowExceptionWhenLoanNotFoundById() {
        // Given
        when(loanRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> loanService.getLoanById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Loan not found with id 999");
    }

    @Test
    @DisplayName("Should update overdue loans and return them as DTOs")
    void shouldUpdateAndReturnOverdueLoans() {
        // Given
        Loan overdueLoan = Loan.builder()
                .id(2L)
                .loanDate(LocalDate.now().minusDays(15))
                .dueDate(LocalDate.now().minusDays(5))
                .status(LoanStatus.ACTIVE)
                .build();

        when(loanRepository.findOverdueLoans(any(LocalDate.class)))
                .thenReturn(List.of(overdueLoan));
        when(loanMapper.toDto(overdueLoan)).thenReturn(testLoanDto);

        // When
        List<LoanDto> result = loanService.getOverdueLoans();

        // Then
        assertThat(result).hasSize(1);
        assertThat(overdueLoan.getStatus()).isEqualTo(LoanStatus.OVERDUE);

        verify(loanRepository).save(overdueLoan);
        verify(loanMapper).toDto(overdueLoan);
    }

    @Test
    @DisplayName("Should return all loans paginated")
    void shouldReturnAllLoansPaginated() {
        // Given
        Pageable pageable = PageRequest.of(0, 5);
        Page<Loan> loanPage = new PageImpl<>(List.of(testLoan));

        when(loanRepository.findAll(pageable)).thenReturn(loanPage);
        when(loanMapper.toDto(testLoan)).thenReturn(testLoanDto);

        // When
        Page<LoanDto> result = loanService.getAllLoans(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(1L);

        verify(loanRepository).findAll(pageable);
        verify(loanMapper).toDto(testLoan);
    }
}
