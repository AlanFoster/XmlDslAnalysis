package impl.fqcn

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import impl.JavaJDK1_7TestBase

/**
 * Tests to ensure that FQCNs are resolved as expected
 */
class ResolveTest
  extends LightCodeInsightFixtureTestCase
  with JavaJDK1_7TestBase {

  override def getTestDataPath: String = testDataMapper("/fqcn/resolve")



}
