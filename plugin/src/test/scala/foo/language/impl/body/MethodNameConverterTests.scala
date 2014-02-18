package foo.language.impl.body

import junit.framework.TestCase
import junit.framework.Assert._
import foo.language.MethodConverter

/**
 * Tests to ensure that method names are converted as expected
 */
class MethodNameConverterTests extends TestCase {
  def testSingleCharacter() {
    doTest("a", None)
  }

  def testTestSetter() {
    doTest("setA", None)
  }

  def testGetterWithNoGetterName() {
    doTest("get", Some(""))
  }

  def testSingleCharacterGetter() {
    doTest("getA", Some("a"))
  }

  def testMultipleCharacterGetter() {
    doTest("getAa", Some("aa"))
  }

  def testMultipleWordGetterCaseSensitivity() {
    doTest("geta", Some("a"))
  }

  def testGetterCaseSensitivity() {
    doTest("GETA", None)
  }

  def testMultipleWorlds() {
    doTest("getFooBarBaz", Some("fooBarBaz"))
  }

  def doTest(methodName: String, expectedResult: Option[String]) {
    val actual = (new Object() with MethodConverter).convertGetterName(methodName)
    assertEquals("The converted method name should be as expected", expectedResult, actual)
  }
}
