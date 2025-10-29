package booktrack.loan;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan, Integer> {
    @Query("SELECT COUNT(l) FROM Loan l WHERE l.user.id = :userId AND l.status = 'ACTIVE'")
    long countActiveLoansForUser(Long userId);

    @Query("SELECT l FROM Loan l WHERE l.status = 'ACTIVE' AND l.dueDate < :date")
    List<Loan> findOverdueLoans(LocalDate date);

    Optional<Loan> findById(long id);

    Page<Loan> findByUserId(Long userId, Pageable pageable);
}
