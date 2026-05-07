import java.util.List;

public class Main {

    // ── ANSI colors for readable test output ─────────────────
    static final String GREEN  = "\u001B[32m";
    static final String RED    = "\u001B[31m";
    static final String YELLOW = "\u001B[33m";
    static final String RESET  = "\u001B[0m";
    static final String BOLD   = "\u001B[1m";

    static int passed = 0;
    static int failed = 0;

    public static void main(String[] args) {

        System.out.println(BOLD + "\n========================================" + RESET);
        System.out.println(BOLD + "   OrbitScript Lexer — Test Suite" + RESET);
        System.out.println(BOLD + "========================================\n" + RESET);

        testKeywords();
        testTypeKeywords();
        testBooleanLiterals();
        testArithmeticOperators();
        testComparisonOperators();
        testLogicalOperators();
        testAssignmentOperator();
        testSeparators();
        testIntLiterals();
        testFloatLiterals();
        testIdentifiers();
        testMultiCharOperators();
        testWhitespaceAndNewlines();
        testMixedExpression();
        testOrbitBlock();
        testTaskBlock();
        testUnknownCharacter();
        testEOF();

        // ── Summary ───────────────────────────────────────────
        System.out.println(BOLD + "\n========================================" + RESET);
        System.out.printf(BOLD + "  Results: " + GREEN + "%d passed" + RESET +
                          BOLD + " / " + RED + "%d failed" + RESET + "\n", passed, failed);
        System.out.println(BOLD + "========================================\n" + RESET);
    }

    // ════════════════════════════════════════════════════════
    // TEST GROUPS
    // ════════════════════════════════════════════════════════

    static void testKeywords() {
        printGroup("KEYWORDS");

        String src = "orbit task type altitude inclination if else when " +
                     "repeat every point to rotate capture transmit " +
                     "enter exit wait degrees seconds minutes hours days " +
                     "km m battery temperature power_level";

        TokenType[] expected = {
            TokenType.ORBIT, TokenType.TASK, TokenType.TYPE,
            TokenType.ALTITUDE, TokenType.INCLINATION,
            TokenType.IF, TokenType.ELSE, TokenType.WHEN,
            TokenType.REPEAT, TokenType.EVERY,
            TokenType.POINT, TokenType.TO,
            TokenType.ROTATE, TokenType.CAPTURE, TokenType.TRANSMIT,
            TokenType.ENTER, TokenType.EXIT, TokenType.WAIT,
            TokenType.DEGREES, TokenType.SECONDS, TokenType.MINUTES,
            TokenType.HOURS, TokenType.DAYS,
            TokenType.KM, TokenType.M,
            TokenType.BATTERY, TokenType.TEMPERATURE, TokenType.POWER_LEVEL,
            TokenType.EOF
        };

        assertTokenTypes("All domain keywords", src, expected);
    }

    static void testTypeKeywords() {
        printGroup("TYPE KEYWORDS");
        assertTokenTypes("int keyword",   "int",   new TokenType[]{TokenType.INT,   TokenType.EOF});
        assertTokenTypes("float keyword", "float", new TokenType[]{TokenType.FLOAT, TokenType.EOF});
        assertTokenTypes("bool keyword",  "bool",  new TokenType[]{TokenType.BOOL,  TokenType.EOF});
    }

    static void testBooleanLiterals() {
        printGroup("BOOLEAN LITERALS");
        assertTokenTypes("true",  "true",  new TokenType[]{TokenType.TRUE,  TokenType.EOF});
        assertTokenTypes("false", "false", new TokenType[]{TokenType.FALSE, TokenType.EOF});

        // Must NOT be treated as identifiers
        assertFirstTokenIsNot("true is not IDENT",  "true",  TokenType.IDENT);
        assertFirstTokenIsNot("false is not IDENT", "false", TokenType.IDENT);
    }

    static void testArithmeticOperators() {
        printGroup("ARITHMETIC OPERATORS");
        assertTokenTypes("plus",  "+", new TokenType[]{TokenType.PLUS,  TokenType.EOF});
        assertTokenTypes("minus", "-", new TokenType[]{TokenType.MINUS, TokenType.EOF});
        assertTokenTypes("star",  "*", new TokenType[]{TokenType.STAR,  TokenType.EOF});
        assertTokenTypes("slash", "/", new TokenType[]{TokenType.SLASH, TokenType.EOF});
        assertTokenTypes("all arithmetic", "+ - * /",
            new TokenType[]{TokenType.PLUS, TokenType.MINUS,
                            TokenType.STAR, TokenType.SLASH, TokenType.EOF});
    }

    static void testComparisonOperators() {
        printGroup("COMPARISON OPERATORS");
        assertTokenTypes("LT",      "<",  new TokenType[]{TokenType.LT,      TokenType.EOF});
        assertTokenTypes("GT",      ">",  new TokenType[]{TokenType.GT,      TokenType.EOF});
        assertTokenTypes("LT_EQ",   "<=", new TokenType[]{TokenType.LT_EQ,   TokenType.EOF});
        assertTokenTypes("GT_EQ",   ">=", new TokenType[]{TokenType.GT_EQ,   TokenType.EOF});
        assertTokenTypes("EQ_EQ",   "==", new TokenType[]{TokenType.EQ_EQ,   TokenType.EOF});
        assertTokenTypes("BANG_EQ", "!=", new TokenType[]{TokenType.BANG_EQ, TokenType.EOF});
    }

    static void testLogicalOperators() {
        printGroup("LOGICAL OPERATORS");
        assertTokenTypes("AND",  "&&", new TokenType[]{TokenType.AND,  TokenType.EOF});
        assertTokenTypes("OR",   "||", new TokenType[]{TokenType.OR,   TokenType.EOF});
        assertTokenTypes("BANG", "!",  new TokenType[]{TokenType.BANG, TokenType.EOF});

        // '!' alone must NOT consume the next character
        assertTokenTypes("! followed by x", "! x",
            new TokenType[]{TokenType.BANG, TokenType.IDENT, TokenType.EOF});
    }

    static void testAssignmentOperator() {
        printGroup("ASSIGNMENT OPERATOR");
        assertTokenTypes("single =",  "=",  new TokenType[]{TokenType.EQ,    TokenType.EOF});
        assertTokenTypes("double ==", "==", new TokenType[]{TokenType.EQ_EQ, TokenType.EOF});

        // Critical: = and == must NOT be confused
        assertFirstTokenType("= gives EQ not EQ_EQ", "=",  TokenType.EQ);
        assertFirstTokenType("== gives EQ_EQ",        "==", TokenType.EQ_EQ);
    }

    static void testSeparators() {
        printGroup("SEPARATORS");
        assertTokenTypes("LBRACE",    "{", new TokenType[]{TokenType.LBRACE,    TokenType.EOF});
        assertTokenTypes("RBRACE",    "}", new TokenType[]{TokenType.RBRACE,    TokenType.EOF});
        assertTokenTypes("LPAREN",    "(", new TokenType[]{TokenType.LPAREN,    TokenType.EOF});
        assertTokenTypes("RPAREN",    ")", new TokenType[]{TokenType.RPAREN,    TokenType.EOF});
        assertTokenTypes("COLON",     ":", new TokenType[]{TokenType.COLON,     TokenType.EOF});
        assertTokenTypes("SEMICOLON", ";", new TokenType[]{TokenType.SEMICOLON, TokenType.EOF});
        assertTokenTypes("COMMA",     ",", new TokenType[]{TokenType.COMMA,     TokenType.EOF});
    }

    static void testIntLiterals() {
        printGroup("INTEGER LITERALS");
        assertTokenTypes("zero",        "0",     new TokenType[]{TokenType.INT_LIT, TokenType.EOF});
        assertTokenTypes("single digit","7",     new TokenType[]{TokenType.INT_LIT, TokenType.EOF});
        assertTokenTypes("multi digit", "408",   new TokenType[]{TokenType.INT_LIT, TokenType.EOF});
        assertTokenTypes("large int",   "36000", new TokenType[]{TokenType.INT_LIT, TokenType.EOF});

        // Check literal value is correctly parsed
        assertLiteralValue("int value 42",  "42",  42);
        assertLiteralValue("int value 100", "100", 100);
    }

    static void testFloatLiterals() {
        printGroup("FLOAT LITERALS");
        assertTokenTypes("basic float", "3.14",  new TokenType[]{TokenType.FLOAT_LIT, TokenType.EOF});
        assertTokenTypes("zero float",  "0.0",   new TokenType[]{TokenType.FLOAT_LIT, TokenType.EOF});
        assertTokenTypes("large float", "550.5", new TokenType[]{TokenType.FLOAT_LIT, TokenType.EOF});

        // Check literal value
        assertLiteralValue("float value 3.14", "3.14", 3.14);

        // "3." — no digit after dot → stays INT_LIT (peekNext check fails)
        assertFirstTokenType("3. stays INT_LIT", "3.", TokenType.INT_LIT);
    }

    static void testIdentifiers() {
        printGroup("IDENTIFIERS");
        assertTokenTypes("single letter",       "x",         new TokenType[]{TokenType.IDENT, TokenType.EOF});
        assertTokenTypes("letter + digits",     "x1",        new TokenType[]{TokenType.IDENT, TokenType.EOF});
        assertTokenTypes("letter + underscore", "my_sat",    new TokenType[]{TokenType.IDENT, TokenType.EOF});
        assertTokenTypes("mixed",               "sat1_data", new TokenType[]{TokenType.IDENT, TokenType.EOF});
        assertTokenTypes("all caps",            "SAT",       new TokenType[]{TokenType.IDENT, TokenType.EOF});

        // Keywords must NOT become IDENT
        assertFirstTokenIsNot("orbit is not IDENT", "orbit", TokenType.IDENT);
        assertFirstTokenIsNot("if is not IDENT",    "if",    TokenType.IDENT);

        // Identifiers that START with a keyword prefix must still be IDENT
        assertFirstTokenType("orbiter is IDENT", "orbiter", TokenType.IDENT);
        assertFirstTokenType("iffy is IDENT",    "iffy",    TokenType.IDENT);
    }

    static void testMultiCharOperators() {
        printGroup("MULTI-CHARACTER OPERATOR DISAMBIGUATION");

        // <= must be ONE token, not LT + EQ
        assertTokenTypes("<= is one token", "<=",
            new TokenType[]{TokenType.LT_EQ, TokenType.EOF});

        // < then = separated by space must be TWO tokens
        assertTokenTypes("< then = (separate)", "< =",
            new TokenType[]{TokenType.LT, TokenType.EQ, TokenType.EOF});

        // != must be ONE token
        assertTokenTypes("!= is one token", "!=",
            new TokenType[]{TokenType.BANG_EQ, TokenType.EOF});

        // && must be ONE token
        assertTokenTypes("&& is one token", "&&",
            new TokenType[]{TokenType.AND, TokenType.EOF});

        // || must be ONE token
        assertTokenTypes("|| is one token", "||",
            new TokenType[]{TokenType.OR, TokenType.EOF});
    }

    static void testWhitespaceAndNewlines() {
        printGroup("WHITESPACE & NEWLINES");

        // Spaces are skipped
        assertTokenTypes("spaces ignored", "orbit   task",
            new TokenType[]{TokenType.ORBIT, TokenType.TASK, TokenType.EOF});

        // Tabs are skipped
        assertTokenTypes("tabs ignored", "orbit\ttask",
            new TokenType[]{TokenType.ORBIT, TokenType.TASK, TokenType.EOF});

        // Newlines increment line counter
        String multiLine = "orbit\ntask\nif";
        List<Token> tokens = new Lexer(multiLine).scanTokens();
        assertCondition("'orbit' is on line 1", tokens.get(0).line == 1);
        assertCondition("'task' is on line 2",  tokens.get(1).line == 2);
        assertCondition("'if' is on line 3",    tokens.get(2).line == 3);
    }

    static void testMixedExpression() {
        printGroup("MIXED EXPRESSION");

        // altitude <= 550.5 && battery > 20
        String expr = "altitude <= 550.5 && battery > 20";
        assertTokenTypes("sensor condition expression", expr, new TokenType[]{
            TokenType.ALTITUDE,
            TokenType.LT_EQ,
            TokenType.FLOAT_LIT,
            TokenType.AND,
            TokenType.BATTERY,
            TokenType.GT,
            TokenType.INT_LIT,
            TokenType.EOF
        });
    }

    static void testOrbitBlock() {
        printGroup("ORBIT BLOCK (REALISTIC SOURCE)");

        String src =
            "orbit leo {\n" +
            "    altitude : 550 km;\n" +
            "    inclination : 45.0 degrees;\n" +
            "}";

        assertTokenTypes("orbit block tokens", src, new TokenType[]{
            TokenType.ORBIT,
            TokenType.IDENT,        // leo
            TokenType.LBRACE,
            TokenType.ALTITUDE,
            TokenType.COLON,
            TokenType.INT_LIT,      // 550
            TokenType.KM,
            TokenType.SEMICOLON,
            TokenType.INCLINATION,
            TokenType.COLON,
            TokenType.FLOAT_LIT,    // 45.0
            TokenType.DEGREES,
            TokenType.SEMICOLON,
            TokenType.RBRACE,
            TokenType.EOF
        });
    }

    static void testTaskBlock() {
        printGroup("TASK BLOCK (REALISTIC SOURCE)");

        String src =
            "task capture_image {\n" +
            "    if (battery > 20) {\n" +
            "        capture;\n" +
            "    }\n" +
            "}";

        assertTokenTypes("task block tokens", src, new TokenType[]{
            TokenType.TASK,
            TokenType.IDENT,        // capture_image
            TokenType.LBRACE,
            TokenType.IF,
            TokenType.LPAREN,
            TokenType.BATTERY,
            TokenType.GT,
            TokenType.INT_LIT,      // 20
            TokenType.RPAREN,
            TokenType.LBRACE,
            TokenType.CAPTURE,
            TokenType.SEMICOLON,
            TokenType.RBRACE,
            TokenType.RBRACE,
            TokenType.EOF
        });
    }

    static void testUnknownCharacter() {
        printGroup("UNKNOWN / ERROR CHARACTERS");

        // @ is not in the language — must produce UNKNOWN, not crash
        List<Token> tokens = new Lexer("@").scanTokens();
        assertCondition("@ produces UNKNOWN token",
            tokens.get(0).type == TokenType.UNKNOWN);

        // Lexer must recover and continue after an unknown character
        List<Token> tokens2 = new Lexer("orbit @ task").scanTokens();
        assertCondition("lexer recovers: first token is ORBIT",
            tokens2.get(0).type == TokenType.ORBIT);
        assertCondition("lexer recovers: third token is TASK",
            tokens2.get(2).type == TokenType.TASK);
    }

    static void testEOF() {
        printGroup("EOF");

        List<Token> tokens = new Lexer("").scanTokens();
        assertCondition("empty source produces exactly one EOF token",
            tokens.size() == 1 && tokens.get(0).type == TokenType.EOF);

        List<Token> tokens2 = new Lexer("orbit").scanTokens();
        assertCondition("last token is always EOF",
            tokens2.get(tokens2.size() - 1).type == TokenType.EOF);
    }

    // ════════════════════════════════════════════════════════
    // ASSERTION HELPERS
    // ════════════════════════════════════════════════════════

    static void assertTokenTypes(String name, String source, TokenType[] expected) {
        List<Token> tokens = new Lexer(source).scanTokens();
        boolean ok = tokens.size() == expected.length;
        if (ok) {
            for (int i = 0; i < expected.length; i++) {
                if (tokens.get(i).type != expected[i]) { ok = false; break; }
            }
        }
        report(name, ok, tokens, expected);
    }

    static void assertTokenSequence(String name, String source, TokenType[] expected) {
        assertTokenTypes(name, source, expected);
    }

    static void assertFirstTokenType(String name, String source, TokenType expected) {
        List<Token> tokens = new Lexer(source).scanTokens();
        boolean ok = !tokens.isEmpty() && tokens.get(0).type == expected;
        reportSimple(name, ok,
            ok ? "" : "expected " + expected + " but got " + tokens.get(0).type);
    }

    static void assertFirstTokenIsNot(String name, String source, TokenType notExpected) {
        List<Token> tokens = new Lexer(source).scanTokens();
        boolean ok = !tokens.isEmpty() && tokens.get(0).type != notExpected;
        reportSimple(name, ok,
            ok ? "" : "expected NOT " + notExpected + " but got it anyway");
    }

    static void assertLiteralValue(String name, String source, Object expectedLiteral) {
        List<Token> tokens = new Lexer(source).scanTokens();
        boolean ok = false;
        if (!tokens.isEmpty()) {
            Object lit = tokens.get(0).literal;
            if (expectedLiteral instanceof Double) {
                ok = lit instanceof Double &&
                     Math.abs((Double) lit - (Double) expectedLiteral) < 1e-9;
            } else {
                ok = expectedLiteral.equals(lit);
            }
        }
        reportSimple(name, ok,
            ok ? "" : "expected literal " + expectedLiteral +
                      " but got " + (tokens.isEmpty() ? "none" : tokens.get(0).literal));
    }

    static void assertCondition(String name, boolean condition) {
        reportSimple(name, condition, condition ? "" : "condition was false");
    }

    // ════════════════════════════════════════════════════════
    // REPORTING
    // ════════════════════════════════════════════════════════

    static void report(String name, boolean ok, List<Token> actual, TokenType[] expected) {
        if (ok) {
            System.out.println(GREEN + "  PASS" + RESET + "  " + name);
            passed++;
        } else {
            System.out.println(RED + "  FAIL" + RESET + "  " + name);
            System.out.print(YELLOW + "        expected: " + RESET);
            for (TokenType t : expected) System.out.print(t + " ");
            System.out.println();
            System.out.print(YELLOW + "        actual:   " + RESET);
            for (Token t : actual) System.out.print(t.type + " ");
            System.out.println();
            failed++;
        }
    }

    static void reportSimple(String name, boolean ok, String detail) {
        if (ok) {
            System.out.println(GREEN + "  PASS" + RESET + "  " + name);
            passed++;
        } else {
            System.out.println(RED + "  FAIL" + RESET + "  " + name);
            if (!detail.isEmpty())
                System.out.println(YELLOW + "        " + detail + RESET);
            failed++;
        }
    }

    static void printGroup(String name) {
        System.out.println(BOLD + "\n── " + name + " ──" + RESET);
    }
}