package foo.eip.rename

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import foo.{CommonTestClasses, JavaJDK1_7TestBase, TestBase}
import foo.language.impl.TestDataInterpolator
import junit.framework.Assert

/**
 * Created by a on 22/04/14.
 */
class MethodRenameTests
  extends LightCodeInsightFixtureTestCase
  with TestBase
  with JavaJDK1_7TestBase
  with TestDataInterpolator
  with CommonTestClasses {

  /**
   * {@inheritdoc}
   */
  override def getTestDataPath: String = testDataMapper("/foo/eip/rename/methodRename")

  case class TestScenario(fileName: String,
                          before: String = "${body.foo<caret>}",
                          newName:String = "newName",
                          after: String = "${body.newName}") {
    def withBefore(before: String) = copy(before = before)
    def withNewName(newName: String) = copy(newName = newName)
    def withAfter(after: String) = copy(after = after)
  }

  val PERSON_MODEL = TestScenario("PersonModel")
  val COMPLEX_MODEL = TestScenario("ComplexModel")

  def testPersonModelGetterText() {
    doTest(
      PERSON_MODEL
        .withBefore("${body.getAge<caret>}")
        .withNewName("newMethod")
        .withAfter("${body.newMethod}")
    )
  }

  def testPersonModelGetterOneChar() {
    doTest(
      PERSON_MODEL
        .withBefore("${body.getAge<caret>}")
        .withNewName("a")
        .withAfter("${body.a}")
    )
  }

  def testPersonModelRenameChainedMethod() {
    doTest(
      PERSON_MODEL
        .withBefore("${body.getAge<caret>.toString}")
        .withNewName("currentAge")
        .withAfter("${body.currentAge.toString}")
    )
  }

  def ignoretestPersonModelChainedMethodRenameEnd() {
    doTest(
      COMPLEX_MODEL
        .withBefore("${body.getSelf.getSelf.getSelf<caret>}")
        .withNewName("a")
        .withAfter("${body.a.a.a}")
    )
  }

  def ignoretestPersonModelChainedMethodRenameMiddle() {
    doTest(
      COMPLEX_MODEL
        .withBefore("${body.getSelf.getSelf<caret>.getSelf}")
        .withNewName("a")
        .withAfter("${body.a.a.a}")
    )
  }

  def doTest(testScenario: TestScenario) {
    testScenario match {
      case TestScenario(testName, beforeSimple, newName, afterSimple) =>
        loadAllCommon(myFixture)

        val (beforeFileName, expectedFileName)  = (s"${testName}.xml", s"${testName}.xml")

        val beforeInterpolated = getInterpolatedTestData(beforeFileName, beforeSimple, getTestDataPath)
        val afterInterpolated = getInterpolatedTestData(expectedFileName, afterSimple, getTestDataPath)

        myFixture.configureByText(beforeFileName, beforeInterpolated)
        myFixture.renameElementAtCaret(newName)

        myFixture.checkResult(afterInterpolated, false)
    }
  }
}
