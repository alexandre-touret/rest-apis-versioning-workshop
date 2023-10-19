package info.touret.bookstore.spring.book.mapper;

import info.touret.bookstore.spring.book.entity.Book;
import info.touret.bookstore.spring.book.generated.dto.BookDto;
import org.mapstruct.Mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface BookMapper {
    Book toBook(BookDto bookDto);

    BookDto toBookDto(Book book);

    List<BookDto> toBookDtos(List<Book> books);


    HashMap<String,String> toMap(Book book);

}
