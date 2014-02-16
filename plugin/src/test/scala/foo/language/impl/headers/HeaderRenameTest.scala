package foo.language.impl.headers


/**
 * Tests to ensure that the apache camel simple language supports header renames
 */
class HeaderRenameTest
  extends HeaderTests {

  /**
   * {@inheritdoc}
   */
  override def getTestDataPath: String = testDataMapper("/foo/language/contribution/headers/rename")

  /**
   * Happy path tests
   */
  def ignoretestInHeadersArrayA_ComplexResolve() { doTest(ComplexHeaders,"hello") }

  /**
   * Performs the test, using the convention of test name being associated
   * with the relevent test file to use
   */
  def doTest(testContext: TestContext, newHeaderName: String) {
    val testFileName = testContext.testFileName.getOrElse("camelTest.Camel")
    // Configure the fixture
    val rawTestName = getTestName(false).takeWhile(_ != '_')
    val testData = getTestData(rawTestName, testContext.testFileName)
    myFixture.configureByText(testFileName, testData)

    myFixture.renameElementAtCaret(newHeaderName)

    // Create the expected file name based on the convention of the current test name with _after.xml
    val expectedFilePath = s"${rawTestName}_after.xml"
    myFixture.checkResultByFile(testFileName, expectedFilePath, false)
  }
}
