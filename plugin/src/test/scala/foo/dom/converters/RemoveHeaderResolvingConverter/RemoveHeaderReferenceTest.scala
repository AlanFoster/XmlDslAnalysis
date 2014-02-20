package foo.dom.converters.RemoveHeaderResolvingConverter

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import foo.TestBase
import com.intellij.psi.xml.{XmlAttribute, XmlFile}
import com.intellij.patterns.XmlPatterns._
import com.intellij.psi.{PsiFile, PsiElement, XmlElementVisitor, PsiRecursiveElementVisitor}
import junit.framework.Assert
import com.intellij.patterns.StandardPatterns._
import com.intellij.openapi.project.Project
import scala.util.Try

/**
 * Ensure that elements are resolved as expected when ctrl+clicking elements.
 * Note - All tests must follow the convention of having a single element defined
 * with an id of 'expectedReference'. If this is not found then it is assumed the
 * given reference does not exist.
 */
class RemoveHeaderReferenceTest
  extends LightCodeInsightFixtureTestCase
  with TestBase {

  /**
   * {@inheritdoc}
   */
  override def getTestDataPath: String = testDataMapper("/foo/dom/converters/RemoveHeaderResolvingConverter/Resolving")

  /**
   * Denotes the id of the element which should be resoled to.
   * This should be marked within the test XML
   */
  val ExpectedReferenceId = "expectedReference"

  /**
   * Ensure that the resolved header is the last defined header within the pipeline
   */
  def testPipelineLastResolves() {
    doTest()
  }

  /**
   * Ensure that the element can reference a single previous header as expected
   */
  def testSingleHeaderResolve() {
    doTest()
  }

  /**
   * Ensure that when there exists no reference that the node will simply evaluate to null
   */
  def testNoMatchingHeader() {
    doTest()
  }

  /**
   * Performs the given test by ensuring that the node is referenced as expected.
   * Note - All tests must follow the convention of having a single element defined
   * with an id of 'expectedReference'. If this is not found then it is assumed the
   * given reference does not exist.
   */
  def doTest() {
    val testName = s"${getTestName(false)}.xml"
    val psiFile = myFixture.configureByFile(testName)

    val expectedReference = findExpectedPsiElement(getProject, psiFile)
    val actualReference = Try(myFixture.getElementAtCaret).getOrElse(null)

    Assert.assertEquals("the resolved node within the resolving converter should be as expected", expectedReference, actualReference)
  }

  /**
   * This method attempts fo find a the matching expected reference, which follows the
   * expected ExpectedReferenceId id
   * @param psiFile The psi file to check against
   */
  private def findExpectedPsiElement(project: Project, psiFile: PsiFile): PsiElement = {
    // Define our pattern to search for
    val expectedReferencePattern =
      xmlTag()
        .withAttributeValue("id", ExpectedReferenceId)

    // TODO Surely this type of code is defined in a hidden utility somewhere...
    var expectedReference: PsiElement = null
    psiFile.accept(new PsiRecursiveElementVisitor() {
      override def visitElement(element: PsiElement): Unit = {
        super.visitElement(element)
        if (expectedReferencePattern.accepts(element)) {
          expectedReference = element
        }
      }
    })
    expectedReference
  }
}
