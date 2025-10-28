package booktrack.book;

import booktrack.book.dto.BookDto;
import booktrack.book.dto.CreateBookRequest;
import booktrack.book.dto.UpdateBookRequest;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BookMapper {

    BookDto toDto(Book book);

    Book toEntity(CreateBookRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UpdateBookRequest dto, @MappingTarget Book entity);
}
