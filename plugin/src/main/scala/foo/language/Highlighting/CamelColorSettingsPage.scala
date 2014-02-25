package foo.language.Highlighting

import com.intellij.openapi.options.colors.{AttributesDescriptor, ColorDescriptor, ColorSettingsPage}
import java.util
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import javax.swing.Icon
import foo.Icons
import foo.language.Core.LanguageConstants
import foo.language.implementation.lexing.CamelLanguageLexer

/**
 * Allows the user to modify the color settings for the apache
 * camel language
 */
class CamelColorSettingsPage extends ColorSettingsPage {
  def getAttributeDescriptors: Array[AttributesDescriptor] = CamelAttributeKeyDescriptions.descriptors

  def getColorDescriptors: Array[ColorDescriptor] = ColorDescriptor.EMPTY_ARRAY

  def getDisplayName: String = LanguageConstants.languageName

  def getIcon: Icon = Icons.Camel

  /**
   * Creates a new CamelSyntaxHighlighter instance, which is not in anyway coupled
   * to a virtual file
   * @return A new instance of the CamelSyntaxHighlighter class
   */
  def getHighlighter: SyntaxHighlighter = new CamelSyntaxHighlighter

  def getDemoText: String = """${body.firstName} == ${body.firstName}
                              |    && ${body.isEmployeed}
                              |    && ${body.address.location} == 'London'
                              |    && ${body.order.items} < 5
                              |    && ${bodyAs(java.lang.String)} == 'Employer'
                              |    && ${bodyAs(not.found.Model)}
                              |    && ${headerAs("isValid", java.lang.String)}""".stripMargin.replaceAll("\r", "")

  def getAdditionalHighlightingTagToDescriptorMap: util.Map[String, TextAttributesKey] = null
}

/**
 * Defines the known set of configurable camel attribute key descriptions
 */
object CamelAttributeKeyDescriptions {

  /**
   * Defines the AttributesDescriptors - which pairs a piece of human readable text
   * with a given TextAttributesKey.
   *
   * Note this TextAttributesKey is used for syntax highlighting with both annotators
   * and within basic token highlighting.
   */
  val descriptors: Array[AttributesDescriptor] = Array(
    new AttributesDescriptor("Operator", CamelTextAttributeKeys.OPERATION_SIGN),
    new AttributesDescriptor("Square Braces", CamelTextAttributeKeys.SQUARE_BRACES),
    new AttributesDescriptor("Parenthesis", CamelTextAttributeKeys.PARENTHESES),
    new AttributesDescriptor("Text", CamelTextAttributeKeys.TEXT),
    new AttributesDescriptor("String", CamelTextAttributeKeys.STRING),
    new AttributesDescriptor("Number", CamelTextAttributeKeys.NUMBER),
    new AttributesDescriptor("Error", CamelTextAttributeKeys.BAD_CHARACTER),
    new AttributesDescriptor("Constant", CamelTextAttributeKeys.CONSTANT),
    new AttributesDescriptor("FQCN", CamelTextAttributeKeys.FQCN),
    new AttributesDescriptor("Camel Function", CamelTextAttributeKeys.CAMEL_FUNC)
  )
}