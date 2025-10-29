package booktrack.loan;

import booktrack.loan.dto.CreateLoanRequest;
import booktrack.loan.dto.LoanDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/loans")
public class LoanController {
    private final LoanService loanService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LoanDto createLoan(@Valid @RequestBody CreateLoanRequest request) {
        return loanService.createLoan(request);
    }

    @PutMapping("/{loanId}/return")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public LoanDto returnBook(@PathVariable Long loanId) {
        return loanService.returnBook(loanId);
    }
}
