package booktrack.book;

import booktrack.book.dto.BookDto;
import booktrack.book.dto.BookMapper;
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
    public Book createBook(Book book) {
        return bookRepository.save(book);
    }
}