package impl

import com.intellij.testFramework.fixtures.{DefaultLightProjectDescriptor, LightCodeInsightFixtureTestCase}
import com.intellij.testFramework.LightProjectDescriptor
import com.intellij.openapi.projectRoots.{JavaSdk, Sdk}
import java.io.File

/**
 * Represents a test base class which provides a java 1.7 JDK.
 * Note this is required in scenarios where you would expect intellisense on known Java classes/packages
 * such as java.lang.String
 */
trait JavaJDK1_7TestBase
  extends LightCodeInsightFixtureTestCase
  with TestBase {

  /**
   * Provide a concrete Sdk instance for this test case
   * @return The Java 1.7 project descriptor
   */
  override def getProjectDescriptor: LightProjectDescriptor = JavaJDK1_7TestBase.projectDescriptor
}

/**
 * The Java JDK 1.7 test base object for providing access to a single projectDesciptor
 */
object JavaJDK1_7TestBase extends TestBase {
  /**
   * Provide the single instance of the DefaultLightProjectDescriptor for the Java JDK 1.7
   * implementation.
   */
  val projectDescriptor = new DefaultLightProjectDescriptor {
    override def getSdk: Sdk = {
      val path = new File(testRoot, "mockJDK-1.7").getPath
      JavaSdk.getInstance().createJdk("1.7", path, false)
    }
  }
}