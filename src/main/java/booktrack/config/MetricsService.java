package booktrack.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Service
@Getter
public class MetricsService {

    private final MeterRegistry meterRegistry;

    private final Counter loansCreated;
    private final Counter loansReturned;
    private final Counter booksAdded;
    private final Counter usersRegistered;

    private final Timer loanCreationTimer;
    private final Timer bookSearchTimer;

    public MetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        this.loansCreated = Counter.builder("booktrack.loans.created")
                .description("Total number of loans created")
                .register(meterRegistry);

        this.loansReturned = Counter.builder("booktrack.loans.returned")
                .description("Total number of loans returned")
                .register(meterRegistry);

        this.booksAdded = Counter.builder("booktrack.books.added")
                .description("Total number of books added")
                .register(meterRegistry);

        this.usersRegistered = Counter.builder("booktrack.users.registered")
                .description("Total number of users registered")
                .register(meterRegistry);

        this.loanCreationTimer = Timer.builder("booktrack.loans.creation.time")
                .description("Time taken to create a loan")
                .register(meterRegistry);

        this.bookSearchTimer = Timer.builder("booktrack.books.search.time")
                .description("Time taken to search books")
                .register(meterRegistry);
    }

    public void incrementLoansCreated() {
        loansCreated.increment();
    }

    public void incrementLoansReturned() {
        loansReturned.increment();
    }

    public void incrementBooksAdded() {
        booksAdded.increment();
    }

    public void incrementUsersRegistered() {
        usersRegistered.increment();
    }
}
