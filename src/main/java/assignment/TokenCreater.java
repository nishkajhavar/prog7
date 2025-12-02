package assignment;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;

public class TokenCreater {
    private final PushbackReader reader;

    public TokenCreater(String query) {
        this.reader = new PushbackReader(new StringReader(query));
    }

    public String getNextToken() {
        try {
            int c = -1;
            boolean isSpace = true;
            while (isSpace) {
                c = reader.read();
                if (c == -1) {
                    break;
                }
                isSpace = Character.isWhitespace((char) c);
                if (!isSpace) {
                    reader.unread(c);
                    break;
                }
            }

            if  (c == -1) {
                return null;
            }
            c = reader.read();
            char current = (char) c;
            if (current == '&' || current=='|' || current=='(' || current==')' || current=='!') {
                return String.valueOf(current);
            }
            else if (Character.isDigit(current) || Character.isLetter(current)) {
                StringBuilder sb = new StringBuilder();
                sb.append(current);

                while ((c = reader.read()) != -1) {
                    char next = (char) c;
                    if ((Character.isDigit(next)) || (Character.isLetter(next))) {
                        sb.append(next);
                    }
                    else {
                        reader.unread(c);
                        break;
                    }
                }
                return sb.toString();
            }
            System.err.println("Invalid character in stream.");
            return null;
        }
        catch (IOException e) {
            System.err.println("Error while reading input stream.");
            return null;
        }
    }
}
