package foo

import com.intellij.openapi.roots.{ModuleRootManager, ModifiableRootModel}
import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture
import java.io.File
import foo.TestBase

/**
 * A trait which defines common classes which can be used during test scenarios.
 * This should be mixed in the appropriate testing scenarios
 */
// TODO It would be nice if there was a nice DSL for this instead of traits perhaps
trait CommonTestClasses
  extends TestBase {

  def loadAllCommon(fixture: JavaCodeInsightTestFixture) {
    loadCommon(fixture, "/foo/models/OrderModel.java")
    loadCommon(fixture, "/foo/processors/OrderProcessor.java")
  }

  def commonFile = new File(testRoot, "/commonClasses")

  def loadCommon(fixture: JavaCodeInsightTestFixture,  path: String) = {
    val targetFile = new File(commonFile, path).getPath
    fixture.copyFileToProject(targetFile, path)
  }
}
