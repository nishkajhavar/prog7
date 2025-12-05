package assignment;

import java.util.*;
import java.net.*;
import org.attoparser.simple.*;

/**
 * A markup handler which is called by the Attoparser markup parser as it parses the input;
 * responsible for building the actual web index.
 */
public class CrawlingMarkupHandler extends AbstractSimpleMarkupHandler {

    private WebIndex webIndex;
    private List<URL> pageNewURLS;
    private Page page;
    private int indexedPageCount = 0;

    public CrawlingMarkupHandler() {
        this.webIndex = new WebIndex();
        this.pageNewURLS = new LinkedList<>();
    }

    public int getIndexedPageCount() {
        return indexedPageCount;
    }

    /**
     * This method returns the complete index that has been crawled thus far when called.
     */
    public Index getIndex() {
        return webIndex;
    }

    public void setCurrentPageURL(URL url) {
        // initialize page object with URL
        this.page = new Page(url);
        // clear the new URL list for current page before parsing starts
        this.pageNewURLS.clear();
    }

    /**
     * This method returns any new URLs found to the Crawler; upon being called, the set of new URLs
     * should be cleared.
     */
    public List<URL> newURLs() {
        // Correct implementation: Return the list and clear the handler's reference.
        List<URL> newURLs = this.pageNewURLS;
        this.pageNewURLS = new LinkedList<>(); // Reset the field to an empty list
        return newURLs;
    }

    /**
     * Called when the parser first starts reading a document.
     */
    public void handleDocumentStart(long startTimeNanos, int line, int col) {
        // Document parsing started
    }

    /**
     * Called when the parser finishes reading a document.
     */
    public void handleDocumentEnd(long endTimeNanos, long totalTimeNanos, int line, int col) {
        this.indexedPageCount++;
    }

    /**
     * Called at the start of any tag.
     */
    public void handleOpenElement(String elementName, Map<String, String> attributes, int line, int col) {
        if (elementName.toLowerCase().equals("a")) {
            if (attributes != null) {
                String href = attributes.get("href");
                if (href != null) {
                    try {
                        URL newURL = new URL(this.page.getURL(), href);
                        String path = newURL.getPath().toLowerCase();

                        // Only proceed if the URL path ends with an HTML extension or a directory slash.
                        if (path.endsWith(".html") || path.endsWith(".htm") || path.endsWith("/")) {
                            this.pageNewURLS.add(newURL);
                        }
                    } catch (MalformedURLException e) {
                        // Ignore malformed URLs
                    }
                }
            }
        }
    }

    /**
     * Called at the end of any tag.
     */
    public void handleCloseElement(String elementName, int line, int col) {
        // Element closed
    }

    /**
     * Called whenever characters are found inside a tag.
     */
    public void handleText(char[] ch, int startIndex, int length, int lineIndex, int colIndex) {
        int currentCharIndex = startIndex;
        int endIndex = startIndex + length;

        while (currentCharIndex < endIndex) {
            // Skip non-alphanumeric characters and ADVANCE the index.
            while (currentCharIndex < endIndex && !Character.isLetterOrDigit(ch[currentCharIndex])) {
                currentCharIndex++;
            }

            // If we reached the end of the buffer, break.
            if (currentCharIndex >= endIndex) {
                break;
            }

            // We found the start of a word. Record the start index.
            int wordStart = currentCharIndex;

            // Find the end of the word (sequence of alphanumeric characters) and ADVANCE the index.
            while (currentCharIndex < endIndex && Character.isLetterOrDigit(ch[currentCharIndex])) {
                currentCharIndex++;
            }

            // Extract and process the word.
            int wordLength = currentCharIndex - wordStart;
            if (wordLength > 0) {
                String currentWord = new String(ch, wordStart, wordLength);
                this.webIndex.addWord(currentWord, this.page);
            }
        }
    }

    // Set URL before parsing starts
    public void setThisPageURL(URL url) {
        this.page = new Page(url);
    }
}