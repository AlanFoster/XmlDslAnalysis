package foo.language.Core

import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

/**
 * Represents the FileType associated with the Apache Camel Language.
 * This class allows for us to have access to our singleton instance
 * of the CamelLanguage
 */
object CamelFileType extends LanguageFileType(CamelLanguage) {
  def getName: String = LanguageConstants.languageName
  def getDescription: String = LanguageConstants.description
  def getDefaultExtension: String = LanguageConstants.extensions
  def getIcon: Icon = LanguageConstants.icon
}


