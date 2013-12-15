package impl.fqcn

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import impl.{CommonTestClasses, JavaJDK1_7TestBase}

/**
 * Tests to ensure that elements can be renamed as expected
 */
class RenameTest
  extends LightCodeInsightFixtureTestCase
  with JavaJDK1_7TestBase
  with CommonTestClasses {

  override def getTestDataPath: String = testDataMapper("/fqcn/rename")

  /**
   * Test to ensure that class renaming is supported
   */
  def testOrderModelReferenceRename() {
    doTest()
  }

  /**
   * Performs the rename test
   */
  def doTest() {
    // Ensure we have added all of the required testing
    loadAllCommon(myFixture)
    val testName = getTestName(true)

    myFixture.configureByFiles(s"${testName}.Camel", s"${testName}_after.Camel")

    myFixture.renameElementAtCaret("NewOrderModel")

    myFixture.checkResultByFile(
      s"${testName}_after.Camel",
      s"${testName}_after.Camel",
      false)
  }
}
