package booktrack.book;

import booktrack.book.dto.BookDto;
import booktrack.book.dto.CreateBookRequest;
import booktrack.book.dto.UpdateBookRequest;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Tag(name = "Books", description = "Book management endpoints")
public class BookController {

    private final BookService bookService;

    @GetMapping("/available")
    @Operation(
            summary = "Get available books",
            description = "Returns a paginated list of available books"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved available books")
    public Page<BookDto> getAvailableBooks(
            @PageableDefault(size = 20, sort = "title") Pageable pageable) {
        return bookService.getAvailableBooks(pageable);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get book by ID",
            description = "Returns a single book by its ID"
    )
    @ApiResponse(responseCode = "200", description = "Book found")
    @ApiResponse(responseCode = "404", description = "Book not found")
    public BookDto getBook(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Create new book (ADMIN only)",
            description = "Creates a new book in the system. Requires ADMIN role."
    )
    @ApiResponse(responseCode = "201", description = "Book created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    public BookDto createBook(@Valid @RequestBody CreateBookRequest book) {
        return bookService.createBook(book);
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Update book (ADMIN only)",
            description = "Updates an existing book. Requires ADMIN role."
    )
    @ApiResponse(responseCode = "200", description = "Book updated successfully")
    @ApiResponse(responseCode = "404", description = "Book not found")
    @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    public BookDto updateBook(@PathVariable Long id,
                              @Valid @RequestBody UpdateBookRequest updateBook) {
        return bookService.updateBook(id, updateBook);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Delete book (ADMIN only)",
            description = "Deletes a book from the system. Requires ADMIN role."
    )
    @ApiResponse(responseCode = "204", description = "Book deleted successfully")
    @ApiResponse(responseCode = "404", description = "Book not found")
    @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
    }
}
