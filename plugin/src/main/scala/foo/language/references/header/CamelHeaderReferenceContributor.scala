package foo.language.references.header

import com.intellij.psi._
import foo.language.completion.VariableAccessorConstructor
import com.intellij.patterns.StandardPatterns._
import com.intellij.util.ProcessingContext
import com.intellij.patterns.PlatformPatterns
import foo.language.generated.psi.CamelVariableAccess
import com.intellij.openapi.util.TextRange
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.impl.source.tree.injected.InjectedLanguageUtil
import foo.dom.DomFileAccessor
import foo.dom.Model.ProcessorDefinition
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlTag
import foo.eip.graph.EipGraphCreator
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.util.xml.ElementPresentationManager
import foo.language.Core.CamelFileType

/**
 * Provides PsiReferences for apache camel's simple language when accessing header
 * information within the camel exchange
 */
class CamelHeaderReferenceContributor extends PsiReferenceContributor {
  /**
   * Pattern possibilities
   */
  val HEADER = VariableAccessorConstructor.afterVariableObject(List("header"), allowArrayAccess = true)
  val IN_HEADER = VariableAccessorConstructor.afterVariableObject(List("in", "header"), allowArrayAccess = true)
  val OUT_HEADER = VariableAccessorConstructor.afterVariableObject(List("out", "header"), allowArrayAccess = true)

  val HEADERS = VariableAccessorConstructor.afterVariableObject(List("headers"), allowArrayAccess = true)
  val IN_HEADERS = VariableAccessorConstructor.afterVariableObject(List("in", "headers"), allowArrayAccess = true)
  val OUT_HEADERS = VariableAccessorConstructor.afterVariableObject(List("out", "headers"), allowArrayAccess = true)

  /**
   * Union all possible header patterns for contribution
   */
  val unionPattern =
    or(
      HEADER,
      IN_HEADER,
      OUT_HEADER,

      HEADERS,
      OUT_HEADERS,
      IN_HEADERS
    )

  /**
   * Registers with the given registrar the acceptable patterns and PsiReference contributions available
   * for header definitions
   * @param registrar The registrar in which to register reference providers with
   */
  override def registerReferenceProviders(registrar: PsiReferenceRegistrar): Unit = {
    registrar.registerReferenceProvider(PlatformPatterns.psiElement(classOf[CamelVariableAccess]), new PsiReferenceProvider {
      override def getReferencesByElement(element: PsiElement, context: ProcessingContext): Array[PsiReference] = element match {
        case access: CamelVariableAccess =>
          val identifier = access.getIdentifier
          val isMatch = unionPattern.accepts(identifier, context)

          if(isMatch) {
            Array(new CamelHeaderReference(identifier, new TextRange(identifier.getStartOffsetInParent, identifier.getTextLength)))
          } else {
            PsiReference.EMPTY_ARRAY
          }
          // Should never occur - IE Our psiElement match should be sufficient
         case _ => PsiReference.EMPTY_ARRAY
      }
    })
  }
}
