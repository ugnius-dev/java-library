package factories;

import interfaces.PublicationFactory;

import publications.Journal;
import abstracts.Publication;

/**
 * Factory class for creating Journal objects.
 * Implements the {@link PublicationFactory} interface.
 */
public class JournalFactory implements PublicationFactory {
  private String editor;
  
  public JournalFactory(String editor) {
    this.editor = editor;
  }

  @Override
  public Publication createPublication(String title, String publisher, int pages, int yearPublished) {
    return new Journal(title, publisher, pages, editor, yearPublished);
  }
}
