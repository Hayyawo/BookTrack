package booktrack.book;

import booktrack.book.dto.BookDto;
import booktrack.book.dto.CreateBookRequest;
import booktrack.book.dto.UpdateBookRequest;
import booktrack.config.MetricsService;
import booktrack.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    private final MetricsService metricsService;

    @Cacheable(value = "availableBooks")
    public Page<BookDto> getAvailableBooks(Pageable pageable) {
        return bookRepository.findByAvailableTrue(pageable)
                .map(bookMapper::toDto);
    }

    @Cacheable(value = "books", key = "#id")
    public BookDto getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + id));
        return bookMapper.toDto(book);
    }

    @Transactional
    @CacheEvict(value = {"books", "availableBooks"}, allEntries = true)
    public BookDto createBook(CreateBookRequest book) {
        Book bookMapperEntity = bookMapper.toEntity(book);
        bookMapperEntity.setAvailable(true);
        Book bookEntity = bookRepository.save(bookMapperEntity);
        metricsService.incrementBooksAdded();
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

    @Transactional
    public void deleteBook(long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Book not found with id " + id);
        }
        bookRepository.deleteById(id);
    }
}