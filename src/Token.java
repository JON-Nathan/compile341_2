

public class Token {
    static int counter = 0;
    int id;
    String symbolClass;
    String inputSnippet;
    Token next;

    public Token(String symbolClass, String inputSnippet) {
        this.symbolClass = symbolClass;
        this.inputSnippet = inputSnippet;
        next = null;
        id = counter++;
    }

    public String getSymbolClass() {
        return symbolClass;
    }

    public String getInputSnippet() {
        return inputSnippet;
    }

    public void add(Token token) {
        next = token;
    }
}
