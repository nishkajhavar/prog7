package assignment;

public class WordNode extends GeneralNode {
    private String word;

    public WordNode(String word) {
        super(word); // ADD THIS LINE
        this.word = word;
    }

    public String getWord() {
        return word;
    }
}