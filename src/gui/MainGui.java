package gui;

import java.io.*;
import java.util.ArrayList;

import javax.swing.*;
import java.awt.event.*;

import abstracts.*;
import factories.*;
import interfaces.*;
import publications.*;

public class MainGui extends JFrame implements Runnable {
  
  private ArrayList<Publication> shelfList = new ArrayList<Publication>();
  private JTable tableObj = null;
  private boolean changesMade = false;

  @Override
  public void run() {
    setTitle("Library MS");
    setSize(800, 600);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
    setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

    //Factory Method pattern
    PublicationFactory bookFactory = new BookFactory("J.K. Rowling");
    PublicationFactory journalFactory = new JournalFactory("Obuolys komanda");

    JPanel buttonsPanel = new JPanel();

    //NEW PUBLICATION BUTTON
    JButton addButton = new JButton("Add Publication");
    addButton.addActionListener(e -> {
      //Open form to add new publication
      String[] options = {"Book", "Journal"};
      int typeIndex = JOptionPane.showOptionDialog(this, "Select publication type:", "Publication Type",
        JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
      if (typeIndex == -1)  return;
      String type = options[typeIndex];

      String title = JOptionPane.showInputDialog(this, "Enter publication title:");
      String publisher = JOptionPane.showInputDialog(this, "Enter publisher:");
      int pages = 0;
      int year = 0;

      try {
        pages = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter number of pages:"));
        year = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter year published:"));
      } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Invalid input. Please enter a number for pages and year.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }

      if (type.equalsIgnoreCase("Book")) {
        shelfList.add((Book) bookFactory.createPublication(title, publisher, pages, year));
      } else if (type.equalsIgnoreCase("Journal")) {
        shelfList.add((Journal) journalFactory.createPublication(title, publisher, pages, year));
      }

      this.changesMade = true;
      updateTable();
    });
    buttonsPanel.add(addButton);

    //SAVE DATA BUTTON
    JButton saveButton = new JButton("Save to File");
    saveButton.addActionListener(e -> {
      serializeData("shelfList.ser");
    });
    buttonsPanel.add(saveButton);

    //LOAD DATA BUTTON
    JButton loadButton = new JButton("Load saved");
    loadButton.addActionListener(e -> {
      //Deserialization code here
      if (this.changesMade) {
        int response = JOptionPane.showConfirmDialog(this, "You have unsaved changes. Do you want to continue?", "Unsaved Changes", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (response == JOptionPane.NO_OPTION) {
          return;
        }
      }

      serializeLoadData("shelfList.ser");
    });
    buttonsPanel.add(loadButton);


    //Display arraylist
    tableObj = new JTable();
    tableObj.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) { //for double-clicks
          int row = tableObj.rowAtPoint(e.getPoint());

          if (row >= 0 && row < shelfList.size()) {
            Publication pub = shelfList.get(row);

            //Open form to cancel, clone or delete publication
            String[] options = {"Cancel", "Clone", "Delete"};
            int actionIndex = JOptionPane.showOptionDialog(tableObj, "Select action:", "Publication Action",
              JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
            String action = options[actionIndex];

            if (action.equals("Cancel")) {
              return;
            } else if (action.equals("Clone")) {
              try {
                Publication clonedPub = null;

                if (pub instanceof Book book) {
                  clonedPub = (Book) book.clone();
                }

                if (clonedPub == null) {
                  throw new CloneNotSupportedException("This publication type not supported for cloning.");
                }

                shelfList.add(clonedPub);
                updateTable();
                changesMade = true;
              } catch (CloneNotSupportedException ex) {
                JOptionPane.showMessageDialog(tableObj, "Error cloning publication: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
              }
            } else if (action.equals("Delete")) {
              shelfList.remove(row);
              updateTable();
              changesMade = true;
            }
          }
        }
      }
    });
    JScrollPane scrollPane = new JScrollPane(tableObj);

    add(buttonsPanel);
    add(scrollPane);

    setVisible(true);

    updateTable();
  }

  private void updateTable() {
    //Update table with new data
    String[] columnNames = {"Type", "Title", "Author/Editor", "Pages", "Year"};
    Object[][] data = new Object[shelfList.size()][6];

    for (int i = 0; i < shelfList.size(); i++) {
      Publication publ = shelfList.get(i);
      if (publ instanceof Book book) {
        data[i][0] = "Book";
        data[i][1] = book.getTitle();
        data[i][2] = book.getAuthor();
        data[i][3] = book.getPages();
        data[i][4] = book.getYearPublished();
      } else if (publ instanceof Journal journal) {
        data[i][0] = "Journal";
        data[i][1] = journal.getTitle();
        data[i][2] = journal.getEditor();
        data[i][3] = journal.getPages();
        data[i][4] = journal.getYearPublished();
      } else {
        continue;
      }
    }

    //Set table model with new data
    tableObj.setModel(new javax.swing.table.DefaultTableModel(data, columnNames) {
      @Override
      public boolean isCellEditable(int row, int column) { return false; }
    });
  }

  private void serializeData(String filename) {
    new Thread(() -> {
      try {
        FileOutputStream fileOut = new FileOutputStream(filename);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(shelfList);
        out.close();
        fileOut.close();
        JOptionPane.showMessageDialog(this, "Shelf list saved", "Success", JOptionPane.INFORMATION_MESSAGE);
        this.changesMade = false;
      } catch (IOException ex) {
        JOptionPane.showMessageDialog(this, "Error saving shelf list: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      }
    }).start();
  }

  private void serializeLoadData(String filename) {
    try {
      FileInputStream fileIn = new FileInputStream(filename);
      ObjectInputStream in = new ObjectInputStream(fileIn);
      //shelfList = (ArrayList<Publication>) in.readObject();
      shelfList = (ArrayList<Publication>) in.readObject();

      in.close();
      fileIn.close();

      JOptionPane.showMessageDialog(this, "Shelf list loaded", "Success", JOptionPane.INFORMATION_MESSAGE);
      this.changesMade = false;
      updateTable();
    } catch (IOException | ClassNotFoundException ex) {
      JOptionPane.showMessageDialog(this, "Error loading shelf list: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }
    
}
