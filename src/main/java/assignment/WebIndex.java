package assignment;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * A web-index which efficiently stores information about pages. Serialization is done automatically
 * via the superclass "Index" and Java's Serializable interface.
 *
 * TODO: Implement this!
 */
public class WebIndex extends Index {
    /**
     * Needed for Serialization (provided by Index) - don't remove this!
     */
    private static final long serialVersionUID = 1L;
    private HashMap<String, Set<Page>> invertedIndex;

    public WebIndex() {
        this.invertedIndex = new HashMap<>();
    }

    public void addWord(String anyCaseWord, Page page) {
        String word = anyCaseWord.toLowerCase();
        if  (invertedIndex.containsKey(word)) {
            invertedIndex.get(word).add(page);
        }
        else {
            Set<Page> set = new HashSet<>();
            set.add(page);
            invertedIndex.put(word,set);
        }
    }

    // UPDATE LATER
    public HashMap<String, Set<Page>> getWebIndex() {
        return invertedIndex;
    }

    // TODO: Implement all of this! You may choose your own data structures an internal APIs.
    // You should not need to worry about serialization (just make any other data structures you use
    // here also serializable - the Java standard library data structures already are, for example).

    public Set<Page> getPageSet(String word) {
        String normalizedWord = word.toLowerCase();
        if  (invertedIndex.containsKey(normalizedWord)) {
            return invertedIndex.get(normalizedWord);
        }
        else {
            return new HashSet<>();
        }
    }

}
