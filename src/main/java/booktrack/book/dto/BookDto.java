package booktrack.book.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BookDto {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private String publisher;
    private Integer publishYear;
    private Boolean available;
    private LocalDateTime createdAt;
}