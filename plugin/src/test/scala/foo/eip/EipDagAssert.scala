package foo.eip

import junit.framework.Assert
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import foo.tooling.serializers.EipDagSerializer
import foo.dom.DomFileAccessor
import foo.intermediaterepresentation.converter.DomAbstractModelConverter
import foo.intermediaterepresentation.typeInference.DataFlowTypeInference
import foo.tooling.graphing.EipGraphCreator

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
  def doTest(fixture: CodeInsightTestFixture, testName: String, serializer: EipDagSerializer) {
    // Load and create the DOM representation
    val virtualFile = fixture.configureByFile(s"${testName}_dom.xml").getVirtualFile
    val loadedDomFile = DomFileAccessor.getBlueprintDomFile(fixture.getProject, virtualFile).get

    // Load the expected EIP DAG, represented as Xml
    val expectedDag = fixture.configureByFile(s"${testName}_eip.xml").getText

    // Instantiate the methods of creating an abstract dom model and semantic information for later DI
    val modelConverter = new DomAbstractModelConverter()
    val dataFlowInference = new DataFlowTypeInference()

    // Create and pretty print the produced Eip DAG for the given DOM file
    val eipDag = new EipGraphCreator()
      .createEipGraph(modelConverter, dataFlowInference)(loadedDomFile)
    val serializedXml = serializer.serialize(eipDag)

    // Assert Equals - Note, IntelliJ will provide a nice comparison tool in failure scenarios
    Assert.assertEquals("the given and expected EipDag should be equal", expectedDag, serializedXml)
  }
}
