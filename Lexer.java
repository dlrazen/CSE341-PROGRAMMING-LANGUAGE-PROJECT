import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lexer {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();

    private int start = 0;
    private int current = 0;
    private int line = 1;

    private static final Map<String, TokenType> KEYWORDS = new HashMap<>();
    static {
        KEYWORDS.put("orbit",       TokenType.ORBIT);
        KEYWORDS.put("task",        TokenType.TASK);
        KEYWORDS.put("type",        TokenType.TYPE);
        KEYWORDS.put("altitude",    TokenType.ALTITUDE);
        KEYWORDS.put("inclination", TokenType.INCLINATION);
        KEYWORDS.put("if",          TokenType.IF);
        KEYWORDS.put("else",        TokenType.ELSE);
        KEYWORDS.put("when",        TokenType.WHEN);
        KEYWORDS.put("repeat",      TokenType.REPEAT);
        KEYWORDS.put("every",       TokenType.EVERY);
        KEYWORDS.put("point",       TokenType.POINT);
        KEYWORDS.put("to",          TokenType.TO);
        KEYWORDS.put("rotate",      TokenType.ROTATE);
        KEYWORDS.put("capture",     TokenType.CAPTURE);
        KEYWORDS.put("transmit",    TokenType.TRANSMIT);
        KEYWORDS.put("enter",       TokenType.ENTER);
        KEYWORDS.put("exit",        TokenType.EXIT);
        KEYWORDS.put("wait",        TokenType.WAIT);
        KEYWORDS.put("degrees",     TokenType.DEGREES);
        KEYWORDS.put("seconds",     TokenType.SECONDS);
        KEYWORDS.put("minutes",     TokenType.MINUTES);
        KEYWORDS.put("hours",       TokenType.HOURS);
        KEYWORDS.put("days",        TokenType.DAYS);
        KEYWORDS.put("km",          TokenType.KM);
        KEYWORDS.put("m",           TokenType.M);
        KEYWORDS.put("battery",     TokenType.BATTERY);
        KEYWORDS.put("temperature", TokenType.TEMPERATURE);
        KEYWORDS.put("power_level", TokenType.POWER_LEVEL);
        KEYWORDS.put("int",         TokenType.INT);
        KEYWORDS.put("float",       TokenType.FLOAT);
        KEYWORDS.put("bool",        TokenType.BOOL);
        KEYWORDS.put("true",        TokenType.TRUE);
        KEYWORDS.put("false",       TokenType.FALSE);
    }

    public Lexer(String source) {
        this.source = source;
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }
        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            // ── Arithmetic operators ───────────────────────
            case '+': addToken(TokenType.PLUS);  break;
            case '-': addToken(TokenType.MINUS); break;
            case '*': addToken(TokenType.STAR);  break;
            case '/': addToken(TokenType.SLASH); break;

            // ── Comparison & logical operators ────────────
            case '<': addToken(match('=') ? TokenType.LT_EQ   : TokenType.LT);    break;
            case '>': addToken(match('=') ? TokenType.GT_EQ   : TokenType.GT);    break;
            case '=': addToken(match('=') ? TokenType.EQ_EQ   : TokenType.EQ);    break;
            case '!': addToken(match('=') ? TokenType.BANG_EQ : TokenType.BANG);  break;
            case '&': if (match('&')) addToken(TokenType.AND); break;
            case '|': if (match('|')) addToken(TokenType.OR);  break;

            // ── Separators ────────────────────────────────
            case '{': addToken(TokenType.LBRACE);    break;
            case '}': addToken(TokenType.RBRACE);    break;
            case '(': addToken(TokenType.LPAREN);    break;
            case ')': addToken(TokenType.RPAREN);    break;
            case ':': addToken(TokenType.COLON);     break;
            case ';': addToken(TokenType.SEMICOLON); break;
            case ',': addToken(TokenType.COMMA);     break;

            // ── Whitespace ────────────────────────────────
            case ' ':
            case '\r':
            case '\t':
                break;
            case '\n':
                line++;
                break;

            // ── Literals & identifiers ────────────────────
            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    System.err.println("[line " + line + "] Error: unexpected character '" + c + "'");
                    addToken(TokenType.UNKNOWN);
                }
                break;
        }
    }

    // ── Helpers ───────────────────────────────────────────

    private char advance() {
        return source.charAt(current++);
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;
        current++;
        return true;
    }

    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    // ── Number scanning ───────────────────────────────────

    private void number() {
        while (isDigit(peek())) advance();

        if (peek() == '.' && isDigit(peekNext())) {
            advance(); // consume '.'
            while (isDigit(peek())) advance();
            addToken(TokenType.FLOAT_LIT, Double.parseDouble(source.substring(start, current)));
        } else {
            addToken(TokenType.INT_LIT, Integer.parseInt(source.substring(start, current)));
        }
    }

    // ── Identifier & keyword scanning ────────────────────

    private void identifier() {
        while (isAlphaNumeric(peek())) advance();
        String text = source.substring(start, current);
        TokenType type = KEYWORDS.getOrDefault(text, TokenType.IDENT);
        addToken(type);
    }

    // ── Character classification ──────────────────────────

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
               (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }
}