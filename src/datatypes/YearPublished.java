package datatypes;

import java.io.Serializable;

/**
 * Represents the year a book was published.
 * This class is used to store and manage the publication year of books in the library system.
 */
public class YearPublished implements Cloneable, Serializable {
  private int yearPublished = 0;

  public YearPublished() {
    this.yearPublished = 0;
  }

  public YearPublished(int yearPublished) {
    this.yearPublished = yearPublished;
  }

  public void setYearPublished(int yearPublished) {
    this.yearPublished = yearPublished;
  }

  public int getYearPublished() {
    return this.yearPublished;
  }

  public String toString() {
    return this.yearPublished + "";
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
}
