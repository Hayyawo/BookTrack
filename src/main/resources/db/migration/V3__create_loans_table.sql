CREATE TABLE loans (
                       id BIGSERIAL PRIMARY KEY,
                       user_id BIGINT NOT NULL REFERENCES users(id),
                       book_id BIGINT NOT NULL REFERENCES books(id),
                       loan_date DATE NOT NULL,
                       due_date DATE NOT NULL,
                       return_date DATE,
                       status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
                       created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_loans_user_id ON loans(user_id);
CREATE INDEX idx_loans_book_id ON loans(book_id);
CREATE INDEX idx_loans_status ON loans(status);