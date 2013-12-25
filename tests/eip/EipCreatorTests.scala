package eip

import impl.TestBase
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import foo.eip.graph.EipGraphCreator
import foo.DomFileAccessor
import foo.eip.graph.StaticGraphTypes.EipDAG
import scala.xml.{Elem, PrettyPrinter}
import junit.framework.Assert

/**
 * Tests for ensuring the validity of the EipCreator interface which converts a Dom tree into
 * a more abstracted EIP Dag
 */
class EipCreatorTests
  extends LightCodeInsightFixtureTestCase
  with TestBase{

  override def getTestDataPath: String = testDataMapper("/eip/serialization")

  /**
   * Performs the expected assertion between the loaded dom file and the created Eip DAG
   */
  def testSimpleRoute() {
    // Load and create the DOM representation
    val virtualFile = myFixture.configureByFile(s"${getTestName(false)}_dom.xml").getVirtualFile
    val loadedDomFile = DomFileAccessor.getBlueprintDomFile(getProject, virtualFile).get

    // Load the expected EIP DAG, represented as Xml
    val expectedDag = myFixture.configureByFile(s"${getTestName(false)}_eip.xml").getText

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
        { eipDag.vertices.map(vertex => <vertex id={vertex.id} eipType={vertex.eipType}  text={vertex.text} />) }
      </vertices>
      <edges>
        {eipDag.edges.map(edge => <edge source={edge.source.id} target={edge.target.id} edgeConnection={edge.edge} />) }
      </edges>
    </eipDag>
  }

  /**
   * Pretty prints the given Xml Element
   * @param elem The root node to convert into a string
   * @return A pretty printed string of th egiven Xml Element
   */
  def prettyPrint(elem: Elem) = new PrettyPrinter(80, 4).format(elem)

  /**
   * Serializes the given EipDag into a human-readable format
   * @param eipDag The given eipDag which contains the EIP abstraction
   * @return A, pretty printed, serialized XML version of the given EipDag
   */
  def serialize(eipDag: EipDAG) = (createXml _ andThen prettyPrint)(eipDag)
}
