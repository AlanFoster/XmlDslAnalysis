package foo.eip.rename

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import foo.{CommonTestClasses, JavaJDK1_7TestBase, TestBase}

/**
 * Tests to ensure that headers can be renamed as expected within the ACMU files
 */
class HeaderRenameTests
  extends LightCodeInsightFixtureTestCase
  with TestBase
  with JavaJDK1_7TestBase
  with CommonTestClasses {

  /**
   * {@inheritdoc}
   */
  override def getTestDataPath: String = testDataMapper("/foo/eip/rename/headerRename")

  case class TestScenario(fileName: String, newName: String = "newName") {
    def withNewName(newName: String) = copy(newName = newName)
  }

  val PIPELINE_TEST = TestScenario("RenameHeaderPipeline")

  /**
   * Ensure that a header can be renamed within a pipeline
   */
  def testRenameHeaderPipeline() {
    doTest(PIPELINE_TEST)
  }

  def testRenameHeaderPipelineOneChar() {
    doTest(PIPELINE_TEST.withNewName("a"))
  }

  def doTest(testScenario: TestScenario) {
    testScenario match {
      case TestScenario(testName, newName) =>
        val (before, expected)  = (s"${testName}.xml", s"${testName}_${testScenario.newName}_expected.xml")

        myFixture.configureByFile(before)
        myFixture.renameElementAtCaret(newName)
        myFixture.checkResultByFile(before, expected, false)
    }
  }
}
