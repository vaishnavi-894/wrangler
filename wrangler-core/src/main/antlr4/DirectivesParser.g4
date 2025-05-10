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

parser grammar DirectivesParser;

options {
    tokenVocab=DirectivesLexer;
}

// Parser rules

// Top-level rule
program: (directive | load_directives | pragma | macro | comment)* EOF;

// Directive rule
directive: ID (LPAREN arguments? RPAREN)? SEMI;

// Load directives rule
load_directives: LOAD_DIRECTIVES COLON value SEMI;

// Pragma rule
pragma: PRAGMA value SEMI;

// Macro rule
macro: MACRO value SEMI;

// Arguments rule
arguments: argument (COMMA argument)*;

// Argument rule
argument: ID ASSIGN value;

// Value rule
value: literal | expression;

// Expression rule
expression: 
    atom
    | expression PLUS atom
    | expression MINUS atom
    | expression STAR atom
    | expression SLASH atom
    | expression PERCENT atom
    | expression LT atom
    | expression GT atom
    | expression LE atom
    | expression GE atom
    | expression EQ atom
    | expression NE atom;

// Atom rule (handles parentheses and literals)
atom: 
    literal
    | LPAREN expression RPAREN;

// Literal rule
literal: 
    INT
    | FLOAT
    | STRING
    | TRUE
    | FALSE
    | NULL;
