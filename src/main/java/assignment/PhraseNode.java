package assignment;

import org.w3c.dom.Node;

import java.util.List;

public class PhraseNode extends GeneralNode {
    private final List<String> phraseWords;

    public PhraseNode(List<String> words) {
        super(words.isEmpty() ? "" : words.get(0));
        this.phraseWords = words;
    }

    public List<String> getWords() {
        return phraseWords;
    }
}
