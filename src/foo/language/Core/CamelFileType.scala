package foo.language.Core

import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

/**
 * Represents the FileType associated with the Apache Camel Language.
 * This class allows for us to have access to our singleton instance
 * of the CamelLanguage.
 *
 * This FileType can be registered by providing an implementation of
 * the FileTypeFactory interface, which should be registered as expected
 * under the plugin.xml file as a fileTypeFactory extension point element.
 *
 * This file type can be searched for using the standard searching mechanisms.
 */
object CamelFileType extends LanguageFileType(CamelLanguage) {
  /**
   * The unique file name associated with the system
   * @return The unique file name
   */
  def getName: String = LanguageConstants.languageName

  /**
   * The description of the language file type
   * @return The description of the language file type
   */
  def getDescription: String = LanguageConstants.description

  /**
   * Used as the key requirement of uniquely identifying a File to a specific
   * file type
   * @return The unique default file extension
   */
  def getDefaultExtension: String = LanguageConstants.extensions

  /**
   * Provide the ICON which can be used within the file when it is opened.
   * This is useful for debugging purposes.
   * @return The icon to show when the given file type is opened.
   *         Note it is possible for this FileType to be injected into an
   *         arbitrary piece of text also.
   */
  def getIcon: Icon = LanguageConstants.icon
}


