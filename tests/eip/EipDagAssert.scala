package eip

import foo.eip.graph.StaticGraphTypes._
import scala.xml.{PrettyPrinter, Elem}
import foo.DomFileAccessor
import foo.eip.graph.EipGraphCreator
import junit.framework.Assert
import com.intellij.testFramework.fixtures.{CodeInsightTestFixture, JavaCodeInsightTestFixture}

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
    val serializedXml = serialize(eipDag)

    // Assert Equals - Note, IntelliJ will provide a nice comparison tool in failure scenarios
    Assert.assertEquals("the given and expected EipDag should be equal", expectedDag, serializedXml)
  }

  /**
   * Creates the XML for the given EipDag
   * @param eipDag The EipDag to convert
   * @return The XML Node element
   */
  def createXml(eipDag: EipDAG) = {
    <eipDag>
      <vertices>
        {eipDag.vertices.map(vertex => <vertex id={vertex.id} eipType={vertex.eipType} text={vertex.text}/>)}
      </vertices>
      <edges>
        {eipDag.edges.map(edge => <edge source={edge.source.id} target={edge.target.id} edgeConnection={edge.edge}/>)}
      </edges>
    </eipDag>
  }

  /**
   * Pretty prints the given Xml Element
   * @param elem The root node to convert into a string
   * @return A pretty printed string of th egiven Xml Element
   */
  def prettyPrint(elem: Elem) = new PrettyPrinter(Integer.MAX_VALUE, 4).format(elem)

  /**
   * Serializes the given EipDag into a human-readable format
   * @param eipDag The given eipDag which contains the EIP abstraction
   * @return A, pretty printed, serialized XML version of the given EipDag
   */
  def serialize(eipDag: EipDAG) = (createXml _ andThen prettyPrint)(eipDag)
}
