CREATE TABLE books (
                       id BIGSERIAL PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       author VARCHAR(255) NOT NULL,
                       isbn VARCHAR(20) UNIQUE,
                       publisher VARCHAR(255),
                       publish_year INTEGER,
                       available BOOLEAN NOT NULL DEFAULT true,
                       created_at TIMESTAMP NOT NULL
);