package booktrack.book;

import booktrack.book.dto.BookDto;
import booktrack.book.dto.CreateBookRequest;
import booktrack.book.dto.UpdateBookRequest;
import booktrack.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookService Unit Tests")
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookService bookService;

    private Book testBook;
    private BookDto testBookDto;
    private CreateBookRequest createRequest;

    @BeforeEach
    void setUp() {
        testBook = Book.builder()
                .id(1L)
                .title("Clean Code")
                .author("Robert C. Martin")
                .isbn("9780132350884")
                .available(true)
                .build();

        testBookDto = new BookDto();
        testBookDto.setId(1L);
        testBookDto.setTitle("Clean Code");
        testBookDto.setAuthor("Robert C. Martin");
        testBookDto.setAvailable(true);

        createRequest = new CreateBookRequest();
        createRequest.setTitle("Clean Code");
        createRequest.setAuthor("Robert C. Martin");
        createRequest.setIsbn("9780132350884");
    }

    @Test
    @DisplayName("Should get available books successfully")
    void shouldGetAvailableBooksSuccessfully() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        Page<Book> bookPage = new PageImpl<>(List.of(testBook));

        when(bookRepository.findByAvailableTrue(pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(testBook)).thenReturn(testBookDto);

        // When
        Page<BookDto> result = bookService.getAvailableBooks(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Clean Code");

        verify(bookRepository).findByAvailableTrue(pageable);
        verify(bookMapper).toDto(testBook);
    }

    @Test
    @DisplayName("Should get book by id successfully")
    void shouldGetBookByIdSuccessfully() {
        // Given
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(bookMapper.toDto(testBook)).thenReturn(testBookDto);

        // When
        BookDto result = bookService.getBookById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Clean Code");

        verify(bookRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when book not found")
    void shouldThrowExceptionWhenBookNotFound() {
        // Given
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> bookService.getBookById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Book not found with id 999");

        verify(bookRepository).findById(999L);
        verifyNoInteractions(bookMapper);
    }

    @Test
    @DisplayName("Should create book successfully")
    void shouldCreateBookSuccessfully() {
        // Given
        when(bookMapper.toEntity(createRequest)).thenReturn(testBook);
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);
        when(bookMapper.toDto(testBook)).thenReturn(testBookDto);

        // When
        BookDto result = bookService.createBook(createRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Clean Code");
        assertThat(result.getAvailable()).isTrue();

        verify(bookRepository).save(any(Book.class));
    }

    @Test
    @DisplayName("Should update book successfully")
    void shouldUpdateBookSuccessfully() {
        // Given
        UpdateBookRequest updateRequest = new UpdateBookRequest();
        updateRequest.setTitle("Clean Code - Updated");

        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(bookRepository.save(testBook)).thenReturn(testBook);
        when(bookMapper.toDto(testBook)).thenReturn(testBookDto);

        // When
        BookDto result = bookService.updateBook(1L, updateRequest);

        // Then
        assertThat(result).isNotNull();
        verify(bookMapper).updateEntityFromDto(updateRequest, testBook);
        verify(bookRepository).save(testBook);
    }

    @Test
    @DisplayName("Should delete book successfully")
    void shouldDeleteBookSuccessfully() {
        // Given
        when(bookRepository.existsById(1L)).thenReturn(true);

        // When
        bookService.deleteBook(1L);

        // Then
        verify(bookRepository).existsById(1L);
        verify(bookRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent book")
    void shouldThrowExceptionWhenDeletingNonExistentBook() {
        // Given
        when(bookRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> bookService.deleteBook(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Book not found with id 999");

        verify(bookRepository).existsById(999L);
        verify(bookRepository, never()).deleteById(anyLong());
    }
}
