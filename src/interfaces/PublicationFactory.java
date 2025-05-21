package interfaces;

import abstracts.Publication;

/**
 * Interface for creating different types of publications.
 * This interface defines a method to create a publication with specific attributes.
 */
public interface PublicationFactory {
  Publication createPublication(String title, String publisher, int pages, int yearPublished);
}