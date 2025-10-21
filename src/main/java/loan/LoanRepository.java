package loan;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Integer> {
    @Query("SELECT COUNT(l) FROM Loan l WHERE l.user.id = :userId AND l.status = 'ACTIVE'")
    long countActiveLoansForUser(Long userId);

    @Query("SELECT l FROM Loan l WHERE l.status = 'ACTIVE' AND l.dueDate < :date")
    List<Loan> findOverdueLoans(LocalDate date);
}
