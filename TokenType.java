public enum TokenType {

    // ── KEYWORDS ──────────────────────────────────────────
    // Domain keywords
    ORBIT, TASK, ALTITUDE, INCLINATION,
    WHEN, REPEAT, EVERY,
    POINT, TO,
    ROTATE, CAPTURE, TRANSMIT,
    ENTER, EXIT, WAIT,

    // Control flow
    IF, ELSE,

    // Units
    DEGREES, SECONDS, MINUTES, HOURS, DAYS, KM, M,

    // Sensor keywords
    BATTERY, TEMPERATURE, POWER_LEVEL,

    // Type keywords
    TYPE,           // user-defined type declaration
    INT,            // primitive type name
    FLOAT,          // primitive type name
    BOOL,           // primitive type name

    // Boolean literals (keywords in most languages)
    TRUE, FALSE,

    // ── OPERATORS ─────────────────────────────────────────
    // Arithmetic
    PLUS,           // +
    MINUS,          // -
    STAR,           // *
    SLASH,          // /

    // Comparison
    LT,             // 
    GT,             // >
    LT_EQ,          // <=
    GT_EQ,          // >=
    EQ_EQ,          // ==
    BANG_EQ,        // !=

    // Logical
    AND,            // &&
    OR,             // ||
    BANG,           // !

    // Assignment
    EQ,             // =

    // ── SEPARATORS ────────────────────────────────────────
    LBRACE,         // {
    RBRACE,         // }
    LPAREN,         // (
    RPAREN,         // )
    COLON,          // :
    SEMICOLON,      // ;
    COMMA,          // ,

    // ── LITERALS ──────────────────────────────────────────
    INT_LIT,        // e.g.  42
    FLOAT_LIT,      // e.g.  3.14
    BOOL_LIT,       // true / false  ← resolved from TRUE / FALSE at parse time

    // ── IDENTIFIER ────────────────────────────────────────
    IDENT,

    // ── SPECIAL ───────────────────────────────────────────
    EOF,            // end of file
    UNKNOWN         // unrecognized character → lexer error with line number
}