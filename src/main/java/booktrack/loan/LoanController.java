package booktrack.loan;

import booktrack.loan.dto.CreateLoanRequest;
import booktrack.loan.dto.LoanDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController("/api/loans")
public class LoanController {
    private final LoanService loanService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LoanDto createLoan(@Valid @RequestBody CreateLoanRequest request) {
        return loanService.createLoan(request);
    }
}
