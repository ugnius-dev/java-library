package factories;

import interfaces.PublicationFactory;

import publications.Book;
import abstracts.Publication;

/**
 * Factory class for creating Book objects.
 * Implements the {@link PublicationFactory} interface.
 */
public class BookFactory implements PublicationFactory {
  private String author;
  
  public BookFactory(String author) {
    this.author = author;
  }

  @Override
  public Publication createPublication(String title, String publisher, int pages, int yearPublished) {
    return new Book(title, publisher, pages, author, yearPublished);
  }
}
