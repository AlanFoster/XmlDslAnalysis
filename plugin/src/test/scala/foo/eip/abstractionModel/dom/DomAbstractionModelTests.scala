package foo.eip.eipCreator

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import foo.eip.serializers.CoreEipDagSerializer
import foo.eip.EipDagAssert
import foo.TestBase
import foo.dom.DomFileAccessor
import foo.eip.graph.EipGraphCreator
import junit.framework.Assert
import foo.eip.converter.DomConverter
import foo.eip.model.AbstractModelPrinter

/**
 * Tests to ensure that the DOM can successfully be converted into an intermediate representation
 */
class DomAbstractionModelTests
  extends LightCodeInsightFixtureTestCase
  with TestBase {

  /**
   * {@inheritdoc}
   */
  override def getTestDataPath: String = testDataMapper("/foo/eip/abstractionModel/dom/")

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
   * Ensures the test is valid - using the test name as the configuration file
   */
  // TODO Could merge with the EipDagAssert class
  def doTest() {
    // Load and create the DOM representation
    val virtualFile = myFixture.configureByFile(s"${getTestName(false)}_dom.xml").getVirtualFile
    val loadedDomFile = DomFileAccessor.getBlueprintDomFile(myFixture.getProject, virtualFile).get

    // Load the expected output
    val expectedModel = myFixture.configureByFile(s"${getTestName(false)}_out.txt").getText

    // Create and pretty print the produced Eip DAG for the given DOM file
    val route = new DomConverter().createAbstraction(loadedDomFile)
    val serialized = AbstractModelPrinter.print(route)

    // Assert Equals - Note, IntelliJ will provide a nice comparison tool in failure scenarios
    Assert.assertEquals("the given and expected EipDag should be equal", expectedModel, serialized)
  }
}
