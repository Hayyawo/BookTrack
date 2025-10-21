package booktrack.book.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateBookRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    private String title;

    @NotBlank(message = "Author is required")
    @Size(min = 1, max = 255, message = "Author must be between 1 and 255 characters")
    private String author;

    @Pattern(regexp = "^(97(8|9))?\\d{9}(\\d|X)$", message = "Invalid ISBN format")
    private String isbn;

    @Size(max = 255, message = "Publisher name too long")
    private String publisher;

    @Min(value = 1000, message = "Publish year must be after 1000")
    @Max(value = 2100, message = "Publish year must be before 2100")
    private Integer publishYear;
}
