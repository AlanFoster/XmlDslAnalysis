package foo.language.implementation.lexing;

import com.intellij.lexer.*;
import com.intellij.psi.tree.IElementType;
import foo.language.CamelTypes;
import com.intellij.psi.TokenType;


%%

// Defined Used code
%{
  public CamelLanguageLexer() {
    this((java.io.Reader) null);
  }
%}

%public
%class CamelLanguageLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode
%eof{ return;
%eof}

EOL="\r"|"\n"|"\r\n"
LINE_WS=[\ \t\f]
WHITE_SPACE=({LINE_WS}|{EOL})+

IDENTIFIER=[a-zA-Z]([a-zA-Z_0-9])*
NUMBER=[:digit:]+
STRING=\"[^\"]*\"|'[^\']*'

%%
<YYINITIAL> {
    {WHITE_SPACE}             { return TokenType.WHITE_SPACE; }

    "${"                      { return CamelTypes.FUNC_BEGIN; }
    "}"                       { return CamelTypes.FUNC_END; }

    "."                       { return CamelTypes.DOT; }
    "?"                       { return CamelTypes.QUESTION_MARK; }

    "["                       { return CamelTypes.LEFT_SQUARE_BRACE; }
    "]"                       { return CamelTypes.RIGHT_SQUARE_BRACE; }

    // Operators
    ">=" { return CamelTypes.GT_EQ; }
    ">" { return CamelTypes.GT; }

    "<=" { return CamelTypes.LT_EQ; }
    "<" { return CamelTypes.LT; }

    "==" { return CamelTypes.EQ_EQ; }

    "&&" { return CamelTypes.AND_AND; }
    "||" { return CamelTypes.OR_OR; }


    "," { return CamelTypes.COMMA; }

    "(" { return CamelTypes.LEFT_PAREN; }
    ")" { return CamelTypes.RIGHT_PAREN; }

    {IDENTIFIER}              { return CamelTypes.IDENTIFIER; }
    {STRING}              { return CamelTypes.STRING; }
    {NUMBER}              { return CamelTypes.NUMBER; }

    [^] { return TokenType.BAD_CHARACTER; }
}

