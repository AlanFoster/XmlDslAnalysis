package foo.eip.abstractionModel.typePropagation

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import foo.{JavaJDK1_7TestBase, CommonTestClasses, TestBase}
import foo.dom.DomFileAccessor
import foo.intermediaterepresentation.converter.DomAbstractModelConverter
import foo.intermediaterepresentation.model.AbstractModelPrinter
import junit.framework.Assert
import foo.intermediaterepresentation.typeInference.TypePropagationTypeInference
import foo.language.typeChecking.InferredSimpleTypeChecker

/**
 * Tests to ensure that type propagation occurs as expected within the abstract model
 * classes
 */
class TypePropagationTests
  extends LightCodeInsightFixtureTestCase
  with TestBase
  with CommonTestClasses
  with JavaJDK1_7TestBase
  {

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
   * Ensure that the type information of children elements in when statements
   * shares the initial choice environment, and not the previous when's environment
   */
  def testSimpleChoiceBean() {
    doTest()
  }

  /**
   * Logging should not mutate the type environment
   */
  def testLoggingProcessor() {
    doTest()
  }

  /**
   * Ensure that bean information propagates as expected
   */
  def testBeanReference() {
    doTest()
  }

  /**
   * Ensure that bean information is unioned successfully
   */
  def testBeanReferenceUnion() {
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
   * Ensure that multiple set bodies in a row function as expected
   */
  def testMultipleSetBodySimple() {
    doTest()
  }

  /**
   * Ensure that RemoveHeader is handled as expected
   */
  def testRemoveHeader() {
    doTest()
  }

  def testOtherwise() {
   doTest()
  }

  /**
   * Ensures the test is valid - using the test name as the configuration file
   */
  def doTest() {
    // Ensure we have added all of the required testing
    loadAllCommon(myFixture)

    // Load and create the DOM representation
    val virtualFile = myFixture.configureByFile(s"/dom/${getTestName(false)}_dom.xml").getVirtualFile
    val loadedDomFile = DomFileAccessor.getBlueprintDomFile(myFixture.getProject, virtualFile).get

    // Load the expected output
    val expectedModel = myFixture.configureByFile(s"/typeInference/${getTestName(false)}_out.txt").getText

    // Create and pretty print the produced Eip DAG for the given DOM file
    val route = new DomAbstractModelConverter().convert(loadedDomFile)
    val routeWithSemantics = new TypePropagationTypeInference(new InferredSimpleTypeChecker()).performTypeInference(route)
    val serialized = AbstractModelPrinter.print(routeWithSemantics)

    // Assert Equals - Note, IntelliJ will provide a nice comparison tool in failure scenarios
    Assert.assertEquals("the given and expected EipDag should be equal", expectedModel, serialized)
  }
}
