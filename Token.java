public class Token {

    public TokenType type;
    public String lexeme;
    public Object literal;
    public int line;

    public Token(TokenType type, String lexeme, Object literal, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    @Override
    public String toString() {
        return "[line: " + line + "] " + type + " " + lexeme + " " + literal;
    }
}