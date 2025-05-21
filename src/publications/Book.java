package publications;

import java.util.ArrayList;

import abstracts.Publication;

/**
 * Class representing a book in a library system.
 * It extends the {@link Publication} class and adds specific attributes and methods for managing book data.
 */
public class Book extends Publication {
  private String author;

  /**
   * Default constructor for Book.
   * Initializes the book with default values.
   */
  public Book() {
    super();
    this.author = "Unknown";
  }

  /**
   * Constructor to create a new Book with specified attributes.
   * @param title
   * @param publisher
   * @param pages
   * @param author
   * @param yearPublished
   */
  public Book(String title, String publisher, int pages, String author, int yearPublished) {
    super(title, publisher, pages, yearPublished);
    this.author = author;
    this.borrowHistory = new ArrayList<>();
  }

  public String getAuthor() { return this.author; }
  public void setAuthor(String author) { this.author = author; }

  public void modifyPublication (String title) {
    this.title = "Book: " + title;
  }

  @Override
  public Book clone() throws CloneNotSupportedException {
    Book cloned = (Book) super.clone();
    cloned.borrowHistory = new ArrayList<>();
    return cloned;
  }
}
