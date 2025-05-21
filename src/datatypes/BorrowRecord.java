package datatypes;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Represents a record of a book borrowed by a reader.
 * It contains information about the reader, the date of borrowing,
 * the due date for returning the book, and the actual return date.
 * This class is used to track the borrowing history of books in the library system.
 */
public class BorrowRecord implements Serializable {
    private String readerName;
    private LocalDateTime borrowDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate = null;

    /**
     * Constructor to create a new BorrowRecord.
     *
     * @param readerName The name of the reader who borrowed the book.
     * @param borrowDate The date and time when the book was borrowed.
     * @param dueDate    The date and time when the book is due to be returned.
     */
    public BorrowRecord(String readerName, LocalDateTime borrowDate, LocalDateTime dueDate) {
      this.readerName = readerName;
      this.borrowDate = borrowDate;
      this.dueDate = dueDate;
    }

    public String getReader() {
      return readerName;
    }

    public LocalDateTime getBorrowDate() {
      return borrowDate;
    }

    public LocalDateTime getDueDate() {
      return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
      this.dueDate = dueDate;
    }

    public LocalDateTime getReturnDate() {
      return returnDate;
    }

    public void setReturnDate(LocalDateTime returnDate) {
      this.returnDate = returnDate;
    }
}
