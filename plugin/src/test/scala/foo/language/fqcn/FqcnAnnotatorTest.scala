package foo.language.fqcn

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import foo.JavaJDK1_7TestBase

/**
 * Tests to ensure that unresolved references are highlighted as red
 */
class FqcnAnnotatorTest
  extends LightCodeInsightFixtureTestCase
  with JavaJDK1_7TestBase {

  override def getTestDataPath: String = testDataMapper("/foo/language/fqcn/annotator")

  /**
   * Ensure highlighting is correct when the last class/package element does not exist
   */
  def testEndTokenFQCNUnresolved() {
    doTest()
  }

  /**
   * Ensure highlighting is correct when the second last class/package element does not exist
   */
  def testSecondEndTokenFQCNUnresolved() {
    doTest()
  }

  /**
   * Performs the actual unit test, using the test name convention as always
   */
  def doTest(){
    myFixture.configureByFile(s"${getTestName(false)}.Camel")
    myFixture.checkHighlighting(false, false, true)
  }
}
