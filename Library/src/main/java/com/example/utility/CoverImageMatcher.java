package com.example.utility;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CoverImageMatcher {
    
    private static final String COVERS_FOLDER_PATH = "/com/example/fxml/Images/Covers/";
    private static final Map<String, String> titleToImageMap = new HashMap<>();
    private static final Map<String, String> normalizedTitleMap = new HashMap<>();
    private static final Map<String, String> matchCache = new ConcurrentHashMap<>();
    
    static {
        initializeTitleToImageMap();
        buildNormalizedMap();
    }
    
    private static void initializeTitleToImageMap() {
        titleToImageMap.put("1984", "1984.jpg");
        titleToImageMap.put("Brave New World", "brave new world.jpg");
        titleToImageMap.put("A Brief History of Time", "brief history of time.jpg");
        titleToImageMap.put("Harry Potter and the Sorcerer’s Stone", "harry potter and the sorcerer’s stone.jpg");
        titleToImageMap.put("Pride and Prejudice", "pride and prejudice.jpg");
        titleToImageMap.put("The Da Vinci Code", "the da vinci code.jpg");
        titleToImageMap.put("The Great Gatsby", "the great gatsby.jpg");
        titleToImageMap.put("The Hobbit", "the hobbit.jfif");
        titleToImageMap.put("The Road", "the road.jpg");
        titleToImageMap.put("To Kill a Mockingbird", "to kill a mockingbird.jpg");
        titleToImageMap.put("Animal Farm", "animal farm.jpg");
        titleToImageMap.put("One Hundred Years of Solitude", "one hundred years of solitude.jpg");
        titleToImageMap.put("Meditations", "meditations.jpg");
        titleToImageMap.put("The Catcher in the Rye", "the catcher in the rye.jpg");
        titleToImageMap.put("Dracula", "dracula.jpg");
        titleToImageMap.put("The Fellowship of the Ring", "the fellowship of the ring.jpg");
        titleToImageMap.put("Sapiens: A Brief History of Humankind", "sapiens a brief history of humankind.jpg");
        titleToImageMap.put("Thinking, Fast and Slow", "thinking, fast and slow.jpg");
        titleToImageMap.put("The Book Thief", "the book thief.jpg");
        titleToImageMap.put("The Girl with the Dragon Tattoo", "the girl with the dragon tattoo.jpg");
        titleToImageMap.put("Guns, Germs, and Steel", "guns, germs, and steel.jpg");
        titleToImageMap.put("Fahrenheit 451", "fahrenheit 451.jpg");
        titleToImageMap.put("Jane Eyre", "jane eyre.jpg");
        titleToImageMap.put("Cosmos", "cosmos.jpg");
        titleToImageMap.put("Of Mice and Men", "of mice and men.jpg");
        titleToImageMap.put("The Alchemist", "the alchemist.jpg");
        titleToImageMap.put("Slaughterhouse-Five", "slaughterhouse-five.jpg");
        titleToImageMap.put("The Republic", "the republic.jpg");
        titleToImageMap.put("Where the Wild Things Are", "where the wild things are.jpg");
        titleToImageMap.put("Astrophysics for People in a Hurry", "astrophysics for people in a hurry.jpg");
        titleToImageMap.put("Nigga si Martin", "martin.jpg");
    }
    
    private static void buildNormalizedMap() {
        // Pre-compute normalized keys for O(1) case-insensitive lookup
        for (Map.Entry<String, String> entry : titleToImageMap.entrySet()) {
            String normalizedKey = entry.getKey().toLowerCase().trim();
            normalizedTitleMap.put(normalizedKey, entry.getValue());
        }
    }
    
    /**
     * Matches a book title to its cover image path with optimized performance
     * @param bookTitle The title of the book
     * @return The relative path to the cover image, or null if no match found
     */
    public static String getCoverImagePath(String bookTitle) {
        if (bookTitle == null || bookTitle.trim().isEmpty()) {
            return null;
        }
        
        // Use cache for repeated lookups
        return matchCache.computeIfAbsent(bookTitle, CoverImageMatcher::findImagePath);
    }
    
    private static String findImagePath(String bookTitle) {
        String normalizedTitle = bookTitle.toLowerCase().trim();
        
        // O(1) exact match (case-insensitive)
        String imagePath = normalizedTitleMap.get(normalizedTitle);
        if (imagePath != null) {
            return COVERS_FOLDER_PATH + imagePath;
        }
        
        // Fallback to partial matching (only if needed)
        return findPartialMatch(normalizedTitle);
    }
    
    private static String findPartialMatch(String normalizedTitle) {
        // Optimized partial matching with early exit
        for (Map.Entry<String, String> entry : normalizedTitleMap.entrySet()) {
            String key = entry.getKey();
            if (key.contains(normalizedTitle) || normalizedTitle.contains(key)) {
                return COVERS_FOLDER_PATH + entry.getValue();
            }
        }
        return null;
    }
    
    /**
     * Clears the match cache (useful for testing or memory management)
     */
    public static void clearCache() {
        matchCache.clear();
    }
    
    /**
     * Gets cache statistics for monitoring
     */
    public static int getCacheSize() {
        return matchCache.size();
    }
}
