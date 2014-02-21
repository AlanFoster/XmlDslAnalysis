package foo

import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture
import com.intellij.psi.PsiElement
import scala.util.{Failure, Success, Try}


/**
 * Represents a rich test fixture which contains additional methods which
 * would be useful to use during testing scenarios
 */
object RichTestFixture {
  implicit def toRichTestFixture(testFixture: JavaCodeInsightTestFixture) =
    new RichTestFixture(testFixture)

  /**
   * Creates a new instance of the RichTestFixture
   * @param testFixture The 'old' test fixture
   */
  class RichTestFixture(testFixture: JavaCodeInsightTestFixture) {
    /**
     * Attempts to extract the element at the given caret position
     * @return None if there is no valid reference within the given caret position
     *         Otherwise it will provide Some(reference)
     */
    def getElementAtCaretSafe: Option[PsiElement] = {
      Try(testFixture.getElementAtCaret) match {
        case Success(elem) =>
            Some(elem)
        // Swallow the initially known test fixture error, if it is valid
        // to do so
        case Failure(error)
          if error.isInstanceOf[AssertionError]
          && error.getMessage.contains("element not found in file") =>
            None
        // Propagate the initial exception if it isn't a known assert error
        case Failure(error) =>
          throw error
      }
    }
  }
}