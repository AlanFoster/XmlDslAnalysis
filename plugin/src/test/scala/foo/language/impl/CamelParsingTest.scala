package foo.language.impl

import com.intellij.testFramework.ParsingTestCase
import foo.language.Core.LanguageConstants
import foo.language.parser.CamelParserDefinition
import java.io.File
import foo.TestBase

/**
 * Camel Parsing tests.
 *
 * Note tests must still follow the JUnit naming convention of 'test{name}'
 * There is also a convention within intellij to follow of, doTest will attempt
 * to find the file "{name}.{extention}" to use the parserDefinition on, and will
 * test the results against "{name}.txt"
 */
class CamelParsingTest
  extends ParsingTestCase("", LanguageConstants.extension, new CamelParserDefinition)
  with TestBase {

  override def getTestDataPath: String = testDataMapper("/foo/language/parsing")

  /**
   * Basic Parsing Test
   */
  def testParsing() {
    doTest(true)
  }

  /**
   * Testing that functions are properly tested
   */
  def testFunction() {
    doTest(true)
  }

  /**
   * Test Empty strings are valid
   */
  def testEmptyString() {
    doTest(true)
  }

  /**
   * Test a Number literal by itself
   */
  def testNumberLiteral() {
    doTest(true)
  }

  /**
   * {@inheritdoc}
   */
  override def skipSpaces(): Boolean = false

  /**
   * {@inheritdoc}
   */
  override def includeRanges(): Boolean = true
}
