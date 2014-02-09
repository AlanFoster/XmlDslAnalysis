package foo.language.completion

import com.intellij.patterns.PlatformPatterns._
import foo.language.generated.CamelTypes
import foo.language.generated.psi.{CamelCamelExpression, CamelFunctionArgs, CamelCamelFuncBody}
import foo.language.elements.CamelBaseElementType
import com.intellij.patterns.StandardPatterns._
import com.intellij.psi.PsiElement

/**
 * Define access to a series of well defined patterns within the camel language
 */
object Patterns {
  /**
   * A variable will be placed between ${..} however, <b>not</b> nested inside a
   * function call, ie ${bodyAs(...)}
   */
  val VARIABLE = {
    val element = psiElement(CamelTypes.IDENTIFIER)
    element
      .inside(classOf[CamelCamelFuncBody])
      .andNot(element.inside(classOf[CamelFunctionArgs]))
  }

  /**
   * A pattern which ensures that the current node is an identifier, with the previous identifier being
   * equal to variable name. For instance ${variableName.caret} matches however ${etc.variableName.caret} will not
   * @param variableName The leading identifier within the expression, ie `in`, `out`
   * @return the constructed pattern
   */
  def afterVariableObject(variableName: String) = {
    val lexeme = CamelBaseElementType.getName _
    psiElement(CamelTypes.IDENTIFIER)
      .withSuperParent(
        2, psiElement().withText(
          // Variable access must handle both a.b and a?.b - elvis operator
          or(
            string().startsWith(variableName + lexeme(CamelTypes.DOT)),
            string().startsWith(variableName + lexeme(CamelTypes.QUESTION_MARK) + lexeme(CamelTypes.DOT)))
        )
      )
      .withSuperParent(3, psiElement(CamelTypes.CAMEL_FUNC_BODY))
  }

  /**
   * An 'in' variable, will be only available after `in.&gt;caret&lt;`
   */
  val IN_VARIABLE = afterVariableObject("in")
  /**
   * Matches the 'out' variable access
   */
  val OUT_VARIABLE = afterVariableObject("out")

  /**
   * Matches only exception variable access
   */
  val EXCEPTION_VARIABLE = afterVariableObject("exception")

  /**
   * Successfully matches a used operator
   */
  val OPERATOR = psiElement().withParent(classOf[CamelCamelExpression])

  /**
   * A pattern which will successfully match in an empty camel func body,
   * other than itself
   */
  val EMPTY_VARIABLE =
    VARIABLE
      .withSuperParent(2,
        psiElement(classOf[CamelCamelFuncBody])
          .withChildren(
            collection(classOf[PsiElement]).size(1)
          )
      )
}
