package eip

import foo.eip.graph.StaticGraphTypes._
import scala.xml.{PrettyPrinter, Elem}
import foo.DomFileAccessor
import foo.eip.graph.EipGraphCreator
import junit.framework.Assert
import com.intellij.testFramework.fixtures.{CodeInsightTestFixture, JavaCodeInsightTestFixture}
import foo.eip.serializers.EipDagSerializer

/**
 * EipDag asserting methods for ensuring that the EipDAGs are represented as expected
 */
object EipDagAssert {

  /**
   * Runs the EipDagAssert using the given credentials
   * @param fixture The test fixture
   * @param testName The current test name.
   *                 The commonly used convention of test name representing the given test scenario is used.
   *                 For instance -
   *                 Loaded Dom File = ${testName}_dom.xml
   *                 Expected EIP Dag = ${testName}_eip.xml
   */
  def doTest(fixture: CodeInsightTestFixture, testName: String) {
    // Load and create the DOM representation
    val virtualFile = fixture.configureByFile(s"${testName}_dom.xml").getVirtualFile
    val loadedDomFile = DomFileAccessor.getBlueprintDomFile(fixture.getProject, virtualFile).get

    // Load the expected EIP DAG, represented as Xml
    val expectedDag = fixture.configureByFile(s"${testName}_eip.xml").getText

    // Create and pretty print the produced Eip DAG for the given DOM file
    val eipDag = new EipGraphCreator().createEipGraph(loadedDomFile)
    val serializedXml = new EipDagSerializer().serialize(eipDag)

    // Assert Equals - Note, IntelliJ will provide a nice comparison tool in failure scenarios
    Assert.assertEquals("the given and expected EipDag should be equal", expectedDag, serializedXml)
  }
}
