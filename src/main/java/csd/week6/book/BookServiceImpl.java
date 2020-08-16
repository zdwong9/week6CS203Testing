package csd.week6.book;

import java.util.List;
import org.springframework.stereotype.Service;


@Service
public class BookServiceImpl implements BookService {
   
    private BookRepository books;
    

    public BookServiceImpl(BookRepository books){
        this.books = books;
    }

    @Override
    public List<Book> listBooks() {
        return books.findAll();
    }

    
    @Override
    public Book getBook(Long id){
        return books.findById(id).orElse(null);
    }
    
    /**
     * Add logic to avoid adding books with the same title
     * Return null if there exists a book with the same title
     */
    @Override
    public Book addBook(Book book) {
        List<Book> sameTitles = books.findByTitle(book.getTitle());
        if(sameTitles.size() == 0)
            return books.save(book);
        else
            return null;
    }
    
    @Override
    public Book updateBook(Long id, Book newBookInfo){
        return books.findById(id).map(book -> {book.setTitle(newBookInfo.getTitle());
            return books.save(book);
        }).orElse(null);

    }

    /**
     * Remove a book with the given id
     * Spring Data JPA does not return a value for delete operation
     * Cascading: removing a book will also remove all its associated reviews
     */
    @Override
    public void deleteBook(Long id){
        books.deleteById(id);
    }

    /**
     * Count the number of books having the longest title
     * Return: int
     * Note: the current implementation is not correct.
     * 
     * TODO: Activity 1 (Week 6)
     * After running your unit tests, please correct the code here to pass all the tests
     */
    public int countLongestBookTitles(){
        List<Book> allBooks = books.findAll();
        int longestTitle = 0;
        for(Book book: allBooks){
            if(book.getTitle().length() > longestTitle)
                longestTitle = book.getTitle().length(); 
        }
        
        // your code here
        
        return longestTitle;
    }

}