package foo.language.completion

import foo.language.elements.CamelBaseElementType
import com.intellij.patterns.PlatformPatterns._
import foo.language.generated.CamelTypes
import com.intellij.patterns.StandardPatterns._
import com.intellij.psi.PsiElement
import com.intellij.patterns.PsiElementPattern
import scala.annotation.tailrec

/**
 * Constructs and creates the required patterns for variable contribution
 * within the Apache Camel Simple language
 */
object VariableAccessorConstructor {

  /**
   * Creates a new PsiPattern which matches the given variable name prefixes
   * @param variableNames The required variable name prefixes, for instance List("in") etc
   * @param allowArrayAccess Defines whether or not object access allows array notation access
   * @return The constructed PsiPattern
   */
  def afterVariableObject(variableNames: List[String], allowArrayAccess: Boolean = false) = {

    val reversedNames = variableNames.reverse

    val identifierPattern = psiElement(CamelTypes.IDENTIFIER)
    constructParentPattern(identifierPattern, reversedNames, 2, allowArrayAccess)
  }

  /**
   * Recursively constructs the parent pattern
   * @param parent The associated parent PsiElement pattern
   * @param reverseElems The remaining elements, which are reversed, as the tree structure
   *                     is backwards when creating the elements
   * @param depth The current depth of traversal, for instance how many super parents to
   *              match
   * @param allowArrayAccess Defines whether or not object access allows array notation access
   * @return The constructed PsiPattern
   */
  @tailrec
  def constructParentPattern(
                             parent: PsiElementPattern.Capture[PsiElement],
                             reverseElems: List[String],
                             depth: Int,
                             allowArrayAccess: Boolean):PsiElementPattern.Capture[PsiElement] = {
    reverseElems match {
      case (variableName :: xs) =>
        constructParentPattern(parent.withSuperParent(depth, getDefinition(variableName, allowArrayAccess)), xs, depth + 1, allowArrayAccess)
      case Nil =>
        parent
          .withSuperParent(depth, psiElement(CamelTypes.CAMEL_FUNC_BODY))
    }
  }

  /**
   * Provides the PsiPattern definition which will successfully match a given variable within
   * the apache camel language
   * @param variableName The variable name to match
   * @param allowArrayAccess Defines whether or not object access allows array notation access
   * @return The constructed PsiPattern
   */
  def getDefinition(variableName: String, allowArrayAccess: Boolean) = {
    val lexeme = CamelBaseElementType.getName _
    val matchingText = {
      // Variable access must handle both a.b and a?.b - elvis operator
      val dot = string().startsWith(variableName + lexeme(CamelTypes.DOT))
      val elvis = string().startsWith(variableName + lexeme(CamelTypes.QUESTION_MARK) + lexeme(CamelTypes.DOT))
      // Some accesses allow array notation
      val array = string().startsWith(variableName + lexeme(CamelTypes.LEFT_SQUARE_BRACE))

      if(!allowArrayAccess) or(dot, elvis) else or(dot, elvis, array)
    }
    psiElement().withText(matchingText)
  }

}
