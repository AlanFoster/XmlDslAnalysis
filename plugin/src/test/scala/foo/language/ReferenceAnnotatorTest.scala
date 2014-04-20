package foo.language

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import foo.{CommonTestClasses, JavaJDK1_7TestBase}
import com.intellij.codeInsight.daemon.impl.analysis.XmlUnusedNamespaceInspection

/**
 * Tests to ensure that the apache camel simple language is highlighted as expected
 * when references are resolved/not resolved
 */
class ReferenceAnnotatorTest
  extends LightCodeInsightFixtureTestCase
  with JavaJDK1_7TestBase
  with CommonTestClasses {

  /**
   * {@inheritdoc}
   */
  override def getTestDataPath: String = testDataMapper("/foo/language/annotators/references")

  /**
   * An unresolved body with no inferred information should be highlighted successfully
   */
  def testUnresolvedBody() {
    doTest()
  }

  /**
   * Ensure a method which does not result, however has multiple possible bodies, is
   * highlighted as valid
   */
  def testMultipleBodyTypes() {
    doTest()
  }

  /**
   * Test a method body which does not resolve successfully
   */
  def testInvalidResultTypeAttributeBodyAccessInvalid() {
    doTest()
  }

  /**
   * Performs the test by placing the XML file into memory and checking
   * the highlighting runs as expected
   */
  def doTest() {
    loadAllCommon(myFixture)
    myFixture.configureByFile(s"${getTestName(false)}.xml")
    myFixture.checkHighlighting(false, false, true)
  }
}
