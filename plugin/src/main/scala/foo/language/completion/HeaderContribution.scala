package foo.language.completion

import com.intellij.codeInsight.completion._
import com.intellij.util.ProcessingContext
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns._
import foo.language.generated.CamelTypes
import foo.language.generated.psi.{CamelCamelExpression, CamelFunctionArgs, CamelCamelFuncBody}
import foo.language.elements.CamelBaseElementType
import com.intellij.patterns.StandardPatterns._
import com.intellij.psi.{PsiFile, PsiDocumentManager, PsiElement}
import Patterns._
import com.intellij.util.xml.{DomUtil, ConvertContext}
import foo.dom.Model.ProcessorDefinition
import foo.dom.DomFileAccessor
import foo.eip.graph.EipGraphCreator
import com.intellij.psi.impl.source.tree.injected.InjectedLanguageUtil
import com.intellij.xml.util.XmlUtil
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.impl.file.PsiFileImplUtil
import com.intellij.psi.xml.{XmlTag, XmlFile}
import com.intellij.patterns.XmlPatterns

/**
 * Provides contribution for headers within the relevant places of the camel language
 */
class HeaderContribution extends CompletionContributor {
  val HEADER = VariableAccessorTest.afterVariableObject(List("header"), true)
  val IN_HEADER = VariableAccessorTest.afterVariableObject(List("in", "header"), true)
  val OUT_HEADER = VariableAccessorTest.afterVariableObject(List("out", "header"), true)

  val HEADERS = VariableAccessorTest.afterVariableObject(List("headers"), true)
  val IN_HEADERS = VariableAccessorTest.afterVariableObject(List("in", "headers"), true)
  val OUT_HEADERS = VariableAccessorTest.afterVariableObject(List("out", "headers"), true)

 /* val HEADER_AS =
    psiElement(CamelTypes.STRING)*/

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

      //HEADER_AS
    )

  extend(CompletionType.BASIC,
    unionPattern,
    new CompletionProvider[CompletionParameters]() {
      /**
       * Provides the completions available under the given context
       * @param parameters
       * @param context
       * @param result
       */
      def addCompletions(parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet) {
        val (camelEditor, project) = (parameters.getEditor, parameters.getEditor.getProject)
        val xmlEditor = InjectedLanguageUtil.getTopLevelEditor(camelEditor)

        // Extract the relevent PsiFile in order to test if we are contained within an XmlFile
        val xmlDocument = xmlEditor.getDocument
        val psiFile: PsiFile = PsiDocumentManager.getInstance(project).getPsiFile(xmlDocument)
        val domFileOption = DomFileAccessor.getBlueprintDomFile(project, psiFile)

        // If the XmlFile was found, create the EipGraph information to suggest the relevent information
        // Otherwise default to an empty map
        val availableHeaders:Map[String, ProcessorDefinition] = domFileOption match {
          case None => Map()
          case Some(domFile) => {
            val caretPosition = xmlEditor.getCaretModel.getOffset

            val xmlText = psiFile.findElementAt(caretPosition)

            val getParentTag = (psiElement: PsiElement) => PsiTreeUtil.getParentOfType(psiElement, classOf[XmlTag], true)

            val simpleTag = getParentTag(xmlText)
            val outterTag = getParentTag(simpleTag)

            val graph = new EipGraphCreator().createEipGraph(domFile)
            val headers = graph.vertices.find(_.psiReference.getXmlTag == outterTag).map(_.semantics.headers)
            headers.getOrElse(Map())
          }
        }

        // Convert all available completions to a Lookup Builder that IJ understands
        availableHeaders.map(_._1)
          .map(LookupElementBuilder.create)
          // Register each lookup element
          .foreach(result.addElement)
      }
    }
  )

}
