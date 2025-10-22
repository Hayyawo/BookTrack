package booktrack.book;

import booktrack.book.dto.BookDto;
import booktrack.book.dto.BookMapper;
import booktrack.book.dto.CreateBookRequest;
import booktrack.book.dto.UpdateBookRequest;
import booktrack.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public Page<BookDto> getAvailableBooks(Pageable pageable) {
        return bookRepository.findByAvailableTrue(pageable)
                .map(bookMapper::toDto);
    }

    public BookDto getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        return bookMapper.toDto(book);
    }

    @Transactional
    public BookDto createBook(CreateBookRequest book) {
        Book bookMapperEntity = bookMapper.toEntity(book);
        Book bookEntity = bookRepository.save(bookMapperEntity);
        return bookMapper.toDto(bookEntity);
    }

    @Transactional
    public BookDto updateBook(long id, UpdateBookRequest book) {
        Book foundBook = bookRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Book not found"));
         bookMapper.updateEntityFromDto(book, foundBook);
        Book updatedBook = bookRepository.save(foundBook);
        return bookMapper.toDto(updatedBook);
    }
}