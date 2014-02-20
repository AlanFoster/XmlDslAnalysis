package foo.language.formatting

import com.intellij.lang.{BracePair, PairedBraceMatcher}
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import foo.language.generated.CamelTypes

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

/**
 * Defines the BracePair pairs within the apache camel simple language
 */
object CamelBraceMatcher {
  val BracePair = Array[BracePair](
    new BracePair(CamelTypes.FUNC_BEGIN, CamelTypes.FUNC_END, true),
    new BracePair(CamelTypes.LEFT_SQUARE_BRACE, CamelTypes.RIGHT_SQUARE_BRACE, true),
    new BracePair(CamelTypes.LEFT_PAREN, CamelTypes.RIGHT_PAREN, true)
  )
}