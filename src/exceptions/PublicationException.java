package exceptions;

/**
 * Exception thrown when there is an issue with publication data.
 */
public class PublicationException extends Exception {
  /**
   * Default constructor for PublicationException.
   */
  public PublicationException() {
    super();
  }

  /**
   * Constructor to create a new PublicationException with a specific message.
   * @param message The error message to be displayed.
   */
  public PublicationException(String message) {
    super(message);
  }
  
}
