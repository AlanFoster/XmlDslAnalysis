package foo

import junit.framework.{Assert, TestCase}
import com.intellij.testFramework.PlatformTestCase
import com.intellij.idea.IdeaTestApplication
import scala.collection.JavaConverters._

class FooTest extends TestCase {
  def testJVMArgs() {
    Assert.assertEquals("JVM Arg bar should be set!", "bar", System.getProperty("foo"))
    Assert.assertEquals("JVM idea.home.path should be set!",
      "C:\\Users\\alan\\.IntelliJIdea13\\system\\plugins-sandbox\\test",
      System.getProperty("idea.home.path"))
    Assert.assertEquals("JVM idea.plugins.path should be set!",
      "C:\\Users\\alan\\.IntelliJIdea13\\system\\plugins-sandbox\\plugins",
      System.getProperty("idea.plugins.path"))
    Assert.assertNotNull("Java home should not be null",
      System.getProperty("java.home")
    )

  }

  // TODO investigate further cucumber https://github.com/JetBrains/intellij-community/blob/master/plugins/git4idea/test-stepdefs/git4idea/GitCucumberWorld.java
  def ignoreTestIntellij() {
    PlatformTestCase.initPlatformLangPrefix()
    IdeaTestApplication.getInstance(null)
   // println("" + PathManager.getBinPath())
    //println("" + PathManager.getHomePath())
   // println(classOf[IdeaWin32].getClass)
   // println(classOf[IdeaWin32].getClass.getClassLoader)
  }

}
