package booktrack.user.dto;

import booktrack.book.dto.BookDto;
import booktrack.loan.LoanStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class LoanDto {
    private Long id;
    private UserDto user;
    private BookDto book;
    private LocalDate loanDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private LoanStatus status;
    private LocalDateTime createdAt;
}
