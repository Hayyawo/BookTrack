package booktrack.loan;

import booktrack.book.Book;
import booktrack.book.BookRepository;
import booktrack.exception.BookNotAvailableException;
import booktrack.exception.InvalidLoanOperationException;
import booktrack.exception.LoanLimitExceededException;
import booktrack.exception.ResourceNotFoundException;
import booktrack.loan.dto.CreateLoanRequest;
import booktrack.loan.dto.LoanDto;
import booktrack.user.User;
import booktrack.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoanService {
    private static final int MAX_ACTIVE_LOANS = 3;
    private static final int DEFAULT_LOAN_PERIOD_DAYS = 14;

    private final LoanRepository loanRepository;
    private final LoanMapper loanMapper;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Transactional
    public LoanDto createLoan(CreateLoanRequest request) {
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + request.getBookId()));
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + request.getUserId()));

        long activeLoans = loanRepository.countActiveLoansForUser(user.getId());
        if (activeLoans >= MAX_ACTIVE_LOANS) {
            throw new LoanLimitExceededException(
                    String.format("User already has %d active loans. Maximum is %d.", activeLoans, MAX_ACTIVE_LOANS)
            );
        }

        if (Boolean.FALSE.equals(book.getAvailable())) {
            throw new BookNotAvailableException("Book is not available: " + book.getTitle());
        }

        LocalDate loanDate = request.getLoanDate();
        LocalDate dueDate = request.getDueDate() != null
                ? request.getDueDate()
                : loanDate.plusDays(DEFAULT_LOAN_PERIOD_DAYS);

        if (dueDate.isBefore(loanDate)) {
            throw new InvalidLoanOperationException("Due date cannot be before loan date");
        }

        Loan loan = Loan.builder()
                .loanDate(loanDate)
                .dueDate(dueDate)
                .status(LoanStatus.ACTIVE)
                .user(user)
                .book(book)
                .build();

        book.setAvailable(false);
        bookRepository.save(book);

        Loan savedLoan = loanRepository.save(loan);
        return loanMapper.toDto(savedLoan);
    }

    @Transactional
    public LoanDto returnBook(long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));

        loan.setStatus(LoanStatus.RETURNED);
        loan.setReturnDate(LocalDate.now());

        Book book = loan.getBook();
        book.setAvailable(true);

        bookRepository.save(book);
        loanRepository.save(loan);

        return loanMapper.toDto(loan);
    }

    public Page<LoanDto> getUserLoans(long userId, Pageable pageable){
        if(!userRepository.existsById(userId)){
            throw new ResourceNotFoundException("User not found with id " + userId);
        }

        return loanRepository.findByUserId(userId, pageable)
                .map(loanMapper::toDto);
    }

    public LoanDto getLoanById(long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with id " + loanId));

        return loanMapper.toDto(loan);
    }

    public List<LoanDto> getOverdueLoans() {
        List<Loan> overdueLoans = loanRepository.findOverdueLoans(LocalDate.now());

        overdueLoans.forEach(loan -> {
            if (loan.getStatus() == LoanStatus.ACTIVE) {
                loan.setStatus(LoanStatus.OVERDUE);
                loanRepository.save(loan);
            }
        });

        return overdueLoans.stream()
                .map(loanMapper::toDto)
                .toList();
    }

    public Page<LoanDto> getAllLoans(Pageable pageable) {
        return loanRepository.findAll(pageable)
                .map(loanMapper::toDto);
    }
}
