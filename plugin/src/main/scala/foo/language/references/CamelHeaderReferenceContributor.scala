package foo.language.references

import com.intellij.psi._
import foo.language.completion.VariableAccessorTest
import com.intellij.patterns.StandardPatterns._
import com.intellij.util.ProcessingContext
import com.intellij.patterns.PlatformPatterns
import foo.language.generated.psi.CamelVariableAccess
import com.intellij.openapi.util.TextRange
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.impl.source.tree.injected.InjectedLanguageUtil
import foo.dom.DomFileAccessor
import foo.dom.Model.ProcessorDefinition
import com.intellij.psi.util.{PsiUtilBase, PsiTreeUtil}
import com.intellij.psi.xml.XmlTag
import foo.eip.graph.EipGraphCreator

/**
 * Provides PsiReferences for apache camel's simple language when accessing header
 * information within the camel exchange
 */
class CamelHeaderReferenceContributor extends PsiReferenceContributor {
  /**
   * Pattern possibilities
   */
  val HEADER = VariableAccessorTest.afterVariableObject(List("header"), allowArrayAccess = true)
  val IN_HEADER = VariableAccessorTest.afterVariableObject(List("in", "header"), allowArrayAccess = true)
  val OUT_HEADER = VariableAccessorTest.afterVariableObject(List("out", "header"), allowArrayAccess = true)

  val HEADERS = VariableAccessorTest.afterVariableObject(List("headers"), allowArrayAccess = true)
  val IN_HEADERS = VariableAccessorTest.afterVariableObject(List("in", "headers"), allowArrayAccess = true)
  val OUT_HEADERS = VariableAccessorTest.afterVariableObject(List("out", "headers"), allowArrayAccess = true)

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
            val psiFile = InjectedLanguageUtil.getTopLevelFile(element)
            val caretPosition = PsiUtilBase.findEditor(psiFile).getCaretModel.getOffset

            Array(new CamelHeaderReference(identifier, caretPosition, new TextRange(identifier.getStartOffsetInParent, identifier.getTextLength)))
          } else {
            PsiReference.EMPTY_ARRAY
          }
          // Should never occur - IE Our psiElement match should be sufficient
         case _ => PsiReference.EMPTY_ARRAY
      }
    })
  }
}

class CamelHeaderReference(element: PsiElement, caretPosition: Int, range: TextRange)
  // Note this reference is a soft reference, ie if it doesn't resolve, it is *not* an error!
  extends PsiReferenceBase[PsiElement](element, range, false) {

  override def getVariants: Array[AnyRef] = {
    val project = element.getProject

    // Extract the relevent PsiFile in order to test if we are contained within an XmlFile
    val psiFile = InjectedLanguageUtil.getTopLevelFile(element)
    val domFileOption = DomFileAccessor.getBlueprintDomFile(project, psiFile)

    // If the XmlFile was found, create the EipGraph information to suggest the relevant information
    // Otherwise default to an empty map
    val availableHeaders:Map[String, ProcessorDefinition] = domFileOption match {
      case None => Map()
      case Some(domFile) => {
        // Calculate the caret offset, from the Editor of the topmost Psi file

        val xmlText = psiFile.findElementAt(caretPosition)

        // Extract the outter tag
        val getParentTag = (psiElement: PsiElement) => PsiTreeUtil.getParentOfType(psiElement, classOf[XmlTag], true)
        val simpleTag = getParentTag(xmlText)
        val outterTag = getParentTag(simpleTag)

        // Calculate graph and headers
        val graph = new EipGraphCreator().createEipGraph(domFile)
        val headers = graph.vertices.find(_.psiReference.getXmlTag == outterTag).map(_.semantics.headers)
        headers.getOrElse(Map())
      }
    }

    // Convert all available completions to a Lookup Builder that IJ understands
    availableHeaders.map(_._1)
      .map(LookupElementBuilder.create)
      .toArray
  }

  override def resolve(): PsiElement = null
}
