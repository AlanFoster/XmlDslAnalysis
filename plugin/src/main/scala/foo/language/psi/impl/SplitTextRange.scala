package foo.language.psi.impl

import com.intellij.openapi.util.TextRange

/**
 * Represents a concrete version of a text range within a piece of text.
 * Useful when splitting text in which you wish to have access to the
 * initial text, for instance during reference contribution.
 */
case class SplitTextRange(text: String, start: Int, end: Int) {
  /**
   * Provides an accessor to IJ's TextRange API
   * @return The IJ Api TextRange
   */
  def getTextRange = new TextRange(start, end)
}
