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

lexer grammar DirectivesLexer;

// Whitespace and comments
WS: [ \t\r\n]+ -> skip;
COMMENT: '#' ~('\n'|'\r')* -> skip;

// Punctuation
SEMI: ';';
LPAREN: '(';
RPAREN: ')';
LBRACE: '{';
RBRACE: '}';
LBRACK: '[';
RBRACK: ']';
COMMA: ',';
DOT: '.';
COLON: ':';

// Operators
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
VERSION: 'version';
LOAD_DIRECTIVES: 'load_directives';
FOR: 'for';
IN: 'in';

// Pragma
PRAGMA: '#pragma';

// Macros
MACRO: '$' ID;

// Literals
INT: [0-9]+;
FLOAT: [0-9]+ ('.' [0-9]*)?;
STRING: '"' (~['"\n\r] | '\"')* '"';
TRUE: 'true';
FALSE: 'false';
NULL: 'null';

// Identifiers
ID: [a-zA-Z_] [a-zA-Z_0-9]*;
