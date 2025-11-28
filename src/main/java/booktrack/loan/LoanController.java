package booktrack.loan;

import booktrack.loan.dto.CreateLoanRequest;
import booktrack.loan.dto.LoanDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Loans", description = "Loan management endpoints")
public class LoanController {

    private final LoanService loanService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Create new loan",
            description = "Creates a new loan for a user and a selected book."
    )
    @ApiResponse(responseCode = "201", description = "Loan created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data or book unavailable")
    public LoanDto createLoan(@Valid @RequestBody CreateLoanRequest request) {
        return loanService.createLoan(request);
    }

    @PutMapping("/{loanId}/return")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Return book",
            description = "Marks a loan as returned and updates book availability."
    )
    @ApiResponse(responseCode = "200", description = "Book returned successfully")
    @ApiResponse(responseCode = "404", description = "Loan not found")
    public LoanDto returnBook(@PathVariable Long loanId) {
        return loanService.returnBook(loanId);
    }

    @GetMapping("/user/{userId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Get user loans",
            description = "Returns a paginated list of loans for a specific user."
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved user loans")
    public Page<LoanDto> getUserLoans(
            @PathVariable Long userId,
            @PageableDefault(size = 20, sort = "loanDate") Pageable pageable) {
        return loanService.getUserLoans(userId, pageable);
    }

    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Get loan by ID",
            description = "Returns a single loan by its ID."
    )
    @ApiResponse(responseCode = "200", description = "Loan found")
    @ApiResponse(responseCode = "404", description = "Loan not found")
    public LoanDto getLoan(@PathVariable Long id) {
        return loanService.getLoanById(id);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Get all loans (ADMIN only)",
            description = "Returns a paginated list of all loans in the system. Requires ADMIN role."
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved loan list")
    @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    public Page<LoanDto> getAllLoans(
            @PageableDefault(size = 20, sort = "loanDate") Pageable pageable) {
        return loanService.getAllLoans(pageable);
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Get overdue loans (ADMIN only)",
            description = "Returns a list of loans that are past their due date. Requires ADMIN role."
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved overdue loans")
    @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    public List<LoanDto> getOverdueLoans() {
        return loanService.getOverdueLoans();
    }
}
