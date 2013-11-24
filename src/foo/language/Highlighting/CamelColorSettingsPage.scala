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
  def getAttributeDescriptors: Array[AttributesDescriptor] = CamelSyntaxHighlighter.descriptors

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
                              |    && ${headerAs("isValid", java.lang.String)}""".stripMargin.replaceAll("\r", "")

  def getAdditionalHighlightingTagToDescriptorMap: util.Map[String, TextAttributesKey] = null
}
