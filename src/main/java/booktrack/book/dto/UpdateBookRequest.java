package booktrack.book.dto;


import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UpdateBookRequest {

    @Size(min = 1, max = 255)
    private String title;

    @Size(min = 1, max = 255)
    private String author;

    @Pattern(regexp = "^(97(8|9))?\\d{9}(\\d|X)$")
    private String isbn;

    @Size(max = 255)
    private String publisher;

    @Min(1000)
    @Max(2100)
    private Integer publishYear;

    private Boolean available;
}