package booktrack.loan.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateLoanRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Book ID is required")
    private Long bookId;

    @PastOrPresent(message = "Loan date cannot be in the future")
    private LocalDate loanDate;

    @Future(message = "Due date must be in the future")
    private LocalDate dueDate;
}
