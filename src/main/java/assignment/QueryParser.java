package assignment;

public class QueryParser {

    private TokenCreater tokenizer;
    private String current;
    private boolean hasError;

    public QueryParser(String query) {
        this.tokenizer = new TokenCreater(query);
        this.hasError = false;
        getNextToken();
    }

    public void getNextToken() {
        if (!hasError) {
            this.current = tokenizer.getNextToken();
        }
    }

    private boolean checkString(String valid) {
        boolean isValid = ((current != null) && valid.equals(current));
        return isValid;
    }

    private boolean isWord() {
        if (current == null) return false;
        if (current.equals("(") || current.equals(")") || current.equals("&") || current.equals("|") ) {
            return false;
        }
        return true;
    }

    private String remove(String valid) {
        if (hasError) return null;
        if (current == null || !valid.equals(current)) {
            System.err.println("Parse error");
            hasError = true;
            return null;
        }
        String token = current;
        getNextToken();
        return token;
    }

    public GeneralNode parseQuery() {
        if (hasError) return null;
        if (checkString("!")) {
            remove("!");
            // The grammar mandates that '!' is followed by a word: Query -> ! word
            if (!isWord()) {
                System.err.println("Parse Error: Expected a word after '!', but found '" + current + "'");
                this.hasError = true;
                return null;
            }

            // Treat the word as a WordNode
            String word = current;
            getNextToken(); // Consume the word token

            // Construct the NotNode, wrapping the WordNode
            return new NegationNode(new WordNode(word));
        }
        else if (checkString("(")) {
            // Case: (Query Op Query)
            if (remove("(") == null) return null; // Consumes '('

            // Recursively build the left subtree
            GeneralNode left = parseQuery();
            if (hasError) return null; // Check for error after recursive call

            // Get the binary operator: AND or OR
            String operator = current;
            if (!operator.equals("&") && !operator.equals("|")) {
                System.err.println("Parse Error: Expected '&' or '|' operator, but found '" + operator + "'");
                this.hasError = true;
                return null;
            }
            if (remove(operator) == null) return null; // Consumes operator

            // Recursively build the right subtree
            GeneralNode right = parseQuery();
            if (hasError) return null; // Check for error after recursive call

            // Read the remaining Right Parens
            if (remove(")") == null) return null; // Consumes ')'

            // Make and return the binary node
            return operator.equals("&") ? new AndNode(left, right) : new OrNode(left, right);

        }
        // 2. Check for a Word
        else if (isWord()) {
            // Case: word
            String word = current; // Get the word string
            getNextToken(); // Consume the word token
            return new WordNode(word);

        }
        // 3. Error Case
        else {
            // A parse error has occurred (e.g., unexpected operator, or EOT where a node was expected)
            System.err.println("Parse Error");
            this.hasError = true;
            return null;
        }

    }

    public boolean hasError() {
        return hasError;
    }
}
