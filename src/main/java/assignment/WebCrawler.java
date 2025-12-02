package assignment;

import java.io.*;
import java.net.*;
import java.util.*;

import org.attoparser.simple.*;
import org.attoparser.config.ParseConfiguration;

// (WebIndex) index.load("index.db")

/**
 * The entry-point for WebCrawler; takes in a list of URLs to start crawling from and saves an index
 * to index.db.
 */
public class WebCrawler {

    /**
     * The WebCrawler's main method starts crawling a set of pages.  You can change this method as
     * you see fit, as long as it takes URLs as inputs and saves an Index at "index.db".
     */
    public static void main(String[] args) {
        // Basic usage information
        if (args.length == 0) {
            System.err.println("Error: No URLs specified.");
            return;
        }

        // 1. Initialize the Queue and the Global Visited Set
        Queue<URL> remaining = new LinkedList<>();
        Set<URL> visited = new HashSet<>();

        // Process starting URLs
        for (String urlString : args) {
            try {
                // Use URI/File conversion for robust URL creation (as previously fixed)
                URI uri = new URI(urlString);
                URL startURL;

                if ("file".equalsIgnoreCase(uri.getScheme())) {
                    File file = new File(uri);
                    startURL = file.toURI().toURL();
                } else {
                    startURL = uri.toURL();
                }

                // CRITICAL: Add to both the queue and the visited set
                remaining.add(startURL);
                visited.add(startURL);

            } catch (MalformedURLException | URISyntaxException e) {
                System.err.printf("Error: URL '%s' was malformed and will be ignored!%n", urlString);
            }
        }

        // Create a parser from the attoparser library, and our handler for markup.
        ISimpleMarkupParser parser = new SimpleMarkupParser(ParseConfiguration.htmlConfiguration());
        CrawlingMarkupHandler handler = new CrawlingMarkupHandler();
        int filesParsed = 0;

        // Try to start crawling, adding new URLS as we see them.
        try {
            URL currentURL; // Declared outside the loop

            // Loop continues as long as there are URLs in the queue.
            while ((currentURL = remaining.poll()) != null) {

                // Tell the handler which page is being processed
                handler.setCurrentPageURL(currentURL);

                // Parse the current URL's page
                parser.parse(new InputStreamReader(currentURL.openStream()), handler);

                // Process new URLs found by the handler
                for (URL newURL : handler.newURLs()) {
                    // CRITICAL FIX: Only add the newURL to the queue if it has NOT been visited yet.
                    if (visited.add(newURL)) { // Set.add returns true if the element was added (was not present)
                        remaining.add(newURL); // Add to the queue for crawling
                    }
                }
            }

            // Save the index once crawling is complete
            handler.getIndex().save("index.db");
            System.out.println(handler.getIndexedPageCount());

        } catch (Exception e) {
            System.err.println("Error: Index generation failed!");
            e.printStackTrace();
            return;
        }
    }

}