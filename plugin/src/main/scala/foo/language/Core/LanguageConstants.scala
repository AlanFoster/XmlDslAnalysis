package foo.language.Core

import foo.Icons

/**
 * Represents the known language constants.
 */
object LanguageConstants {
  /**
   * @return The icon associated with this language. This icon will be used when the
   *         language is viewed individually, and within the GUI menus which
   *         reference this language.
   */
  def icon = Icons.Camel

  /**
   * @return The extension associated with the file type
   */
  def extension = "camel"

  /**
   * @return The human readable description of this language
   */
  def description = "Camel Language Suppport"

  /**
   * @return The referencable name that is associated with this language
   */
  def languageName = "Camel"
}
