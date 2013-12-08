package foo.language.references

import com.intellij.psi._
import com.intellij.codeInsight.lookup.{LookupElement, LookupElementBuilder}
import com.intellij.openapi.util.TextRange

class CamelFQCNReference(element: PsiElement)
  extends PsiReferenceBase[PsiElement](element, new TextRange(0, element.getTextLength))
  with PsiPolyVariantReference {

  def multiResolve(incompleteCode: Boolean): Array[ResolveResult] = {
    Array[ResolveResult]()
  }

  def getVariants: Array[AnyRef] = {
    List[LookupElement](
      LookupElementBuilder.create("java.lang.String"),
      LookupElementBuilder.create("java.lang.Integer"),
      LookupElementBuilder.create("java.lang.Object")
    ).toArray
  }

  def resolve(): PsiElement = {
    val results = multiResolve(incompleteCode = false)
    if(results.length == 1) results(0).getElement
    else null
  }
}
