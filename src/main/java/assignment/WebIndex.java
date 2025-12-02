package assignment;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A web-index which efficiently stores information about pages. Serialization is done automatically
 * via the superclass "Index" and Java's Serializable interface.
 */
public class WebIndex extends Index {
    /**
     * Needed for Serialization (provided by Index) - don't remove this!
     */
    private static final long serialVersionUID = 1L;

    // Maps words to the set of pages containing that word
    private HashMap<String, Set<Page>> positionalIndex;

    public WebIndex() {
        this.positionalIndex = new HashMap<>();
    }

    public void addWord(String anyCaseWord, Page page) {
        String word = anyCaseWord.toLowerCase();
        if (positionalIndex.containsKey(word)) {
            positionalIndex.get(word).add(page);
        } else {
            Set<Page> set = new HashSet<>();
            set.add(page);
            positionalIndex.put(word, set);
        }
    }

    public HashMap<String, Set<Page>> getWebIndex() {
        return positionalIndex;
    }

    public Set<Page> getPageSet(String word) {
        String normalizedWord = word.toLowerCase();
        if (positionalIndex.containsKey(normalizedWord)) {
            return positionalIndex.get(normalizedWord);
        } else {
            return new HashSet<>();
        }
    }

    public Set<Page> getAllPages() {
        Set<Page> all = new HashSet<>();
        for (Set<Page> pageSet : positionalIndex.values()) {
            all.addAll(pageSet);
        }
        return all;
    }
}