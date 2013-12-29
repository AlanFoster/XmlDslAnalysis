package foo

import java.io.File

/**
 * A trait which represent the common methods within a testing class.
 * It is expected that the caller mixes this trait in with their
 * calling test class
 */
trait TestBase {
  /**
   * Returns the absolute given path of the loaded source directory relative to the
   * data folder
   * @param relativePath The relative path from the test data
   * @return The absolute path to the test data
   */
  def testDataMapper(relativePath: String) = new File(testRoot, relativePath).getPath

  /**
   * Computes the test root absolute path
   * @return The absolute path of the test data folder
   */
  def testRoot = new File(sourceRoot, "../../../testData").getPath

  /**
   * Computes the absolute source root
   * @return The absolute path of the source root folder of the project
   */
  def sourceRoot = new File(classOf[TestBase].getResource("/").getPath)
}
