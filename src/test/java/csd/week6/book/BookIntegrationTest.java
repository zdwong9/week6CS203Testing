package csd.week6.book;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.RequestEntity;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import csd.week6.user.User;
import csd.week6.user.UserRepository;

/** Start an actual HTTP server listening at a random port */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class BookIntegrationTest {

	@LocalServerPort
	private int port;

	private final String baseUrl = "http://localhost:";

	@Autowired
	/**
	 * Use TestRestTemplate for testing a real instance of your application as an
	 * external actor.
	 * TestRestTemplate is just a convenient subclass of RestTemplate that is
	 * suitable for integration tests.
	 * It is fault tolerant, and optionally can carry Basic authentication headers.
	 */
	private TestRestTemplate restTemplate;

	@Autowired
	private BookRepository books;

	@Autowired
	private UserRepository users;

	@Autowired
	private BCryptPasswordEncoder encoder;

	@AfterEach
	void tearDown() {
		// clear the database after each test
		books.deleteAll();
		users.deleteAll();
	}

	@Test
	public void getBooks_Success() throws Exception {
		URI uri = new URI(baseUrl + port + "/books");
		books.save(new Book("Gone With The Wind"));

		// Need to use array with a ReponseEntity here
		ResponseEntity<Book[]> result = restTemplate.getForEntity(uri, Book[].class);
		Book[] books = result.getBody();

		assertEquals(200, result.getStatusCode().value());
		assertEquals(1, books.length);
	}

	@Test
	public void getBook_ValidBookId_Success() throws Exception {
		Book book = new Book("Gone With The Wind");
		Long id = books.save(book).getId();
		URI uri = new URI(baseUrl + port + "/books/" + id);

		ResponseEntity<Book> result = restTemplate.getForEntity(uri, Book.class);

		assertEquals(200, result.getStatusCode().value());
		assertEquals(book.getTitle(), result.getBody().getTitle());
	}

	@Test
	public void getBook_InvalidBookId_Failure() throws Exception {
		URI uri = new URI(baseUrl + port + "/books/1");

		ResponseEntity<Book> result = restTemplate.getForEntity(uri, Book.class);

		assertEquals(404, result.getStatusCode().value());
	}

	@Test
	public void addBook_Success() throws Exception {
		URI uri = new URI(baseUrl + port + "/books");
		Book book = new Book("A New Hope");
		users.save(new User("admin", encoder.encode("goodpassword"), "ROLE_ADMIN"));

		ResponseEntity<Book> result = restTemplate.withBasicAuth("admin", "goodpassword")
				.postForEntity(uri, book, Book.class);

		assertEquals(201, result.getStatusCode().value());
		assertEquals(book.getTitle(), result.getBody().getTitle());
	}

	/**
	 * TODO: Activity 2 (Week 6)
	 * Add integration tests for delete/update a book.
	 * For delete operation: there should be two tests for success and failure (book
	 * not found) scenarios.
	 * Similarly, there should be two tests for update operation.
	 * You should assert both the HTTP response code, and the value returned if any
	 * 
	 * For delete and update, you should use restTemplate.exchange method to send
	 * the request
	 * E.g.: ResponseEntity<Void> result = restTemplate.withBasicAuth("admin",
	 * "goodpassword")
	 * .exchange(uri, HttpMethod.DELETE, null, Void.class);
	 */
	// your code here
	public void deleteBook_ValidBookId_Success() throws Exception {
		Book book = new Book("A New Hope");
		URI uri = new URI(baseUrl + port + "/books/" + book.getId());
		books.save(book);

		users.save(new User("admin", encoder.encode("goodpassword"), "ROLE_ADMIN"));

		ResponseEntity<Void> result = restTemplate.withBasicAuth("admin", "goodpassword")
				.exchange(uri, HttpMethod.DELETE, null, Void.class);

		assertEquals(200, result.getStatusCode().value());

		result = restTemplate.withBasicAuth("admin", "goodpassword").exchange(uri, HttpMethod.DELETE, null, Void.class);

		assertEquals(404, result.getStatusCode().value());
	}

	public void deleteBook_inValidBookId_Failure() throws Exception {
		Book book = new Book("A New Hope");
		URI uri = new URI(baseUrl + port + "/books/" + book.getId());

		users.save(new User("admin", encoder.encode("goodpassword"), "ROLE_ADMIN"));

		ResponseEntity<Void> result = restTemplate.withBasicAuth("admin", "goodpassword")
				.exchange(uri, HttpMethod.DELETE, null, Void.class);

		assertEquals(404, result.getStatusCode().value());
	}

	public void updateBook_ValidBookId_Success() throws Exception {

		// arrange book
		Book book = new Book("A New Hope");
		// arrange updated book
		Book newBook = new Book("A Updated Hope");

		// arrange uri
		URI uri = new URI(baseUrl + port + "/books/" + book.getId());

		// mock the save
		books.save(book);

		// arrange and mock save
		users.save(new User("admin", encoder.encode("goodpassword"), "ROLE_ADMIN"));

		RequestEntity<Book> request = RequestEntity
				.post(uri)
				.accept(MediaType.APPLICATION_JSON)
				.body(newBook);

		ResponseEntity<Book> result = restTemplate.withBasicAuth("admin", "goodpassword")
				.exchange(request, Book.class);

		assertEquals(200, result.getStatusCode().value());
		assertEquals(newBook.getTitle(), result.getBody().getTitle());
	}

	public void updateBook_inValidBookId_Failure() throws Exception {

		// arrange book
		Book book = new Book("A New Hope");

		System.out.println("hellooo");
		System.out.println("hellooo");
		System.out.println("hellooo");
		System.out.println("hellooo");

		// arrange uri
		URI uri = new URI(baseUrl + port + "/books/" + book.getId());

		// arrange and mock save
		users.save(new User("admin", encoder.encode("goodpassword"), "ROLE_ADMIN"));

		RequestEntity<Book> request = RequestEntity
				.post(uri)
				.accept(MediaType.APPLICATION_JSON)
				.body(book);

		ResponseEntity<Book> result = restTemplate.withBasicAuth("admin", "goodpassword")
				.exchange(request, Book.class);

		assertEquals(404, result.getStatusCode().value());

		System.out.println("hello hello");
	}
}
