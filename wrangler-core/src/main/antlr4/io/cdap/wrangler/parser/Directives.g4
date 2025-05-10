/*
 * Copyright © 2017-2019 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

grammar Directives;

options {
    language = Java;
}

fragment BYTE_UNIT
 : 'B' | 'KB' | 'MB' | 'GB' | 'TB'
 ;

fragment TIME_UNIT
 : 'ns' | 'us' | 'ms' | 's' | 'm' | 'h' | 'd'
 ;

BYTE_SIZE
 : Number BYTE_UNIT
 ;

TIME_DURATION
 : Number TIME_UNIT
 ;

/**
 * Parser Grammar for recognizing tokens and constructs of the directives language.
 */

// Parser Rules

recipe
 : statements EOF
 ;

statements
 :  ( Comment | macro | directive ';' | pragma ';' | ifStatement)*
 ;

directive
 : command
  (   codeblock
    | identifier
    | macro
    | text
    | number
    | bool
    | column
    | colList
    | numberList
    | boolList
    | stringList
    | numberRanges
    | properties
  )*?
  ;

ifStatement
  : ifStat elseIfStat* elseStat? RBRACE
  ;

ifStat
  : IF expression LBRACE statements
  ;

elseIfStat
  : RBRACE ELSE IF expression LBRACE statements
  ;

elseStat
  : RBRACE ELSE LBRACE statements
  ;

expression
  : LPAREN expression RPAREN
  | LPAREN (~LPAREN | expression)* RPAREN
  ;

forStatement
 : FOR LPAREN Identifier ASSIGN expression SEMI expression SEMI expression RPAREN LBRACE statements RBRACE
 ;

macro
 : DOLLAR OBRACE (~OBRACE | macro | MACRO)*? CBRACE
 ;

pragma
 : PRAGMA (pragmaLoadDirective | pragmaVersion)
 ;

pragmaLoadDirective
 : LOAD_DIRECTIVES identifierList
 ;

pragmaVersion
 : VERSION Number
 ;

codeblock
 : EXP COLON condition
 ;

identifier
 : Identifier
 ;

properties
 : PROP COLON OBRACE (propertyList)+ CBRACE
 ;

propertyList
 : property (COMMA property)*
 ;

property
 : Identifier ASSIGN ( text | number | bool )
 ;

numberRanges
 : numberRange (COMMA numberRange)*
 ;

numberRange
 : Number COLON Number ASSIGN value
 ;

value
 : String | Number | Column | Bool
 ;

ecommand
 : EXCLAMATION Identifier
 ;

config
 : Identifier
 ;

column
 : Column
 ;

text
 : String
 ;

number
 : Number
 | BYTE_SIZE
 | TIME_DURATION
 ;

// Parser Rules

// Whitespace and comments
WS: [ \t\r\n]+ -> skip;
COMMENT: '#' ~('\n'|'\r')* -> skip;

bool
 : Bool
 ;

condition
 : OBRACE (~CBrace | condition)* CBrace
 ;

command
 : Identifier
 ;

colList
 : Column (COMMA Column)+
 ;

numberList
 : Number (COMMA Number)+
 | BYTE_SIZE (COMMA BYTE_SIZE)+
 | TIME_DURATION (COMMA TIME_DURATION)+
 ;

boolList
 : Bool (COMMA Bool)+
 ;

stringList
 : String (COMMA String)+
 ;

identifierList
 : Identifier (COMMA Identifier)*
 ;


// Lexer Rules

// Whitespace and comments
WS: [ \t\r\n]+ -> skip;
COMMENT: '#' ~('\n'|'\r')* -> skip;

// Punctuation
SEMI: ';';
LPAREN: '(';
RPAREN: ')';
LBRACE: '{';

// Byte Size and Time Duration
BYTE_SIZE: Number ('B' | 'KiB' | 'MiB' | 'GiB' | 'TiB');
TIME_DURATION: Number ('ns' | 'us' | 'ms' | 's' | 'm' | 'h' | 'd');
RBRACE: '}';
LBRACK: '[';
RBRACK: ']';
COMMA: ',';
DOT: '.';
COLON: ':';
ASSIGN: '=';
PLUS: '+';
MINUS: '-';
STAR: '*';
SLASH: '/';
PERCENT: '%';
LT: '<';
GT: '>';
LE: '<=';
GE: '>=';
EQ: '==';
NE: '!=';

// Keywords
IF: 'if';
ELSE: 'else';
FOR: 'for';
VERSION: 'version';
LOAD_DIRECTIVES: 'load_directives';
EXP: 'exp';
PROP: 'prop';
EXCLAMATION: '!';
PRAGMA: '#pragma';

// Tokens
Comment: '#' ~('\n'|'\r')*;
DOLLAR: '$';
OBRACE: '{';
CBRACE: '}';
LBRACE: '(';
RBRACE: ')';
LBRACK: '[';
RBRACK: ']';
COMMA: ',';
DOT: '.';
COLON: ':';
ASSIGN: '=';
PLUS: '+';
MINUS: '-';
STAR: '*';
SLASH: '/';
PERCENT: '%';
LT: '<';
GT: '>'; 
LE: '<=';
GE: '>=';
EQ: '==';
NE: '!=';
SEMI: ';';

// Literals
INT: [0-9]+;
FLOAT: [0-9]+ ('.' [0-9]*)?;
BYTE_SIZE: INT ('.' [0-9]*)? ('B' | 'KiB' | 'MiB' | 'GiB' | 'TiB');
TIME_DURATION: INT ('.' [0-9]*)? ('ns' | 'us' | 'ms' | 's' | 'm' | 'h' | 'd');
STRING: '"' (~['"\n\r] | '\"')* '"';
TRUE: 'true';
FALSE: 'false';
NULL: 'null';

// Identifiers
ID: [a-zA-Z_] [a-zA-Z_0-9]*;
Identifier: ID;
Column: ':' [a-zA-Z_\-] [:a-zA-Z_0-9\-]*;
Macro: [a-zA-Z_] [a-zA-Z_0-9]*;
Number: INT ('.' Digit*)?;
Bool: TRUE | FALSE;

// Escape sequences
fragment EscapedChar
   :   '\' ('b'|'t'|'n'|'f'|'r'|'"'|'\'|'\\')
   |   UnicodeEscape
   |   OctalEscape
   ;

fragment
OctalEscape
   :   '\' ('0'..'3') ('0'..'7') ('0'..'7')
   |   '\' ('0'..'7') ('0'..'7')
   |   '\' ('0'..'7')
   ;

fragment
UnicodeEscape
   :   '\' 'u' HexDigit HexDigit HexDigit HexDigit
   ;

fragment HexDigit : ('0'..'9'|'a'..'f'|'A'..'F') ;

// Comments and whitespace
Comment
 : ('//' ~[\r\n]* | '/*' .*? '*/' | '--' ~[\r\n]* ) -> skip
;

// Number fragments
fragment Digit : [0-9] ;
