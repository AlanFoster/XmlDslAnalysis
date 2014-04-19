package foo.eip.abstractionModel.converters

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import foo.TestBase
import foo.dom.DomFileAccessor
import junit.framework.Assert
import foo.intermediaterepresentation.converter.DomAbstractModelConverter
import foo.intermediaterepresentation.model.AbstractModelPrinter

/**
 * Tests to ensure that the DOM can successfully be converted into an intermediate representation
 */
class DomAbstractionModelTests
  extends LightCodeInsightFixtureTestCase
  with TestBase {

  /**
   * {@inheritdoc}
   */
  override def getTestDataPath: String = testDataMapper("/foo/eip/abstractionModel/")

  /**
   * Tests an empty route
   */
  def testEmptyRoute() {
    doTest()
  }

  /**
   * Performs the expected assertion between the loaded dom file and the created Eip DAG
   */
  def testSimpleRoute() {
    doTest()
  }

  /**
   * Attempts to pipeline all currently known processors
   */
  def testPipelineHeaders() {
    doTest()
  }

  /**
   * Attempts to create the abstract representation of a simple choice
   */
  def testSimpleChoice() {
    doTest()
  }

  /**
   * Ensure that bean information is unioned successfully
   */
  def testBeanReferenceUnion() {
    doTest()
  }

  /**
   * Ensure that bean information propagates as expected
   */
  def testBeanReference() {
    doTest()
  }

  /**
   * Test a more complex scenario with arbitarily nested components etc
   */
  def testComplexNestedChoice() {
    doTest()
  }

  /**
   * Ensure that setBody expression works as expected
   */
  def testSetBodySimple() {
    doTest()
  }

  /**
   * Ensure that RemoveHeader is handled as expected
   */
  def testRemoveHeader() {
    doTest()
  }

  /**
   * Logging processors should be accepted
   */
  def testLoggingProcessor() {
    doTest()
  }

  /**
   * Ensures the test is valid - using the test name as the configuration file
   */
  // TODO Could merge with the EipDagAssert class
  def doTest() {
    // Load and create the DOM representation
    val virtualFile = myFixture.configureByFile(s"/dom/${getTestName(false)}_dom.xml").getVirtualFile
    val loadedDomFile = DomFileAccessor.getBlueprintDomFile(myFixture.getProject, virtualFile).get

    // Load the expected output
    val expectedModel = myFixture.configureByFile(s"/converter/${getTestName(false)}_out.txt").getText

    // Create and pretty print the produced Eip DAG for the given DOM file
    val route = new DomAbstractModelConverter().convert(loadedDomFile)
    val serialized = AbstractModelPrinter.print(route)

    // Assert Equals - Note, IntelliJ will provide a nice comparison tool in failure scenarios
    Assert.assertEquals("the given and expected EipDag should be equal", expectedModel, serialized)
  }
}
