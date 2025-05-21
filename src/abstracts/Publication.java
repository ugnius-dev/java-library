package abstracts;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

import datatypes.BorrowRecord;
import datatypes.YearPublished;
import exceptions.*;

/**
 * Abstract class representing a publication in a library system.
 * It contains common attributes and methods for managing publication data.
 * The class also provides methods for borrowing and returning publications.
 */
public abstract class Publication implements Cloneable, Serializable {
  protected String title;
  private String publisher;
  private int pages;

  private YearPublished yearPublished = null;

  protected ArrayList<BorrowRecord> borrowHistory = null;
  private boolean availability = true;

  /**
   * Default constructor for Publication.
   */
  public Publication() {
    this("Unknown title", "Unknown publisher", 0, 0);
  }

  /**
   * Constructor to create a new Publication with specified attributes.
   * @param title         The title of the publication.
   * @param publisher     The publisher of the publication.
   * @param pages         The number of pages in the publication.
   * @param yearPublished The year the publication was published.
   */
  public Publication(String title, String publisher, int pages, int yearPublished) {
    this.publisher = publisher;
    this.title = title;
    this.pages = pages;
    this.yearPublished = new YearPublished(yearPublished);
  }

  // Getters and setters
  public String getTitle() { return this.title; }
  public void setTitle(String title) { this.title = title; }

  public String getPublisher() { return this.publisher; }
  public void setPublisher(String publisher) { this.publisher = publisher; }

  public int getPages() { return this.pages; }
  public void setPages(int pages) { this.pages = pages; }

  public String getYearPublished() { return this.yearPublished.toString(); }
  public void setYearPublished(int yearPublished) { this.yearPublished.setYearPublished(yearPublished); }

  
  /**
   * Modifies the publication's title.
   * @param title
   */
  public void modifyPublication (String title) {
    this.title = title;
  }

  /**
   * Modifies the publication's title and number of pages.
   * @param title
   * @param pages
   */
  public void modifyPublication (String title, int pages) {
    this.title = title;
    this.pages = pages;
  }


  /**
   * Checks if the publication is available for borrowing.
   * @return true if the publication is available, false otherwise.
   */
  public boolean isAvailable() {
    return this.availability;
  }

  /**
   * Borrows the publication for a specified reader and due date.
   * @param reader
   * @param borrowDue
   * @throws DateInTheFutureException
   */
  public void borrowBook(String reader, LocalDateTime borrowDue) throws DateInTheFutureException {
    this.borrowBook(reader, borrowDue, LocalDateTime.now());
  }

  /**
   * Borrows the publication for a specified reader, due date, and borrow date.
   * @param reader
   * @param borrowDue
   * @param borrowDate
   * @throws DateInTheFutureException
   */
  public void borrowBook(String reader, LocalDateTime borrowDue, LocalDateTime borrowDate) throws DateInTheFutureException {
    if (borrowDate.isAfter(LocalDateTime.now())) {
      throw new DateInTheFutureException("Borrow date cannot be in the future", borrowDate);
    }

    if (borrowDue.isBefore(LocalDateTime.now())) {
      throw new DateInTheFutureException("Due date cannot be before borrow date", borrowDue);
    }

    if (availability && borrowHistory != null) {
      this.availability = false;
      this.borrowHistory.add(0, new BorrowRecord(reader, borrowDate, borrowDue));
    }
  }

  /**
   * This method updates the availability status and the return date of the publication.
   */
  public void returnBook() {
    LocalDateTime returnDate = LocalDateTime.now();
    
    if (!this.availability) {
      if (this.borrowHistory != null && !this.borrowHistory.isEmpty()) {
        this.availability = true;
        this.borrowHistory.get(0).setReturnDate(returnDate);
      }
    }
  }

  /**
   * Returns the borrow history of the publication.
   * @return ArrayList of BorrowRecord objects representing the borrow history.
   */
  public ArrayList<BorrowRecord> getBorrowHistory() {
    return this.borrowHistory;
  }

  /**
   * Checks if the publication can be borrowed.
   * @return true if the publication can be borrowed, false otherwise.
   */
  public boolean canBeBorrowed() {
    return !(this.borrowHistory == null);
  } 

  @Override
  protected Object clone() throws CloneNotSupportedException {
    Publication cloned = (Publication) super.clone();
    cloned.yearPublished = (YearPublished) this.yearPublished.clone();
    cloned.borrowHistory = null;
    cloned.availability = true;
    return cloned;
  }
}
