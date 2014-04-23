package foo.tooling.graphing.ultimate

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import foo.{CommonTestClasses, JavaJDK1_7TestBase, TestBase}
import foo.tooling.editor.EipEditor
import foo.tooling.graphing.strategies.icons.{IntellijIconLoader, EipIconLoader}
import foo.tooling.graphing.strategies.tooltip.SemanticToolTipStrategy
import junit.framework.Assert
import javax.swing.{WindowConstants, JFrame}
import org.apache.commons.lang.time.DurationFormatUtils

class GraphTests
  extends LightCodeInsightFixtureTestCase
  with TestBase
  with JavaJDK1_7TestBase
  with CommonTestClasses {

  /**
   * {@inheritdoc}
   */
  override def getTestDataPath: String = testDataMapper("/foo/tools/graphing/ultimate")

  def testComplexNestedRoute() {
    doTest()
  }

  /**
   * Ensure no NPE is triggered on a malformed data model from the graphing library
   */
  def testMalformedFromElement() {
    doTest()
  }

  /**
   * Ensure no NPE is triggered in a long method call chain
   */
  def testExtremelyLongMethodCallChain() {
    doTest()
  }

  def testChoiceWithOtherwise() {
    doTest()
  }

  private def doTest() {
    loadAllCommon(myFixture)

    val testName = getTestName(false)
    val virtualFile = myFixture.configureByFile(s"${testName}_dom.xml").getVirtualFile

    val startTime = System.currentTimeMillis()
    val ideaGraphCreator = new IdeaGraphCreator(new EipIconLoader with IntellijIconLoader, new SemanticToolTipStrategy)

    val eipEditor= new EipEditor(getProject, virtualFile, List(ideaGraphCreator))
    eipEditor.selectNotify()
    val component = eipEditor.getComponent

    val window = new JFrame()
    window.setSize(500, 500)
    window.add(component)
    window.setVisible(true)
    window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)

    Assert.assertNotNull("The EIP graph should be created as expected", component)
    val endTime = System.currentTimeMillis()
    val difference = endTime - startTime

    val formattedDifference = DurationFormatUtils.formatDurationISO(difference)
    println("Generated graph in : " + formattedDifference)
  }
}