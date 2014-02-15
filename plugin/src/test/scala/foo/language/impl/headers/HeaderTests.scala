package foo.language.impl.headers

import foo.{JavaJDK1_7TestBase, TestBase}
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase

/**
 * Represents the base class for tests which are intended to be ran
 * to test the header language within the apache camel simple language
 */
class HeaderTests
  extends LightCodeInsightFixtureTestCase
  with JavaJDK1_7TestBase
  with TestBase {

  /**
   * {@inheritdoc}
   */
  override def getTestDataPath: String = testDataMapper("/foo/language/contribution/headers")

  /**
   * Test scenario descriptions - which contain the file name and assocaited expected headers
   * given the context
   */
  case class TestContext(testFileName: Option[String], expectedHeaders: List[String]) {
    /**
     * Creates a new TestContext in which no headers are expected, IE used in
     * the scenario of no expected contribution
     */
    def emptyContribution = TestContext(testFileName, List())
  }
  val ComplexHeaders = TestContext(Some("ComplexHeaders.xml"), ('a' to 'l').map(_.toString).toList)
  val EmptyContext = TestContext(Some("EmptyContext.xml"), List("a"))
  val Standalone = TestContext(None, List())
}
