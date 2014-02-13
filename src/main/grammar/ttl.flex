/* JFlex for TTL */
package crg.turtle;

import beaver.Symbol;
import beaver.Scanner;
import static crg.turtle.Parser.Terminals.*;

/**
 * This class is autogenerated by JFlex (http://www.jflex.de).
 * This source code is not encumbered with any licences from JFlex.
 * Quoting from: http://www.jflex.de/copying.html
 * " The code generated by JFlex inherits the copyright of the specification it
 *   was produced from. If it was your specification, you may use the generated
 *   code without restriction. "
 */
%%

%class TtlLexer
%extends Scanner
%public
%type Symbol
%yylexthrow Scanner.Exception
%eofval{
  return new Symbol(EOF, "end-of-file");
%eofval}
%unicode
%line
%column

%{
  static final Symbol END_OF_STATEMENT = new Symbol(EOF, "end of statement");

  private boolean endOfStatement = false;

  StringBuilder string = new StringBuilder();

  private Symbol token(short id) {
    return new Symbol(id, yyline + 1, yycolumn + 1, yylength());
  }

  private Symbol token(short id, Object value) {
    return new Symbol(id, yyline + 1, yycolumn + 1, yylength(), value);
  }

  private String unicode(String text) {
    if (text.charAt(1) == 'u') {
      return new String(new char[] {(char)Integer.parseInt(text.substring(2), 16)});
    } else {
      return new String(Character.toChars(Integer.parseInt(text.substring(2), 16)));
    }
  }

  public static TtlLexer newLexer(java.io.InputStream i) {
    return new TtlLexer(i);
  }

  public Symbol nextToken() throws java.io.IOException, Scanner.Exception {
    if (endOfStatement) {
      endOfStatement = false;
      return END_OF_STATEMENT;
    }
    return yylex();
  }
%}


Comment = "#" [^\r\n]* [\r\n]?

WS = [ \t\r\n]
ANON = "[" {WS}* "]"
DOT = "."
SEMICOLON = ";"
COMMA = ","
LEFT_BRACKET = "["
RIGHT_BRACKET = "]"
LEFT_PAREN = "("
RIGHT_PAREN = ")"
TYPED_LITERAL_SEPARATOR = "^^"
A = "a"

PN_LOCAL_ESC = "\\" [_\~\.-\!\$&'\(\)\*\+,;=:\/\?#@%]
HEX = [0-9a-fA-F]
PERCENT = "%" {HEX} {HEX}
PLX = {PERCENT} | {PN_LOCAL_ESC}
UCHAR = ( "\\u" {HEX} {HEX} {HEX} {HEX} ) | ( "\\U" {HEX} {HEX} {HEX} {HEX} {HEX} {HEX} {HEX} {HEX} )
PN_CHARS_BASE = [a-zA-Z\u00c0-\u00d6\u00d8-\u00f6\u00f8-\u02ff\u0370-\u037d\u037f-\u1fff\u200c-\u200d\u2070-\u218f\u2c00-\u2fef\u3001-\ud7ff\uf900-\ufdcf\ufdf0-\ufffd\ud800\udc00-\udb7f\udfff] | {UCHAR}
PN_CHARS_U = {PN_CHARS_BASE} | "_"
PN_CHARS = {PN_CHARS_U} | [\-0-9\u00b7\u0300-\u036f\u203f-\u2040]
PN_LOCAL = ( {PN_CHARS_U} | [:0-9] | {PLX} ) ( ( {PN_CHARS} | "." | ":" | {PLX} )*  ( {PN_CHARS} | ":" | {PLX} ) ) ?
PN_PREFIX = {PN_CHARS_BASE} ( ({PN_CHARS} | ".")* {PN_CHARS} )?

/* NIL = "(" {WS}* ")" */
/* ECHAR = "\\" [tbnrf\\\"'] */

STRING_LITERAL_SINGLE_QUOTE_DELIM = "'"
STRING_LITERAL_QUOTE_DELIM = "\""
STRING_LITERAL_LONG_SINGLE_QUOTE_DELIM = "'''"
STRING_LITERAL_LONG_QUOTE_DELIM = "\"\"\""

EXPONENT = [eE] [+\-]? [0-9]+
INTEGER = [0-9]+ 
DECIMAL = ( [0-9]+ "." [0-9]+ ) | ( "." [0-9]+ )
DOUBLE = ( [0-9]+ "." [0-9]+ {EXPONENT} ) | ( "." ( [0-9] )+ {EXPONENT} ) | ( ( [0-9] )+ EXPONENT )
INTEGER_POSITIVE = "+" {INTEGER}
DECIMAL_POSITIVE = "+" {DECIMAL}
DOUBLE_POSITIVE = "+" {DOUBLE}
INTEGER_NEGATIVE = "-" {INTEGER}
DECIMAL_NEGATIVE = "-" {DECIMAL}
DOUBLE_NEGATIVE = "-" {DOUBLE}

BASE = "@base"
PREFIX = "@prefix"
SPARQL_PREFIX = [Pp] [Rr] [Ee] [Ff] [Ii][Xx]
SPARQL_BASE = [Bb][Aa][Ss][Ee]
IRI_REF = "<" ( [^\<\>\"\{\}\|\^`\\\x00-\x20] | {UCHAR} )* ">"
PNAME_NS = {PN_PREFIX}? ":" 
PNAME_LN = {PNAME_NS} {PN_LOCAL}
BLANK_NODE_LABEL = "_:" {PN_LOCAL}
LANGTAG = "@" [a-zA-Z]+ ( "-" [a-zA-Z0-9]+ )*
TRUE = "true"
FALSE = "false"

%state STRING_LITERAL_SINGLE_QUOTE, STRING_LITERAL_QUOTE, STRING_LITERAL_LONG_SINGLE_QUOTE, STRING_LITERAL_LONG_QUOTE, TEMP_END

%%
/* keywords */
<YYINITIAL> {
  {Comment}                      { /* ignore */ }

  {BASE}                         { return token(BASE); }
  {PREFIX}                       { return token(PREFIX); }
  {SPARQL_BASE}                  { return token(SPARQL_BASE); }
  {SPARQL_PREFIX}                { return token(SPARQL_PREFIX); }
  {ANON}                         { return token(BLANK_NODE); }
  {IRI_REF}                      { return token(IRI_REF, yytext().substring(1, yylength() - 1)); }
  {PNAME_LN}                     { return token(PNAME_LN, yytext()); }
  {PNAME_NS}                     { return token(PNAME_NS, yytext().substring(0, yylength() - 1)); }
  {COMMA}                        { return token(COMMA); }
  {SEMICOLON}                    { return token(SEMICOLON); }
  {LEFT_BRACKET}                 { return token(START_BLANKNODE_LIST); }
  {RIGHT_BRACKET}                { return token(END_BLANKNODE_LIST); }
  {LEFT_PAREN}                   { return token(START_COLLECTION); }
  {RIGHT_PAREN}                  { return token(END_COLLECTION); }
  {TYPED_LITERAL_SEPARATOR}      { return token(TYPED_LITERAL_SEPARATOR); }
  {A}                            { return token(RDF_TYPE); }

  {DOT}                          { endOfStatement = true; return token(DOT); }

  {STRING_LITERAL_SINGLE_QUOTE_DELIM}      { yybegin(STRING_LITERAL_SINGLE_QUOTE); string.setLength(0); }
  {STRING_LITERAL_QUOTE_DELIM}             { yybegin(STRING_LITERAL_QUOTE); string.setLength(0); }
  {STRING_LITERAL_LONG_SINGLE_QUOTE_DELIM} { yybegin(STRING_LITERAL_LONG_SINGLE_QUOTE); string.setLength(0); }
  {STRING_LITERAL_LONG_QUOTE_DELIM}        { yybegin(STRING_LITERAL_LONG_QUOTE); string.setLength(0); }

  {LANGTAG}                      { return token(LANGTAG, yytext().substring(1)); }

  {INTEGER_POSITIVE}             { return token(INTEGER_LITERAL, yytext().substring(1, yylength() - 1)); }
  {DECIMAL_POSITIVE}             { return token(DECIMAL_LITERAL, yytext().substring(1, yylength() - 1)); }
  {DOUBLE_POSITIVE}              { return token(DOUBLE_LITERAL, yytext().substring(1, yylength() - 1)); }
  {INTEGER}                      { return token(INTEGER_LITERAL, yytext()); }
  {DECIMAL}                      { return token(DECIMAL_LITERAL, yytext()); }
  {DOUBLE}                       { return token(DOUBLE_LITERAL, yytext()); }
  {INTEGER_NEGATIVE}             { return token(INTEGER_LITERAL, yytext()); }
  {DECIMAL_NEGATIVE}             { return token(DECIMAL_LITERAL, yytext()); }
  {DOUBLE_NEGATIVE}              { return token(DOUBLE_LITERAL, yytext()); }

  {TRUE}                         { return token(BOOL, Boolean.TRUE); }
  {FALSE}                        { return token(BOOL, Boolean.FALSE); }

  {BLANK_NODE_LABEL}             { return token(BLANK_NODE, yytext()); }

  /* whitespace */
  {WS}                           { /* ignore */ }
}

<STRING_LITERAL_SINGLE_QUOTE> "'"        { yybegin(YYINITIAL); return token(STRING, string.toString()); }
<STRING_LITERAL_QUOTE> "\""              { yybegin(YYINITIAL); return token(STRING, string.toString()); }
<STRING_LITERAL_LONG_SINGLE_QUOTE> "'''" { yybegin(YYINITIAL); return token(STRING, string.toString()); }
<STRING_LITERAL_LONG_QUOTE> "\"\"\""     { yybegin(YYINITIAL); return token(STRING, string.toString()); }

<STRING_LITERAL_SINGLE_QUOTE,STRING_LITERAL_QUOTE,STRING_LITERAL_LONG_SINGLE_QUOTE,STRING_LITERAL_LONG_QUOTE> {
  "\\t"                          { string.append('\t'); }
  "\\b"                          { string.append('\b'); }
  "\\n"                          { string.append('\n'); }
  "\\r"                          { string.append('\r'); }
  "\\f"                          { string.append('\f'); }
  "\\\\"                         { string.append('\\'); }
  "\\\""                         { string.append('"'); }
  "\\'"                          { string.append('\''); }
}

<STRING_LITERAL_SINGLE_QUOTE> {
  [^'\\\n\r]*                    { string.append( yytext() ); }
  {UCHAR}                        { string.append( unicode(yytext()) ); }
}

<STRING_LITERAL_QUOTE> {
  [^\"\\\n\r]*                   { string.append( yytext() ); }
  {UCHAR}                        { string.append( unicode(yytext()) ); }
}

<STRING_LITERAL_LONG_SINGLE_QUOTE> {
  ( ( "'" | "''" )? )*           { string.append( yytext() ); }
  [^'\\]*                        { string.append( yytext() ); }
  {UCHAR}                        { string.append( unicode(yytext()) ); }
}

<STRING_LITERAL_LONG_QUOTE> {
  ( ( "\"" | "\"\"" )? )*        { string.append( yytext() ); }
  [^\"\\]*                       { string.append( yytext() ); }
  {UCHAR}                        { string.append( unicode(yytext()) ); }
}

/* error fallback */
.|\n                             { throw new Error("Illegal character <"+yytext()+"> at line: " + yyline + " column: " + yycolumn); }

