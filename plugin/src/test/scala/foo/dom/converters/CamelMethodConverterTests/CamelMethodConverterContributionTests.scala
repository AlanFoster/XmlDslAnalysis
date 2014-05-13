package foo.dom.converters.CamelMethodConverterTests

import scala.collection.JavaConverters._
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import foo.{CommonTestClasses, JavaJDK1_7TestBase, TestBase}
import com.intellij.codeInsight.completion.CompletionType
import org.unitils.reflectionassert.ReflectionAssert._
import org.unitils.reflectionassert.ReflectionComparatorMode._

/**
 * Tests to ensure that method name contribution works as expected within
 * the DOm
 */
class CamelMethodConverterContributionTests
  extends LightCodeInsightFixtureTestCase
  with JavaJDK1_7TestBase
  with TestBase
  with CommonTestClasses {

  /**
   * {@inheritdoc}
   */
  override def getTestDataPath: String = testDataMapper("/foo/dom/converters/CamelMethodConverterContribution/Variants")

  def testDuplicatesRemoved() {
    doTest(List(
      "charAt", "checkBounds", "codePointAt", "codePointBefore", "codePointCount", "compareTo", "compareToIgnoreCase", "concat",
      "contains", "contentEquals", "copyValueOf", "endsWith", "equals", "equalsIgnoreCase", "format", "getBytes", "getChars", "hashCode",
      "indexOf", "intern", "lastIndexOf", "length", "matches", "offsetByCodePoints", "regionMatches", "replace", "replaceAll",
      "replaceFirst", "split", "startsWith", /*"String",*/ "substring", "toCharArray", "toLowerCase", "toString", "toUpperCase", "trim",
      "valueOf", "clone", /*"Object",*/ "finalize", "getClass", "notify", "notifyAll", "registerNatives", "subSequence", "wait"))
  }


  /**
   * Ensure that constructor methods are not shown to the user
   */
  def testConstructorMethod() {
    loadAllCommon(myFixture)
    doTest(List(
      /* "ConnectionFactory", */ "createConnection", "getConnectionString", "getMaximumConnections", "getTimeout",
      "getUrl", "getUsername", "setConnectionString", "setMaximumConnections", "setPassword", "setTimeout", "setUrl",
      "setUsername", "clone", "equals", "hashCode", /*"Object",*/ "toString", "finalize", "getClass", "notify", "notifyAll",
      "registerNatives", "wait"))
  }

  /**
   * Performs the main test with the convention of method names being associated
   * with the file to configure the tests with
   */
  def doTest(expectedHeaders: List[String]) {
    // Configure the fixture
    myFixture.configureByFile(s"${getTestName(false)}.xml")

    myFixture.complete(CompletionType.BASIC)
    val suggestedStrings = myFixture.getLookupElementStrings

    assertReflectionEquals(expectedHeaders.asJava, suggestedStrings, LENIENT_ORDER)
  }
}
