package foo.language.references.body

import com.intellij.psi._
import foo.language.generated.psi.{CamelFunctionArgs, CamelCamelFuncBody}
import com.intellij.util.ProcessingContext
import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PlatformPatterns._
import com.intellij.psi.impl.source.tree.injected.InjectedLanguageUtil
import foo.dom.DomFileAccessor
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlTag
import foo.eip.graph.EipGraphCreator
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.openapi.module.ModuleUtilCore
import foo.language.psi.impl.ElementSplitter
import foo.language.MethodConverter
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.util.xml.ElementPresentationManager
import scala.Some
import scala.util.Try

/**
 * Provides reference contribution for the apache camel language, using
 * the body accessor notation - ie ${body<caret>}
 */
class CamelBodyReferenceContributor extends PsiReferenceContributor {
  /**
   * Psi Patterns that this contribution applies to
   */
  val VARIABLE_ACCESS_PATTERN = {
    val element = psiElement(classOf[CamelCamelFuncBody])
    element
      .andNot(element.withChild(psiElement(classOf[CamelFunctionArgs])))
  }

  override def registerReferenceProviders(registrar: PsiReferenceRegistrar): Unit = {
    registrar.registerReferenceProvider(VARIABLE_ACCESS_PATTERN, new PsiReferenceProvider {
      override def getReferencesByElement(element: PsiElement, context: ProcessingContext): Array[PsiReference] = {
        val originalElement = element.getOriginalElement
        val text = originalElement.getText
        val splitSections = ElementSplitter.split(text).reverse

        // Match any section as long as it is one of ${in.body...} ${out.body...} or ${body...}
        splitSections match {
          case ("in", _, _) :: (body@("body", _, _)) :: xs => createReferences(originalElement, body, xs)
          case ("out", _, _) :: (body@("body", _, _)) :: xs => createReferences(originalElement, body, xs)
          case (body@("body", _, _)) :: xs => createReferences(originalElement, body, xs)
          case _ => PsiReference.EMPTY_ARRAY
        }
      }

      /**
       * Creates and references the list of known PsiReferences for the camel body notation
       * @param element The parent element to register within
       * @param bodyReference The text tuple which is associated with a body reference
       * @param methodContributions The remainder split text contributions which should resolve
       *                            to methods
       * @return The created array of `PsiReferences[PsiElement]`s
       */
      def createReferences(element: PsiElement,
                           bodyReference: (String, Int, Int),
                           methodContributions: List[(String, Int, Int)]): Array[PsiReference] = {
        val createTextRange = (tuple: (String, Int, Int)) => new TextRange(tuple._2, tuple._3)
        val references = {
          new CamelBodyReference(
            element,
            createTextRange(bodyReference)
          ) :: methodContributions.map(tuple => new CamelMethodReference(element, createTextRange(tuple)))
        }
        references.toArray
      }
    })
  }
}
