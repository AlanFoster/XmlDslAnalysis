package foo.language.fqcn

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import scala.collection.JavaConverters._
import org.unitils.reflectionassert.ReflectionAssert._
import org.unitils.reflectionassert.ReflectionComparatorMode._
import foo.JavaJDK1_7TestBase


/**
 * Tests for ensuring that variant contribution for FQCN classes in camel
 * are as expected
 */
class FqcnVariantsTest
  extends LightCodeInsightFixtureTestCase
  with JavaJDK1_7TestBase {

  /**
   * {@inheritdoc}
   */
  override def getTestDataPath: String = testDataMapper("/foo/language/fqcn/variants")

  def testPackageVariantsSuggestedWhenNoExistingText() {
    val expected = List(
      "java", "javax", "META-INF", "org"
    )
    testPackageVariantsSuggested(expected)
  }

  def testPackageVariantsSuggestedWithInitialTextSupplied() {
    val expected = List(
      "java", "javax"
    )
    testPackageVariantsSuggested(expected)
  }

  // XXX investigate why code completion isn't working in the tests, but works fine in the 'real' system
  def ignoreTestPackageVariantsSuggestedWhenLeftMostPackageSupplied() {
    val expected = List(
      "lang"
    )
    testPackageVariantsSuggested(expected)
  }

  def testPackageVariantsSuggested(expected: List[String]) {
    val fileName = s"${getTestName(false)}.Camel"
    val suggestedVariants = myFixture.getCompletionVariants(fileName)

    assertReflectionEquals(expected.asJava, suggestedVariants, LENIENT_ORDER)
  }
}
