package com.example.utility;

import java.util.HashMap;
import java.util.Map;

public class CoverImageMatcher {
    
    private static final String COVERS_FOLDER_PATH = "/com/example/fxml/Images/Covers/";
    private static Map<String, String> titleToImageMap;
    
    static {
        initializeTitleToImageMap();
    }
    
    private static void initializeTitleToImageMap() {
        titleToImageMap = new HashMap<>();
        
        // Map book titles to their cover image files
        titleToImageMap.put("1984", "1984.jpg");
        titleToImageMap.put("Brave New World", "brave new world.jpg");
        titleToImageMap.put("A Brief History of Time", "brief history of time.jpg");
        titleToImageMap.put("Harry Potter and the Sorcerer's Stone", "harry potter and the sorcerer's stone.jpg");
        titleToImageMap.put("Pride and Prejudice", "pride and prejudice.jpg");
        titleToImageMap.put("The Da Vinci Code", "the da vinci code.jpg");
        titleToImageMap.put("The Great Gatsby", "the great gatsby.jpg");
        titleToImageMap.put("The Hobbit", "the hobbit.jfif");
        titleToImageMap.put("The Road", "the road.jpg");
        titleToImageMap.put("To Kill a Mockingbird", "to kill a mockingbird.jpg");
    }
    
    /**
     * Matches a book title to its cover image path
     * @param bookTitle The title of the book
     * @return The relative path to the cover image, or null if no match found
     */
    public static String getCoverImagePath(String bookTitle) {
        if (bookTitle == null || bookTitle.trim().isEmpty()) {
            return null;
        }
        
        // Try exact match first
        String imagePath = titleToImageMap.get(bookTitle.trim());
        if (imagePath != null) {
            return COVERS_FOLDER_PATH + imagePath;
        }
        
        // Try case-insensitive match
        for (Map.Entry<String, String> entry : titleToImageMap.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(bookTitle.trim())) {
                return COVERS_FOLDER_PATH + entry.getValue();
            }
        }
        
        // Try partial match (contains)
        for (Map.Entry<String, String> entry : titleToImageMap.entrySet()) {
            if (entry.getKey().toLowerCase().contains(bookTitle.trim().toLowerCase()) ||
                bookTitle.trim().toLowerCase().contains(entry.getKey().toLowerCase())) {
                return COVERS_FOLDER_PATH + entry.getValue();
            }
        }
        
        return null; // No match found
    }
    
    /**
     * Updates a book's cover image path based on its title
     * @param bookTitle The title of the book
     * @return The cover image path or null if no match
     */
    public static String updateBookCoverPath(String bookTitle) {
        return getCoverImagePath(bookTitle);
    }
}
