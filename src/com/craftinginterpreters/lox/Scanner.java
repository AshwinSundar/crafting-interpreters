package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.craftinginterpreters.lox.TokenType.*;

class Scanner {
  private final String source;
  private final List<Token> tokens = new ArrayList<>();
  private int start = 0; // points to first character in lexeme being scanned
  private int current = 0; // points to current character being scanned
  private int line = 1; // tracks which line of the source we're on

  Scanner(String source) {
    this.source = source;
  }

  List<Token> scanTokens() {
    while(!isAtEnd()) {
      // We are at the beginning of the next lexeme
      start = current;
      scanToken();
    }

    tokens.add(new Token(EOF, "", null, line));
    return tokens;
  }

  private void scanToken() {
    char c = advance();
    switch(c) {
      case '(': addToken(LEFT_PAREN); break;
      case ')': addToken(RIGHT_PAREN); break;
      case '{': addToken(LEFT_BRACE); break;
      case '}': addToken(RIGHT_BRACE); break;
      case ',': addToken(COMMA); break;
      case '.': addToken(DOT); break;
      case '-': addToken(MINUS); break;
      case '+': addToken(PLUS); break;
      case ';': addToken(SEMICOLON); break;
      case '*': addToken(STAR); break;
      case '!':
        addToken(match('=') ? BANG_EQUAL : BANG);
        break;
      case '=':
        addToken(match('=') ? EQUAL_EQUAL : EQUAL);
        break;
      case '<':
        addToken(match('=') ? LESS_EQUAL : LESS);
        break;
      case '>':
        addToken(match('=') ? GREATER_EQUAL : GREATER);
        break;
      case '/':
        if (match('/')) {
          // This is a comment, and goes to the end of the line
          while (peek() != '\n' && !isAtEnd()) advance();
        } else {
          // Otherwise, must be the division operator
          addToken(SLASH);
        }
        break;
      case ' ':
      case '\r':
      case '\t':
        // ignore whitespace
        break;
      case '\n':
        line++;
        break;
      case '"': string(); break;
      default: 
        if (isDigit(c)) {
          number();
        } else if (isAlpha(c)) {
          identifier();
        } else {
          Lox.error(line, "Unexpected character.");
        }
        break;
    }
  }

  private void identifier() {
    while (isAlphaNumeric(peek())) advance();

    String text = source.substring(start, current);
    TokenType type = keywords.get(text);
    if (type == null) type = IDENTIFIER;
    addToken(type);
  }

  private void number() {
    // consume as many digits as are found
    while (isDigit(peek())) advance();

    // Handle decimal
    if (peek() == '.' && isDigit(peekNext())) {
      advance();
    }

    // consume any digits after decimal
    while (isDigit(peek())) advance();

    // convert lexeme to its numeric value
    addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
  }

  private void string() {
    while (peek() != '"' && !isAtEnd()) {
      if (peek() == '\n') line++; // multiline strings are permitted - but need to advance line if newline is encountered inside of a string
      advance();
    }

    if (isAtEnd()) {
      Lox.error(line, "Unterminated string.");
      return;
    }

    // Have reached the closing bracket
    advance();

    // Trim surrounding quotes
   String value = source.substring(start + 1, current - 1);
   addToken(STRING, value);
  }

  private boolean match(char expected) {
    if (isAtEnd()) return false;
    if (source.charAt(current) != expected) return false;

    // only advance character if it matches expected
    current++;
    return true;
  }

  // lookahead
  private char peek() {
    if (isAtEnd()) return '\0';
    return source.charAt(current);
  }

  private char peekNext() {
    if (current + 1 >= source.length()) return '\0';
    return source.charAt(current + 1);
  }

  private boolean isAlpha(char c) {
    return (c >= 'a' && c <= 'z') ||
           (c >= 'A' && c <= 'Z') ||
           c == '_';
  }

  private boolean isAlphaNumeric(char c) {
    return isAlpha(c) || isDigit(c);
  }

  private boolean isDigit(char c) {
    return c >= '0' && c <= '9';
  }
 
  // returns true when all characters have been consumed
  private boolean isAtEnd() {
    return current >= source.length();
  }

  private char advance() {
    return source.charAt(current++);
  }

  // gets text of current lexeme and creates a new token for it
  private void addToken(TokenType type) {
    addToken(type, null);
  }

  private void addToken(TokenType type, Object literal) {
    String text = source.substring(start, current);
    tokens.add(new Token(type, text, literal, line));
  }

  private static final Map<String, TokenType> keywords;

  // reserved words
  static {
    keywords = new HashMap<>();
    keywords.put("and",     AND);
    keywords.put("class",   CLASS);
    keywords.put("else",    ELSE);
    keywords.put("false",   FALSE);
    keywords.put("for",     FOR);
    keywords.put("fun",     FUN);
    keywords.put("if",      IF);
    keywords.put("nil",     NIL);
    keywords.put("or",      OR);
    keywords.put("print",   PRINT);
    keywords.put("return",  RETURN);
    keywords.put("super",   SUPER);
    keywords.put("this",    THIS);
    keywords.put("true",    TRUE);
    keywords.put("var",     VAR);
    keywords.put("while",   WHILE);
  }
}
