package foo.language.formatting

import com.intellij.lang.{BracePair, PairedBraceMatcher}
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import foo.language.CamelTypes

/**
 * Provides the brace matching definition for Camel.
 *
 * It is this class that performs the role of automatically closing braces.
 */
class CamelBraceMatcher extends PairedBraceMatcher {
  /**
   * {@inheritDoc}
   */
  def getPairs: Array[BracePair] = CamelBraceMatcher.BracePair

  /**
   * {@inheritDoc}
   */
  def isPairedBracesAllowedBeforeType(lbraceType: IElementType, contextType: IElementType): Boolean = true

  /**
   * {@inheritDoc}
   */
  def getCodeConstructStart(file: PsiFile, openingBraceOffset: Int): Int = openingBraceOffset
}

object CamelBraceMatcher {
  val BracePair = Array[BracePair](new BracePair(CamelTypes.FUNC_BEGIN, CamelTypes.FUNC_END, true))
}