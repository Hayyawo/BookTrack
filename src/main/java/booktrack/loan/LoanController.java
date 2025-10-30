package booktrack.loan;

import booktrack.loan.dto.CreateLoanRequest;
import booktrack.loan.dto.LoanDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public LoanDto returnBook(@PathVariable Long loanId) {
        return loanService.returnBook(loanId);
    }

    @GetMapping("/user/{userId}")
    public Page<LoanDto> getUserLoans(@PathVariable Long userId,
                                      @PageableDefault(size = 20, sort = "loanDate") Pageable pageable) {
        return loanService.getUserLoans(userId, pageable);
    }

    @GetMapping("/{id}")
    public LoanDto getLoan(@PathVariable Long id) {
        return loanService.getLoanById(id);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Page<LoanDto> getAllLoans(
            @PageableDefault(size = 20, sort = "loanDate") Pageable pageable) {
        return loanService.getAllLoans(pageable);
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasRole('ADMIN')")
    public List<LoanDto> getOverdueLoans() {
        return loanService.getOverdueLoans();
    }
}
