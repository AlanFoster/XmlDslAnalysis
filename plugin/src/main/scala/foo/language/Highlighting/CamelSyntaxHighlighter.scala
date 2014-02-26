package foo.language.Highlighting

import com.intellij.openapi.editor.colors.TextAttributesKey
import foo.language.generated.CamelTypes
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType
import com.intellij.lexer.Lexer
import foo.language.implementation.lexing.CamelLanguageLexerAdapter
import com.intellij.psi.TokenType

/**
 * Represents a CamelSyntaxHighlighter
 */
class CamelSyntaxHighlighter extends SyntaxHighlighterBase {
  /**
   *
   * @return The flex adapter based lexer for the Apache Camel Language
   */
  def getHighlightingLexer: Lexer = new CamelLanguageLexerAdapter()

  def getTokenHighlights(tokenType: IElementType): Array[TextAttributesKey] =
    CamelSyntaxHighlighter.tokenToColor(tokenType)
}

object CamelSyntaxHighlighter {

  /**
   * Represents the high level groupings of token classes.
   * Note - this only provides a basic level of token association to colors,
   * and does *not* provide the complex coloring such as function names.
   */
  private val tokenClassGroupings = Map(
    // Operators
    CamelTextAttributeKeys.OPERATION_SIGN -> List(
      CamelTypes.AND_AND,
      CamelTypes.OR_OR,
      CamelTypes.GT,
      CamelTypes.GT_EQ,
      CamelTypes.LT,
      CamelTypes.LT_EQ
    ),

    // Braces
    CamelTextAttributeKeys.SQUARE_BRACES -> List(
      CamelTypes.LEFT_SQUARE_BRACE,
      CamelTypes.RIGHT_SQUARE_BRACE
    ),

    CamelTextAttributeKeys.FQCN -> List(
      CamelTypes.FQCN
    ),

    CamelTextAttributeKeys.FUNC_BRACES -> List(
      CamelTypes.FUNC_BEGIN,
      CamelTypes.FUNC_END
    ),

    CamelTextAttributeKeys.TEXT -> List(
      CamelTypes.IDENTIFIER,
      CamelTypes.DOT,
      CamelTypes.QUESTION_MARK
    ),

    CamelTextAttributeKeys.PARENTHESES -> List(
      CamelTypes.LEFT_PAREN,
      CamelTypes.RIGHT_PAREN
    ),

    CamelTextAttributeKeys.NUMBER -> List(
      CamelTypes.NUMBER
    ),

    // Strings
    CamelTextAttributeKeys.STRING -> List(
      CamelTypes.STRING
    ),

    // Errors
    CamelTextAttributeKeys.BAD_CHARACTER -> List(
      TokenType.BAD_CHARACTER
    ),

    // Language constants - reserved words etc
    CamelTextAttributeKeys.CONSTANT -> List(
      TokenType.WHITE_SPACE
    )
  )

  /**
   * Create a new map from the tokenClassGroupings which contains the
   * mappings of IElementType to the respective color.
   *
   * Note the distinctive default value to be STRING to avoid possible exceptions
   */
  val tokenToColor: Map[IElementType, Array[TextAttributesKey]] =
    { for { (k, vs) <- tokenClassGroupings; v <- vs } yield (v, Array(k)) }
    .withDefaultValue(Array(CamelTextAttributeKeys.STRING))
}