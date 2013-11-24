package impl

import com.intellij.testFramework.ParsingTestCase
import foo.language.Core.LanguageConstants
import foo.language.parser.CamelParserDefinition
import java.io.File

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

  override def skipSpaces(): Boolean = false
  override def includeRanges(): Boolean = true

  override def getTestDataPath: String = testDataMapper("/parsing")
}
