package foo

import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture
import java.io.File

/**
 * A trait which defines common classes which can be used during test scenarios.
 * This should be mixed in the appropriate testing scenarios
 */
trait CommonTestClasses
  extends TestBase {

  def loadAllCommon(fixture: JavaCodeInsightTestFixture) {
    // Models
    loadCommon(fixture, "/foo/models/OrderModel.java")
    loadCommon(fixture, "/foo/models/PersonModel.java")
    loadCommon(fixture, "/foo/models/ComplexModel.java")
    loadCommon(fixture, "/foo/models/BaseSimpleModel.java")
    loadCommon(fixture, "/foo/models/EdgeCaseModel.java")
    loadCommon(fixture, "/foo/models/Connection.java")

    // Processors
    loadCommon(fixture, "/foo/processors/OrderProcessor.java")

    // Factories
    loadCommon(fixture, "/foo/factory/OrderFactory.java")
    loadCommon(fixture, "/foo/factory/PersonFactory.java")
    loadCommon(fixture, "/foo/factory/ComplexModelFactory.java")
    loadCommon(fixture, "/foo/factory/EdgeCaseFactory.java")
    loadCommon(fixture, "/foo/factory/ConnectionFactory.java")
  }

  def commonFile = new File(testRoot, "/commonClasses")

  def loadCommon(fixture: JavaCodeInsightTestFixture,  path: String) = {
    val targetFile = new File(commonFile, path).getPath
    fixture.copyFileToProject(targetFile, path)
  }
}
