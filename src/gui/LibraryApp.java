package gui;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import abstracts.Publication;
import datatypes.BorrowRecord;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import publications.*;

public class LibraryApp {
  private static final String SERIALIZATION_FILE = "shelfList.ser";

  private Stage stage;
  private StackPane root;
  private Label statusBar;
  private FlowPane bookFlow;

  private ArrayList<Publication> shelfList = new ArrayList<Publication>();

  private Rectangle overlay;

  private ObservableMap<String, VBox> modals = FXCollections.observableHashMap();
  private ArrayList<String> openedModals = new ArrayList<>();
  private ArrayList<String> disposableModals = new ArrayList<>();

  public LibraryApp(Stage stage) {
    this.stage = stage;
  }

  /**
   * Sets the status message in the status bar.
   * @param message The message to be displayed in the status bar.
   */
  private void setStatus(String message) {
    statusBar.setText(message);
  }

  private void overlayVisibility(boolean visible) {
    overlay.setVisible(visible);
  }
    

  /**
   * Initializes the GUI components and sets up the main layout.
   * This method creates the main window.
   */
  public void start() {
    root = new StackPane();
    BorderPane mainLayout = new BorderPane();

    // Mygtukai viršuje
    Button addButton = new Button("Nauja knyga");
    Button saveButton = new Button("Išsaugoti");
    Button loadButton = new Button("Įkelti");
    Button exitButton = new Button("Išeiti");

    saveButton.setOnAction(e -> { serializeData(SERIALIZATION_FILE); });
    loadButton.setOnAction(e -> { serializeLoadData(SERIALIZATION_FILE); renderBooks(); });
    exitButton.setOnAction(e -> { serializeData(SERIALIZATION_FILE); stage.close(); });

    HBox topBar = new HBox();
    topBar.setSpacing(0);
    topBar.setPadding(new Insets(0));
    topBar.setPrefHeight(50);
    topBar.setStyle("-fx-background-color: #f0f0f0;");

    List<Button> topButtons = Arrays.asList(addButton, saveButton, loadButton, exitButton);
    for (Button btn : topButtons) {
      btn.getStyleClass().add("top-bar-btn");
      btn.setMaxWidth(Double.MAX_VALUE);
      HBox.setHgrow(btn, Priority.ALWAYS);
    }

    topBar.getChildren().addAll(addButton, saveButton, loadButton, exitButton);
    mainLayout.setTop(topBar);

    // Knygų sąrašas
    bookFlow = new FlowPane();
    bookFlow.setHgap(20);
    bookFlow.setVgap(20);
    bookFlow.setPadding(new Insets(15));

    ScrollPane scrollBookPane = new ScrollPane(bookFlow);
    scrollBookPane.setFitToWidth(true);
    scrollBookPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    scrollBookPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    scrollBookPane.setStyle("-fx-background-color:transparent;");

    mainLayout.setCenter(scrollBookPane);

    // Statuso juosta apačioje
    statusBar = new Label("Sveiki atvykę į biblioteką!");
    statusBar.setMaxWidth(Double.MAX_VALUE);
    statusBar.getStyleClass().add("status-bar");
    mainLayout.setBottom(statusBar);

    root.getChildren().add(mainLayout);

    // Overlay
    overlay = new Rectangle();
    overlay.setFill(Color.rgb(0, 0, 0, 0.5));
    overlay.widthProperty().bind(root.widthProperty());
    overlay.heightProperty().bind(root.heightProperty());
    overlay.setVisible(false);
    root.getChildren().add(overlay);

    // Modalo kurimas
    createModalForm();
    createBookInfoModal();

    addButton.setOnAction(e -> {
      openModal("newPublication");
    });

    Scene scene = new Scene(root, 800, 600);
    scene.getStylesheets().add(getClass().getResource("../resources/style.css").toExternalForm());

    scene.setOnKeyPressed(event -> {
      if (event.getCode() == KeyCode.ESCAPE) closeAllModals();
    });

    stage.setScene(scene);
    stage.setTitle("Biblioteka");
    stage.show();
  }

  private VBox createBookCard(int publicationIndex) {
    VBox bookCard = new VBox(10);
    bookCard.getStyleClass().add("book-card");

    bookCard.setMaxWidth(150);
    bookCard.setMinWidth(150);

    Publication publication = shelfList.get(publicationIndex);

    Label statusBar = new Label();
    statusBar.setPrefHeight(18);
    statusBar.setMaxWidth(Double.MAX_VALUE);
    statusBar.setAlignment(Pos.CENTER);
    statusBar.setText(!publication.isAvailable() ? "Paskolinta" : "Prieinama");
    statusBar.setStyle(!publication.isAvailable()
      ? "-fx-background-color: #ff6b6b; -fx-text-fill: white; -fx-font-size: 11px; -fx-background-radius: 5;"
      : "-fx-background-color: #51cf66; -fx-text-fill: white; -fx-font-size: 11px; -fx-background-radius: 5;"
    );

    // Book image
    String imageName = "book.png";
    if (publication instanceof Journal) {
      imageName = "journal.png";
    }
    ImageView bookImage = new ImageView(getClass().getResource("../resources/" + imageName).toExternalForm());
    bookImage.setFitWidth(100);
    bookImage.setFitHeight(100);
    bookImage.setPreserveRatio(true);

    Label bookTitle = new Label(publication.getTitle());
    bookTitle.setWrapText(true);

    bookCard.getChildren().addAll(statusBar, bookImage, bookTitle);
    //center bookCard vertically
    bookCard.setAlignment(Pos.TOP_CENTER);

    ContextMenu bookMenu = new ContextMenu();

    MenuItem deleteItem = new MenuItem("Ištrinti");
    MenuItem copyItem = new MenuItem("Kopijuoti");
    MenuItem loanItem = new MenuItem("Paskolinti");
    MenuItem returnItem = new MenuItem("Grąžinti");
    MenuItem loanHistory = new MenuItem("Skaitytojų istorija");

    bookMenu.getItems().addAll(deleteItem, copyItem, loanItem, returnItem, loanHistory);

    bookCard.setOnMouseClicked(event -> {
      setStatus("Paspaudėte ant leidinio„" + publication.getTitle() + "“");
      if (event.getButton() == MouseButton.PRIMARY) {
        showBookDetails(publicationIndex);
        bookMenu.hide();
      } else if (event.getButton() == MouseButton.SECONDARY) {
        bookMenu.show(bookCard, event.getScreenX(), event.getScreenY());
      }
    });

    deleteItem.setOnAction(e -> {
      setStatus("Ištrintas leidinys „" + publication.getTitle() + "“");
      shelfList.remove(publicationIndex);
      renderBooks();
    });

    copyItem.setOnAction(e -> {
      try {
        Publication clonedPub = null;

        if (publication instanceof Book book) {
          clonedPub = (Book) book.clone();
        }

        if (clonedPub == null) {
          throw new CloneNotSupportedException("This publication type not supported for cloning.");
        }

        shelfList.add(clonedPub);
        setStatus("Leidinys „" + publication.getTitle() + "“ nukopijuotas");
        renderBooks();
      } catch (CloneNotSupportedException ex) {
        setStatus("Klaida kopijuojant leidinį: " + ex.getMessage());
      }
    });

    loanItem.setOnAction(e -> {
      setStatus("Atidaryti paskolinti leidinį „" + publication.getTitle() + "“");
      showBorrowModal(publicationIndex);
      openModal("borrowModal" + publicationIndex);
    });

    loanHistory.setOnAction(e -> {
      setStatus("Atidaryti skolinimo istoriją leidiniui „" + publication.getTitle() + "“");
      showBorrowHistoryModal(publicationIndex);
      openModal("borrowHistoryModal" + publicationIndex);
    });

    returnItem.setOnAction(e -> {
      setStatus("Grąžinti leidinį „" + publication.getTitle() + "“");
      publication.returnBook();
      renderBooks();
      setStatus("Leidinys „" + publication.getTitle() + "“ grąžintas");
    });

    if (!publication.canBeBorrowed()) {
      loanItem.setVisible(false);
      loanHistory.setVisible(false);
      returnItem.setVisible(false);
    } else {
      if (publication.isAvailable()) {
        returnItem.setVisible(false);
      } else {
        loanItem.setVisible(false);
      }
    }

    return bookCard;
  }

  private void renderBooks() {
    bookFlow.getChildren().clear();

    for (int i = 0; i < shelfList.size(); i++) {
      VBox bookCard = createBookCard(i);
      bookFlow.getChildren().add(bookCard);
    }
  }
    
  private void createModalForm() {
    VBox modalForm = new VBox(15);

    modalForm.setPadding(new Insets(20));
    modalForm.setMaxWidth(400);
    modalForm.setMaxHeight(300);
    modalForm.setMinHeight(420);
    modalForm.setMinWidth(420);
    modalForm.getStyleClass().add("modal-preview");

    GridPane formGrid = new GridPane();
    formGrid.setHgap(10);
    formGrid.setVgap(15);

    Label typeLabel = new Label("Leidinio tipas:");
    ComboBox<String> typeCombo = new ComboBox<>();
    typeCombo.getItems().addAll("Knyga", "Žurnalas");
    typeCombo.setValue("Knyga");
    typeCombo.getStyleClass().add("text-field");

    Label titleLabel = new Label("Leidinio pavadinimas:");
    TextField titleField = new TextField();
    titleField.getStyleClass().add("text-field");

    Label publisherLabel = new Label("Leidykla:");
    TextField publisherField = new TextField();
    publisherField.getStyleClass().add("text-field");

    Label authorLabel = new Label("Autorius / redaktorius:");
    TextField authorField = new TextField();
    authorField.getStyleClass().add("text-field");

    Label yearLabel = new Label("Metai:");
    TextField yearField = new TextField();
    yearField.getStyleClass().add("text-field");

    Label pagesLabel = new Label("Puslapių skaičius:");
    TextField pagesField = new TextField();
    pagesField.getStyleClass().add("text-field");

    //Error
    Label errorLabel = new Label();
    errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 11px;");
    errorLabel.setVisible(false);
    errorLabel.setManaged(false);

    formGrid.add(typeLabel, 0, 0);
    formGrid.add(typeCombo, 1, 0);

    formGrid.add(titleLabel, 0, 1);
    formGrid.add(titleField, 1, 1);

    formGrid.add(publisherLabel, 0, 2);
    formGrid.add(publisherField, 1, 2);

    formGrid.add(authorLabel, 0, 3);
    formGrid.add(authorField, 1, 3);

    formGrid.add(yearLabel, 0, 4);
    formGrid.add(yearField, 1, 4);

    formGrid.add(pagesLabel, 0, 5);
    formGrid.add(pagesField, 1, 5);

    formGrid.add(errorLabel, 0, 6);
    GridPane.setColumnSpan(errorLabel, 2);

    pagesField.textProperty().addListener((obs, oldVal, newVal) -> {
      if (!newVal.matches("\\d*")) {
        pagesField.setText(newVal.replaceAll("[^\\d]", ""));
      }
    });

    yearField.textProperty().addListener((obs, oldVal, newVal) -> {
      if (!newVal.matches("\\d*")) {
        yearField.setText(newVal.replaceAll("[^\\d]", ""));
      }
    });

    Button saveBtn = new Button("Išsaugoti");
    Button cancelBtn = new Button("Atšaukti");
    saveBtn.getStyleClass().add("action-button");
    cancelBtn.getStyleClass().add("action-button");

    HBox buttons = new HBox(10, saveBtn, cancelBtn);
    buttons.setAlignment(Pos.CENTER_RIGHT);

    Label modalTitle = new Label("Pridėti naują knygą");
    modalTitle.getStyleClass().add("modal-title");

    modalForm.getChildren().addAll(modalTitle, formGrid, buttons);
    StackPane.setAlignment(modalForm, Pos.CENTER);

    saveBtn.setOnAction(e -> {
      String title = titleField.getText().trim();
      String publisher = publisherField.getText().trim();
      String author = authorField.getText().trim();
      String year = yearField.getText().trim();
      String pages = pagesField.getText().trim();
      String type = typeCombo.getValue();

      errorLabel.setVisible(false);
      errorLabel.setManaged(false);

      if (title.isEmpty() || publisher.isEmpty() || author.isEmpty() || year.isEmpty() || pages.isEmpty()) {
        errorLabel.setText("Visi laukai privalomi");
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
        return;
      }

      int pagesCount, yearPublished;
      try {
        yearPublished = Integer.parseInt(year);
      } catch (NumberFormatException ex) {
        errorLabel.setText("Metai turi būti skaičius");
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
        return;
      }

      try {
        pagesCount = Integer.parseInt(pages);
      } catch (NumberFormatException ex) {
        errorLabel.setText("Puslapių skaičius turi būti skaičius");
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
        return;
      }

      if (type == "Knyga") {
        shelfList.add(new Book(title, publisher, pagesCount, author, yearPublished));
      } else if (type == "Žurnalas") {
        shelfList.add(new Journal(title, publisher, pagesCount, author, yearPublished));
      }

      renderBooks();
      setStatus("Pridėta knyga „" + title + "“");
      closeModal("newPublication");
    });

    cancelBtn.setOnAction(e -> {
      closeModal("newPublication");
    });

    registerModal("newPublication", modalForm);
  }

  private VBox bookDetailsBox;
  private void createBookInfoModal() {
    VBox modalPreview = new VBox(15);
    modalPreview.setPadding(new Insets(20));
    modalPreview.setMaxWidth(400);
    modalPreview.setMaxHeight(300);
    modalPreview.setMinHeight(300);
    modalPreview.setMinWidth(200);
    modalPreview.getStyleClass().add("modal-preview");

    bookDetailsBox = new VBox(10);

    Button closeBtn = new Button("Uždaryti");
    closeBtn.getStyleClass().add("action-button");
    closeBtn.setOnAction(e -> {
      closeModal("previewBookInfo");
    });

    modalPreview.getChildren().addAll(bookDetailsBox, closeBtn);
    registerModal("previewBookInfo", modalPreview);
  }

  private void showBookDetails(int bookIndex) {
    bookDetailsBox.getChildren().clear();

    Publication publication = shelfList.get(bookIndex);

    Label title = new Label(publication.getTitle());
    title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
    bookDetailsBox.getChildren().add(title);

    if (publication instanceof Book book) {
      Label type = new Label("Knyga");
      type.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
      Label author = new Label("Autorius: " + book.getAuthor());
      Label pages = new Label("Puslapių skaičius: " + book.getPages());
      Label year = new Label("Išleidimo metai: " + book.getYearPublished());
      bookDetailsBox.getChildren().addAll(type, author, pages, year);
    } else if (publication instanceof Journal journal) {
      Label type = new Label("Žurnalas");
      type.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
      Label editor = new Label("Redaktorius: " + journal.getEditor());
      Label pages = new Label("Puslapių skaičius: " + journal.getPages());
      Label year = new Label("Išleidimo metai: " + journal.getYearPublished());
      bookDetailsBox.getChildren().addAll(type, editor, pages, year);
    }

    openModal("previewBookInfo");
  }

  public void showBorrowModal(int bookIndex) {
    VBox modalContent = new VBox(15);
    modalContent.setPadding(new Insets(20));
    modalContent.setMaxWidth(400);
    modalContent.setMaxHeight(300);
    modalContent.setMinHeight(300);
    modalContent.setMinWidth(200);
    modalContent.getStyleClass().add("modal-preview");

    Label title = new Label("Paskolinti knygą „%s“".formatted(shelfList.get(bookIndex).getTitle()));
    title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

    TextField nameField = new TextField();
    nameField.setPromptText("Skaitytojo vardas");
    Label nameError = new Label();
    nameError.setStyle("-fx-text-fill: red; -fx-font-size: 11px;");
    nameError.setVisible(false);
    nameError.setManaged(false);

    DatePicker returnDate = new DatePicker();
    returnDate.setPromptText("Grąžinimo data");
    Label dateError = new Label();
    dateError.setStyle("-fx-text-fill: red; -fx-font-size: 11px;");
    dateError.setVisible(false);
    dateError.setManaged(false);

    // Mygtukai
    HBox buttons = new HBox(10);
    buttons.setAlignment(Pos.CENTER_RIGHT);
    Button cancelBtn = new Button("Atšaukti");
    Button confirmBtn = new Button("Paskolinti");
    cancelBtn.getStyleClass().add("action-button");
    confirmBtn.getStyleClass().add("action-button");
    buttons.getChildren().addAll(cancelBtn, confirmBtn);

    modalContent.getChildren().addAll(title, nameField, nameError, returnDate, dateError, buttons);

    cancelBtn.setOnAction(e -> {
      closeModal("borrowModal" + bookIndex);
    });

    confirmBtn.setOnAction(e -> {
      boolean hasError = false;
      String borrower = nameField.getText().trim();
      LocalDate returnDue = returnDate.getValue();
      nameError.setVisible(false);
      dateError.setVisible(false);
      nameError.setManaged(false);
      dateError.setManaged(false);

      if (borrower.length() < 3) {
        nameError.setText("Įveskite bent 3 simbolius");
        nameError.setVisible(true);
        nameError.setManaged(true);
        hasError = true;
      }

      if (returnDue == null) {
        dateError.setText("Pasirinkite datą");
        dateError.setVisible(true);
        dateError.setManaged(true);
        hasError = true;
      }

      if (returnDue != null && returnDue.isBefore(LocalDate.now())) {
        dateError.setText("Pasirinkite datą ateityje");
        dateError.setVisible(true);
        hasError = true;
      }

      if (!hasError) {
        try {
          shelfList.get(bookIndex).borrowBook(borrower, returnDue.atStartOfDay());
        } catch (Exception ex) {
          setStatus("Klaida paskolinant knygą: " + ex.getMessage());
          return;
        }
        renderBooks();
        setStatus("Knyga „%s“ paskolinta. Grąžinti iki %s".formatted(borrower, returnDue.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        closeModal("borrowModal" + bookIndex);
      }
    });

    registerModal("borrowModal" + bookIndex, modalContent, true);
  }

  public void showBorrowHistoryModal(int bookIndex) {
    VBox modalContent = new VBox(15);
    modalContent.setPadding(new Insets(20));
    modalContent.setMaxWidth(500);
    modalContent.setMaxHeight(300);
    modalContent.setMinHeight(300);
    modalContent.setMinWidth(200);
    modalContent.getStyleClass().add("modal-preview");

    Label title = new Label("Leidinio „%s“ skolinimo istorija".formatted(shelfList.get(bookIndex).getTitle()));
    title.getStyleClass().add("modal-title");
    
    ArrayList<BorrowRecord> borrowHistory = shelfList.get(bookIndex).getBorrowHistory();
    if (borrowHistory == null) {
      Label noHistory = new Label("Nėra elementų");
      noHistory.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
      modalContent.getChildren().addAll(title, noHistory);
    } else {
      
      TableView<BorrowRecord> table = new TableView<>();

      TableColumn<BorrowRecord, String> nameCol = new TableColumn<>("Vardas");
      TableColumn<BorrowRecord, String> dateCol = new TableColumn<>("Paskolinta");
      TableColumn<BorrowRecord, String> dueCol = new TableColumn<>("Grąžinti iki");
      TableColumn<BorrowRecord, String> returnCol = new TableColumn<>("Grąžinta");

      table.setPlaceholder(new Label("Leidinys nebuvo paskolintas!"));

      nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getReader()));
      dateCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBorrowDate().toLocalDate().toString()));
      dueCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDueDate().toLocalDate().toString()));
      returnCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getReturnDate() != null ? data.getValue().getReturnDate().toLocalDate().toString() : "Nėra"));

      table.getColumns().addAll(List.of(nameCol, dateCol, dueCol, returnCol));
      table.setItems(FXCollections.observableArrayList(borrowHistory));

      //100% width
      nameCol.prefWidthProperty().bind(table.widthProperty().multiply(0.25));
      dateCol.prefWidthProperty().bind(table.widthProperty().multiply(0.25));
      dueCol.prefWidthProperty().bind(table.widthProperty().multiply(0.25));
      returnCol.prefWidthProperty().bind(table.widthProperty().multiply(0.25));
      nameCol.setResizable(false);

      ScrollPane scrollPane = new ScrollPane(table);
      scrollPane.setFitToWidth(true);
      scrollPane.getStyleClass().add("history-scroll");

      modalContent.getChildren().add(scrollPane);
    }

    Button closeBtn = new Button("Uždaryti");
    closeBtn.getStyleClass().add("action-button");
    closeBtn.setOnAction(e -> {
      closeModal("borrowHistoryModal" + bookIndex);
    });

    modalContent.getChildren().add(closeBtn);
    StackPane.setAlignment(modalContent, Pos.CENTER);
    registerModal("borrowHistoryModal" + bookIndex, modalContent, true);
  }

  private void registerModal(String name, VBox modal) { registerModal(name, modal, false); }
  private void registerModal(String name, VBox modal, boolean disposable) {
    root.getChildren().add(modal);
    StackPane.setAlignment(modal, Pos.CENTER);
    modal.setVisible(false);
    modals.put(name, modal);
    if (disposable) {
      disposableModals.add(name);
    }
  }

  private void openModal(String name) {
    VBox modal = modals.get(name);
    if (modal != null) {
      modal.setVisible(true);
      overlayVisibility(true);
      openedModals.add(name);
    }
  }

  private void closeModal(String name) {
    VBox modal = modals.get(name);
    if (modal != null) {
      modal.setVisible(false);
      overlayVisibility(false);
      openedModals.remove(name);
      if (disposableModals.contains(name)) {
        root.getChildren().remove(modal);
        modals.remove(name);
        disposableModals.remove(name);
      }
    }
  }

  private void closeAllModals() {
    for (String name : openedModals) {
      VBox modal = modals.get(name);
      if (modal != null) {
        modal.setVisible(false);
        if (disposableModals.contains(name)) {
          root.getChildren().remove(modal);
          modals.remove(name);
          disposableModals.remove(name);
        }
      }
    }
    overlayVisibility(false);
    openedModals.clear();
  }


  /**
   * Serializes the shelfList to a file.
   * @param filename
   */
  private void serializeData(String filename) {
    new Thread(() -> {
      try {
        FileOutputStream fileOut = new FileOutputStream(filename);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(shelfList);
        out.close();
        fileOut.close();
        Platform.runLater(() -> setStatus("Bibliotekos sąrašas išsaugotas"));
      } catch (IOException ex) {
        Platform.runLater(() -> setStatus("Klaida išsaugant bibliotekos sąrašą: " + ex.getMessage()));
      }
    }).start();
  }

  /**
   * Deserializes the shelfList from a file.
   * @param filename
   */
  @SuppressWarnings("unchecked")
  private void serializeLoadData(String filename) {
    try {
      FileInputStream fileIn = new FileInputStream(filename);
      ObjectInputStream in = new ObjectInputStream(fileIn);
      shelfList = (ArrayList<Publication>) in.readObject();

      in.close();
      fileIn.close();

      setStatus("Bibliotekos sąrašas įkeltas");
    } catch (IOException | ClassNotFoundException ex) {
      setStatus("Klaida įkeliant bibliotekos sąrašą: " + ex.getMessage());
    }
  }
}