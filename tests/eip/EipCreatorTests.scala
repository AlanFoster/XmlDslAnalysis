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
    EipDagAssert.doTest(myFixture, getTestName(false))
  }
}
