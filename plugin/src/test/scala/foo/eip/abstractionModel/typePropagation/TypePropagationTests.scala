package foo.eip.abstractionModel.typePropagation

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import foo.TestBase
import foo.dom.DomFileAccessor
import foo.eip.converter.DomAbstractModelConverter
import foo.eip.model.AbstractModelPrinter
import junit.framework.Assert
import foo.eip.typeInference.DataFlowTypeInference

/**
 * Tests to ensure that type propagation occurs as expected within the abstract model
 * classes
 */
class TypePropagationTests
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
  def ignoretestPipelineHeaders() {
    doTest()
  }

  /**
   * Attempts to create the abstract representation of a simple choice
   */
  def ignoretestSimpleChoice() {
    doTest()
  }

  /**
   * Ensure that bean information propagates as expected
   */
  def ignoretestBeanReference() {
    doTest()
  }

  /**
   * Test a more complex scenario with arbitarily nested components etc
   */
  def ignoretestComplexNestedChoice() {
    doTest()
  }

  /**
   * Ensure that setBody expression works as expected
   */
  def testSetBodySimple() {
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
    val expectedModel = myFixture.configureByFile(s"/typeInference/${getTestName(false)}_out.txt").getText

    // Create and pretty print the produced Eip DAG for the given DOM file
    val route = new DomAbstractModelConverter().createAbstraction(loadedDomFile)
    val routeWithSemantics = new DataFlowTypeInference().performTypeInference(route)
    val serialized = AbstractModelPrinter.print(routeWithSemantics)

    // Assert Equals - Note, IntelliJ will provide a nice comparison tool in failure scenarios
    Assert.assertEquals("the given and expected EipDag should be equal", expectedModel, serialized)
  }


}