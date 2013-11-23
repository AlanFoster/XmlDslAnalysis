package impl

import java.io.File

/**
 * A trait which represent the common methods within a testing class.
 * It is expected that the caller mixes this trait in with their
 * calling test class
 */
trait TestBase {
  def testDataMapper(s: String) = new File(testRoot, s).getPath

  def testRoot = new File(sourceRoot, "../../../testData").getPath
  def sourceRoot = new File(classOf[TestBase].getResource("/").getPath)
}
