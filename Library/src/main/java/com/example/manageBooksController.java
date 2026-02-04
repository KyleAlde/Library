package com.example;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import com.example.utility.dao.BookDAO;
import com.example.model.Book;
import com.example.model.Book.BookStatus;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.control.Alert.AlertType;

public class manageBooksController {

    @FXML private TableView<Book> BookList;
    
    // Table columns matching FXML
    @FXML private TableColumn<Book, String> isbn;
    @FXML private TableColumn<Book, String> title;
    @FXML private TableColumn<Book, String> author;
    @FXML private TableColumn<Book, String> publisher;
    @FXML private TableColumn<Book, String> publicationDate;
    @FXML private TableColumn<Book, String> status;
    @FXML private TableColumn<Book, String> synopsis;
    @FXML private TableColumn<Book, String> coverID;
    @FXML private TableColumn<Book, String> bookGenres;
    
    // Buttons
    @FXML private Button addBook, clearFields, editBook, deleteBook;
    
    // Input fields
    @FXML private TextField insertIsbn, insertTitle, insertAuthor, insertPublisher, insertPublicationDate,
                           insertSynopsis, insertCoverID;
    @FXML private ComboBox<BookStatus> insertStatus;
    @FXML private ComboBox<String> insertBookGenres;
    
    private final BookDAO bookDAO = new BookDAO();
    private final ObservableList<Book> bookData = FXCollections.observableArrayList();
    private boolean isEditMode = false;
    private String currentEditIsbn = "";

    @FXML
    private void initialize() {
        setupTableColumns();
        setupSelectionListener();
        setupStatusComboBox();
        setupGenreComboBox();
        loadBooks();
    }

    private void setupTableColumns() {
        // Bind columns to Book properties
        isbn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIsbn()));
        title.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        author.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAuthor()));
        publisher.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPublisher()));
        
        // Publication date as string
        publicationDate.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getPublicationDate().toString()));
        
        status.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getStatus().toString()));
        
        // Synopsis with text wrapping
        synopsis.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSynopsis()));
        synopsis.setCellFactory(column -> {
            javafx.scene.control.TableCell<Book, String> cell = new javafx.scene.control.TableCell<>() {
                private final Text text = new Text();
                
                {
                    text.wrappingWidthProperty().bind(column.widthProperty().subtract(10));
                }
                
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                    } else {
                        text.setText(item);
                        setGraphic(text);
                    }
                }
            };
            return cell;
        });
        
        // Cover image path
        coverID.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getCoverImagePath()));
        
        // Genres with text wrapping
        bookGenres.setCellValueFactory(cellData -> 
            new SimpleStringProperty(String.join(", ", cellData.getValue().getGenres())));
        bookGenres.setCellFactory(column -> {
            javafx.scene.control.TableCell<Book, String> cell = new javafx.scene.control.TableCell<>() {
                private final Text text = new Text();
                
                {
                    text.wrappingWidthProperty().bind(column.widthProperty().subtract(10));
                }
                
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                    } else {
                        text.setText(item);
                        setGraphic(text);
                    }
                }
            };
            return cell;
        });
        
        BookList.setItems(bookData);
    }
    
    private void setupStatusComboBox() {
        insertStatus.getItems().addAll(BookStatus.values());
        insertStatus.setValue(BookStatus.AVAILABLE); // Set default value
    }

    private void setupGenreComboBox() {
        try {
            List<String> genres = bookDAO.getAllGenres();
            insertBookGenres.getItems().addAll(genres);
        } catch (SQLException e) {
            System.err.println("Failed to load genres: " + e.getMessage());
            // Add some default genres if database fails
            insertBookGenres.getItems().addAll("Fiction", "Non-Fiction", "Science Fiction", "Fantasy", "Mystery", "Romance");
        }
    }

    private void setupSelectionListener() {
        BookList.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, selectedBook) -> {
                if (selectedBook != null) {
                    populateFields(selectedBook);
                }
            }
        );
    }

    private void loadBooks() {
        try {
            List<Book> books = bookDAO.getAllBooks();
            bookData.clear();
            bookData.addAll(books);
            System.out.println("Loaded " + books.size() + " books");
        } catch (SQLException e) {
            showAlert("Error", "Failed to load books: " + e.getMessage(), AlertType.ERROR);
        }
    }

    @FXML
    private void onActionClearFields() {
        clearFields();
        showAlert("Info", "All fields have been cleared.", AlertType.INFORMATION);
    }

    @FXML
    private void onActionAddBook() {
        // Check if a book is currently selected (not in edit mode)
        if (!isEditMode && BookList.getSelectionModel().getSelectedItem() != null) {
            showAlert("Invalid Operation", "Please clear fields or use Edit button to modify selected book.", AlertType.WARNING);
            return;
        }
        
        try {
            // Validate required fields
            if (!validateRequiredFields()) return;
            
            // Parse publication date
            LocalDate pubDate = parsePublicationDate();
            if (pubDate == null) return;
            
            // Parse status
            BookStatus bookStatus = parseBookStatus();
            if (bookStatus == null) return;
            
            if (isEditMode) {
                // Update existing book
                bookDAO.updateBook(insertIsbn.getText(), insertTitle.getText(), 
                                 insertSynopsis.getText(), insertAuthor.getText(),
                                 insertPublisher.getText(), pubDate);
                bookDAO.updateStatus(insertIsbn.getText(), bookStatus.toString());
                
                // Invalidate cache to refresh book data across the application
                                
                showAlert("Success", "Book updated successfully!", AlertType.INFORMATION);
                isEditMode = false;
                currentEditIsbn = "";
                
            } else {
                // Add new book
                bookDAO.addBook(insertIsbn.getText(), insertTitle.getText(), 
                              insertSynopsis.getText(), insertAuthor.getText(),
                              insertPublisher.getText(), pubDate);
                bookDAO.updateStatus(insertIsbn.getText(), bookStatus.toString());
                
                // Invalidate cache to refresh book data across the application
                                
                showAlert("Success", "Book added successfully!", AlertType.INFORMATION);
            }
            
            clearFields();
            loadBooks();
            
        } catch (SQLException e) {
            showAlert("Error", "Database error: " + e.getMessage(), AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void onActionEditBook() {
        Book selectedBook = BookList.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert("Warning", "Please select a book to edit", AlertType.WARNING);
            return;
        }
        
        // Enter edit mode
        isEditMode = true;
        currentEditIsbn = selectedBook.getIsbn();
        
        // Populate fields
        populateFields(selectedBook);
        
        // Update button text
        addBook.setText("Update Book");
        
        showAlert("Edit Mode", "Editing book: " + selectedBook.getTitle() + 
                  ". Modify fields and click 'Update Book'.", AlertType.INFORMATION);
    }

    @FXML
    private void onActionDeleteBook() {
        Book selectedBook = BookList.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert("Warning", "Please select a book to delete", AlertType.WARNING);
            return;
        }
        
        Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete Book");
        confirmAlert.setContentText("Are you sure you want to delete '" + 
                                   selectedBook.getTitle() + "'?");
        
        if (confirmAlert.showAndWait().orElse(javafx.scene.control.ButtonType.CANCEL) == javafx.scene.control.ButtonType.OK) {
            try {
                bookDAO.deleteBook(selectedBook.getIsbn());
                
                // Invalidate cache to refresh book data across the application
                                
                showAlert("Success", "Book deleted successfully!", AlertType.INFORMATION);
                loadBooks();
                clearFields();
                
                // Exit edit mode if deleting the book being edited
                if (isEditMode && selectedBook.getIsbn().equals(currentEditIsbn)) {
                    isEditMode = false;
                    currentEditIsbn = "";
                    addBook.setText("Add Book");
                }
                
            } catch (SQLException e) {
                showAlert("Error", "Failed to delete book: " + e.getMessage(), AlertType.ERROR);
            }
        }
    }

    private void populateFields(Book book) {
        insertIsbn.setText(book.getIsbn());
        insertTitle.setText(book.getTitle());
        insertAuthor.setText(book.getAuthor());
        insertPublisher.setText(book.getPublisher());
        insertPublicationDate.setText(book.getPublicationDate().toString());
        insertStatus.setValue(book.getStatus());
        insertSynopsis.setText(book.getSynopsis());
        insertCoverID.setText(book.getCoverImagePath());
        insertBookGenres.setValue(book.getGenres().isEmpty() ? "" : book.getGenres().iterator().next());
    }

    private boolean validateRequiredFields() {
        if (insertIsbn.getText().trim().isEmpty() || 
            insertTitle.getText().trim().isEmpty() || 
            insertAuthor.getText().trim().isEmpty()) {
            showAlert("Validation Error", 
                     "ISBN, Title, and Author are required fields.", 
                     AlertType.WARNING);
            return false;
        }
        return true;
    }
    
    private LocalDate parsePublicationDate() {
        String dateStr = insertPublicationDate.getText().trim();
        if (dateStr.isEmpty()) {
            showAlert("Validation Error", "Publication date is required.", AlertType.WARNING);
            return null;
        }
        
        try {
            return LocalDate.parse(dateStr);
        } catch (Exception e) {
            showAlert("Validation Error", 
                     "Invalid date format. Use YYYY-MM-DD (e.g., 2024-01-15)", 
                     AlertType.WARNING);
            return null;
        }
    }
    
    private BookStatus parseBookStatus() {
        BookStatus selectedStatus = insertStatus.getValue();
        if (selectedStatus == null) {
            // Default to AVAILABLE if not specified
            return BookStatus.AVAILABLE;
        }
        return selectedStatus;
    }

    private void clearFields() {
        insertIsbn.clear();
        insertTitle.clear();
        insertAuthor.clear();
        insertPublisher.clear();
        insertPublicationDate.clear();
        insertStatus.setValue(BookStatus.AVAILABLE);
        insertSynopsis.clear();
        insertCoverID.clear();
        insertBookGenres.setValue(null);
        
        // Reset button if in edit mode
        if (isEditMode) {
            isEditMode = false;
            currentEditIsbn = "";
            addBook.setText("Add Book");
        }
    }

    private void showAlert(String title, String message, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}