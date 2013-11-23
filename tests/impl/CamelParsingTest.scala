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
class CamelParsingTest extends ParsingTestCase("", LanguageConstants.extensions, new CamelParserDefinition) {
  def testParsing() {
    doTest(true)
  }

  override def skipSpaces(): Boolean = false
  override def includeRanges(): Boolean = true

  // Common
  override def getTestDataPath: String = new File(sourceRoot, "../../../testData").getPath
  def sourceRoot = new File(classOf[CamelParsingTest].getResource("/").getPath)
}
