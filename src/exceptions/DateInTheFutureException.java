package exceptions;

import java.time.LocalDateTime;

/**
 * Exception thrown when a date is in the future.
 * This exception is used to indicate that a date provided
 * by the user is in the future and not valid for the current context.
 */
public class DateInTheFutureException extends PublicationException {
  private LocalDateTime dateTime = null;

  /**
   * Constructor to create a new DateInTheFutureException.
   *
   * @param message The error message to be displayed.
   * @param futureDate The date that is in the future.
   */
  public DateInTheFutureException(String message, LocalDateTime futureDate) {
    super(message);
    this.dateTime = futureDate;
  }

  /**
   * @return The future date that caused the exception.
   */
  public LocalDateTime getFutureDate() {
    return dateTime;
  } 
}
