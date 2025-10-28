package booktrack.loan;

import booktrack.book.BookMapper;
import booktrack.loan.dto.LoanDto;
import booktrack.user.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class, BookMapper.class})
public interface LoanMapper {
    @Mapping(source = "user", target = "user")
    @Mapping(source = "book", target = "book")
    LoanDto toDto(Loan loan);
}
