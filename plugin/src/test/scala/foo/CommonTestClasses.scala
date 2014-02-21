package foo

import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture
import java.io.File

/**
 * A trait which defines common classes which can be used during test scenarios.
 * This should be mixed in the appropriate testing scenarios
 */
// TODO It would be nice if there was a nice DSL for this instead of traits perhaps
trait CommonTestClasses
  extends TestBase {

  def loadAllCommon(fixture: JavaCodeInsightTestFixture) {
    // Models
    loadCommon(fixture, "/foo/models/OrderModel.java")
    loadCommon(fixture, "/foo/models/PersonModel.java")
    loadCommon(fixture, "/foo/models/ComplexModel.java")
    loadCommon(fixture, "/foo/models/BaseSimpleModel.java")

    // Processors
    loadCommon(fixture, "/foo/processors/OrderProcessor.java")

    // Factories
    loadCommon(fixture, "/foo/factory/OrderFactory.java")
    loadCommon(fixture, "/foo/factory/PersonFactory.java")
    loadCommon(fixture, "/foo/factory/ComplexModelFactory.java")
  }

  def commonFile = new File(testRoot, "/commonClasses")

  def loadCommon(fixture: JavaCodeInsightTestFixture,  path: String) = {
    val targetFile = new File(commonFile, path).getPath
    fixture.copyFileToProject(targetFile, path)
  }
}
