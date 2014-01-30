package foo.language.Highlighting

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.{SyntaxHighlighterColors, HighlighterColors, DefaultLanguageHighlighterColors}
import foo.language.generated.CamelTypes
import com.intellij.openapi.fileTypes.{SyntaxHighlighterBase, SyntaxHighlighter}
import com.intellij.psi.tree.IElementType
import com.intellij.lexer.Lexer
import foo.language.implementation.lexing.CamelLanguageLexerAdapter
import com.intellij.psi.TokenType
import com.intellij.openapi.options.colors.AttributesDescriptor
import foo.language.Core.LanguageConstants
import java.awt.{Font, Color}
import com.intellij.openapi.editor.markup.TextAttributes

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
  import DefaultLanguageHighlighterColors._
  import HighlighterColors._
  import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey

  /**
   * A function, which when given an existing TextAttributesKey, will return a custom
   * value with additional metadata relating specifically to highlight editing
   *
   * @param inputKey The old TextAttributesKey
   * @param outputAttributes The output attributes, defaults to the input key's attributes
   * @return The new TextAttributesKey which contains the additional metadata
   */
  def customKey(inputKey: TextAttributesKey)
               (outputAttributes: => TextAttributes = inputKey.getDefaultAttributes): (TextAttributesKey, TextAttributesKey) =
    inputKey -> createTextAttributesKey(s"${LanguageConstants.languageName}_${inputKey.toString}", outputAttributes)

  def customColor(color: Color) = new TextAttributes(color, null, null, null, Font.BOLD)

  /**
   * Represents a mapping of known TextAttributesKey to custom Camel TextAttributesKey
   * values with a modified text attributes key value - useful for the syntax editing page
   *
   * This maps to the existing value if it does not exist, using the identity function f: A->A
   */
  private val textAttributeKeys: Map[TextAttributesKey, TextAttributesKey] = Map(
    customKey(OPERATION_SIGN)(),
    customKey(BRACES)(),
    customKey(PARENTHESES)(),
    customKey(TEXT)(customColor(Color.decode("#82290C"))),
    customKey(STRING)(),
    customKey(NUMBER)(),
    customKey(BAD_CHARACTER)(),
    customKey(CONSTANT)(),
    customKey(STATIC_FIELD)(customColor(Color.decode("#FF00FF")))
  ).withDefault(x => customKey(x)()._2)


  // Represents the high level groupings of token classes
  private val tokenClassGroupings = Map(
    // Operators
    OPERATION_SIGN -> List(
      CamelTypes.AND_AND,
      CamelTypes.OR_OR,
      CamelTypes.GT,
      CamelTypes.GT_EQ,
      CamelTypes.LT,
      CamelTypes.LT_EQ
    ),

    // Braces
    BRACES -> List(
      CamelTypes.FUNC_BEGIN,
      CamelTypes.FUNC_END,

      CamelTypes.LEFT_SQUARE_BRACE,
      CamelTypes.RIGHT_SQUARE_BRACE
    ),

    STATIC_FIELD -> List(
      CamelTypes.FQCN
    ),

    TEXT -> List(
      CamelTypes.IDENTIFIER,
      CamelTypes.DOT,
      CamelTypes.QUESTION_MARK
    ),

    PARENTHESES -> List(
      CamelTypes.LEFT_PAREN,
      CamelTypes.RIGHT_PAREN
    ),

    NUMBER -> List(
      CamelTypes.NUMBER
    ),

    // Strings
    STRING -> List(
      CamelTypes.STRING
    ),

    // Errors
    BAD_CHARACTER -> List(
      TokenType.BAD_CHARACTER
    ),

    // Language constants - reserved words etc
    CONSTANT -> List(
      TokenType.WHITE_SPACE
    )
  )

  // Create a new map from the tokenClassGroupings which contains the
  // mappings of IElementType to the respective color.
  // Note the distinctive default value to be STRING to avoid possible exceptions
  val tokenToColor: Map[IElementType, Array[TextAttributesKey]] =
    { for { (k, vs) <- tokenClassGroupings; v <- vs } yield (v, Array(textAttributeKeys(k))) }
    .withDefaultValue(Array(STRING))

  val descriptors: Array[AttributesDescriptor] = Array(
    new AttributesDescriptor("Operator", textAttributeKeys(OPERATION_SIGN)),
    new AttributesDescriptor("Square Braces", textAttributeKeys(BRACES)),
    new AttributesDescriptor("Parenthesis", textAttributeKeys(PARENTHESES)),
    new AttributesDescriptor("Text", textAttributeKeys(TEXT)),
    new AttributesDescriptor("String", textAttributeKeys(STRING)),
    new AttributesDescriptor("Number", textAttributeKeys(NUMBER)),
    new AttributesDescriptor("Error", textAttributeKeys(BAD_CHARACTER)),
    new AttributesDescriptor("Constant", textAttributeKeys(CONSTANT)),
    new AttributesDescriptor("FQCN", textAttributeKeys(STATIC_FIELD))
  )
}