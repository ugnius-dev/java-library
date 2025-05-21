package publications;

import abstracts.Publication;

/**
 * Class representing a journal in a library system.
 * It extends the {@link Publication} class and adds specific attributes and methods for managing journal data.
 */
public class Journal extends Publication {
  private String editor;
  
  /**
   * Default constructor for Journal.
   * Initializes the journal with default values.
   */
  public Journal() {
    super();
    this.editor = "Unknown";
    this.borrowHistory = null;
  }

  /**
   * Constructor to create a new Journal with specified attributes.
   * @param title
   * @param publisher
   * @param pages
   * @param editor
   * @param yearPublished
   */
  public Journal(String title, String publisher, int pages, String editor, int yearPublished) {
    super(title, publisher, pages, yearPublished);
    this.editor = editor;
  }

  public String getEditor() { return this.editor; }
  public void setEditor(String editor) { this.editor = editor; }

  public void modifyPublication (String title) {
    this.title = "Journal: " + title;
  }

}
