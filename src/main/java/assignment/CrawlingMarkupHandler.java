package assignment;

import java.util.*;
import java.net.*;
import org.attoparser.simple.*;

/**
 * A markup handler which is called by the Attoparser markup parser as it parses the input;
 * responsible for building the actual web index.
 *
 * TODO: Implement this!
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
        // TODO: Implement this!
        return webIndex;
    }

    public void setCurrentPageURL(URL url) {
        // Initializes the 'page' object with the URL being crawled
        this.page = new Page(url);
        // CRITICAL: Clear the new URL list for the current page before parsing starts
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
    * These are some of the methods from AbstractSimpleMarkupHandler.
    * All of its method implementations are NoOps, so we've added some things
    * to do; please remove all the extra printing before you turn in your code.
    *
    * Note: each of these methods defines a line and col param, but you probably
    * don't need those values. You can look at the documentation for the
    * superclass to see all of the handler methods.
    */

    /**
    * Called when the parser first starts reading a document.
    * @param startTimeNanos  the current time (in nanoseconds) when parsing starts
    * @param line            the line of the document where parsing starts
    * @param col             the column of the document where parsing starts
    */
    public void handleDocumentStart(long startTimeNanos, int line, int col) {
        // TODO: Implement this.
        //System.out.println("Start of document is " + this.page.getURL());
    }

    /**
    * Called when the parser finishes reading a document.
    * @param endTimeNanos    the current time (in nanoseconds) when parsing ends
    * @param totalTimeNanos  the difference between current times at the start
    *                        and end of parsing
    * @param line            the line of the document where parsing ends
    * @param col             the column of the document where the parsing ends
    */
    public void handleDocumentEnd(long endTimeNanos, long totalTimeNanos, int line, int col) {
        // TODO: Implement this.
        System.out.println("End of document");
        this.indexedPageCount++;
    }

    /**
    * Called at the start of any tag.
    * @param elementName the element name (such as "div")
    * @param attributes  the element attributes map, or null if it has no attributes
    * @param line        the line in the document where this element appears
    * @param col         the column in the document where this element appears
    */
    public void handleOpenElement(String elementName, Map<String, String> attributes, int line, int col) {
        // TODO: Implement this.
        if (elementName.toLowerCase().equals("a")) {
            if (attributes != null) {
                String href = attributes.get("href");
                if (href != null) {
                    try {
                        URL newURL = new URL(this.page.getURL(), href);
                        String path = newURL.getPath().toLowerCase();

                        // 🔥 CRITICAL: Only proceed if the URL path ends with an HTML extension or a directory slash.
                        if (path.endsWith(".html") || path.endsWith(".htm") || path.endsWith("/")) {
                            this.pageNewURLS.add(newURL);
                        } else {
                            // Optionally print a message to show what was ignored
                            System.out.println("Ignoring non-HTML resource: " + newURL);
                        }
                    }
                    catch (MalformedURLException e) {
                        System.err.println(href + " on page " +this.page.getURL() + " is not a valid URL");
                    }
                }
            }
        }
    }

    /**
    * Called at the end of any tag.
    * @param elementName the element name (such as "div").
    * @param line        the line in the document where this element appears.
    * @param col         the column in the document where this element appears.
    */
    public void handleCloseElement(String elementName, int line, int col) {
        // TODO: Implement this.
        System.out.println("End element:   " + elementName);
    }

    /**
    * Called whenever characters are found inside a tag. Note that the parser is not
    * required to return all characters in the tag in a single chunk. Whitespace is
    * also returned as characters.
    * @param ch      buffer containing characters; do not modify this buffer
    * @param start   location of 1st character in ch
    * @param length  number of characters in ch
    */
// assignment.CrawlingMarkupHandler.java

    public void handleText(char[] ch, int startIndex, int length, int lineIndex, int colIndex) {

        int currentCharIndex = startIndex;
        int endIndex = startIndex + length;

        while (currentCharIndex < endIndex) {

            // 1. Skip non-alphanumeric characters and ADVANCE the index.
            while (currentCharIndex < endIndex && !Character.isLetterOrDigit(ch[currentCharIndex])) {
                currentCharIndex++;
            }

            // If we reached the end of the buffer, break.
            if (currentCharIndex >= endIndex) {
                break;
            }

            // 2. We found the start of a word. Record the start index.
            int wordStart = currentCharIndex;

            // 3. Find the end of the word (sequence of alphanumeric characters) and ADVANCE the index.
            while (currentCharIndex < endIndex && Character.isLetterOrDigit(ch[currentCharIndex])) {
                currentCharIndex++;
            }

            // 4. Extract and process the word.
            int wordLength = currentCharIndex - wordStart;
            if (wordLength > 0) {
                String currentWord = new String(ch, wordStart, wordLength);
                this.webIndex.addWord(currentWord, this.page);
            }

            // The outer loop automatically continues, starting the next search at the new currentCharIndex.
        }
    }

    // set URL before parsing starts
    public void setThisPageURL(URL url) {
        this.page = new Page(url);
    }


}
