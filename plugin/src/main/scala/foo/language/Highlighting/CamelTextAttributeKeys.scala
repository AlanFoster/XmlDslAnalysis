package foo.language.Highlighting

import com.intellij.openapi.editor.colors.TextAttributesKey._
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors._
import foo.language.Core.LanguageConstants
import com.intellij.openapi.editor.{HighlighterColors, DefaultLanguageHighlighterColors}

/**
 * Defines the main keys which should be used when highlighting the
 * apache camel simple language.
 *
 * These keys will be accessed through an annotator, and can additionally
 * be changed within the camel settings page, and also within the embedded
 * resource file which provides the default color schemes for the language.
 *
 * Note this TextAttributesKey is used for syntax highlighting with both annotators
 * and within basic token highlighting.
 */
object CamelTextAttributeKeys {
  var FUNC_BRACES = createTextAttributesKey(
    camelId("FUNC_BRACES"),
    KEYWORD
  )

  def FQCN: TextAttributesKey = createTextAttributesKey(
    camelId("STATIC_FIELD"),
    DefaultLanguageHighlighterColors.STATIC_FIELD
  )

  def CONSTANT: TextAttributesKey = createTextAttributesKey(
    camelId("CONSTANT"),
    DefaultLanguageHighlighterColors.CONSTANT
  )

  def BAD_CHARACTER: TextAttributesKey = createTextAttributesKey(
    camelId("BAD_CHARACTER"),
    HighlighterColors.BAD_CHARACTER
  )

  def NUMBER: TextAttributesKey = createTextAttributesKey(
    camelId("NUMBER"),
    DefaultLanguageHighlighterColors.NUMBER
  )

  def STRING: TextAttributesKey = createTextAttributesKey(
    camelId("STRING"),
    DefaultLanguageHighlighterColors.STRING
  )

  def TEXT: TextAttributesKey = createTextAttributesKey(
    camelId("TEXT"),
    HighlighterColors.TEXT
  )

  def PARENTHESES: TextAttributesKey = createTextAttributesKey(
    camelId("PARENTHESES"),
    DefaultLanguageHighlighterColors.PARENTHESES
  )

  def SQUARE_BRACES: TextAttributesKey = createTextAttributesKey(
    camelId("BRACES"),
    DefaultLanguageHighlighterColors.BRACES
  )

  /**
   * Camel operator text attributes key, such as || etc
   */
  val OPERATION_SIGN: TextAttributesKey = createTextAttributesKey(
    camelId("OPERATION_SIGN"),
    DefaultLanguageHighlighterColors.OPERATION_SIGN
  )

  //val CAMEL_OPERATOR = TextAttributesKey.createTextAttributesKey()

  /**
   * Represents the key for camel function names is `headerAs`(...)
   */
  val CAMEL_FUNC = {
    createTextAttributesKey(
      camelId("CAMEL_FUNC"),
      FUNCTION_CALL
    )
  }

  /**
   * Creates a new unique id associated with the camel language.
   * This operation should be used in conjunction with creating new
   * TextAttributeKeys
   *
   * @param id The initial ID
   * @return A unique id associated with the language and the given id
   */
  private def camelId(id: String) = s"${LanguageConstants.languageName.toUpperCase}_${id}"
}
