package foo.language.references.body

import com.intellij.psi._
import foo.language.generated.psi.{CamelFunctionArgs, CamelCamelFuncBody}
import com.intellij.util.ProcessingContext
import com.intellij.patterns.PlatformPatterns._
import foo.language.psi.impl.{SplitTextRange, ElementSplitter}

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
          case SplitTextRange("in", _, _) :: (body@SplitTextRange("body", _, _)) :: xs => createReferences(originalElement, body, xs)
          case SplitTextRange("out", _, _) :: (body@SplitTextRange("body", _, _)) :: xs => createReferences(originalElement, body, xs)
          case (body@SplitTextRange("body", _, _)) :: xs => createReferences(originalElement, body, xs)
          case _ => PsiReference.EMPTY_ARRAY
        }
      }

      /**
       * Creates and references the list of known PsiReferences for the camel body notation
       * @param element The parent element to register within
       * @param bodyRange The text range which is associated with a body reference
       * @param methodRanges The remainder split text contributions which should resolve
       *                            to methods
       * @return The created array of `PsiReferences[PsiElement]`s
       */
      def createReferences(element: PsiElement,
                           bodyRange: SplitTextRange,
                           methodRanges: List[SplitTextRange]): Array[PsiReference] = {
        val references = {
          val bodyReference = new CamelBodyReference(
            element,
            bodyRange.getTextRange
          )

          // Compute the method references with foldLeft
          val methodReferences = methodRanges.foldLeft(List[CamelMethodReference]())({
            case (prev, tuple) =>
                val newMethodReference = new CamelMethodReference(element, tuple.getTextRange, prev.headOption)
                newMethodReference :: prev
            })

          // Add both the available body and method references
          bodyReference :: methodReferences
        }
        references.toArray
      }
    })
  }
}
