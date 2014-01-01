package tests.foo

import junit.framework.{Assert, TestCase}
import com.intellij.testFramework.PlatformTestCase
import com.intellij.idea.IdeaTestApplication

class FooTest extends TestCase {
  def ignoredJVMArgs() {
    Assert.assertEquals("JVM Arg should be set!", "bar", System.getProperty("foo"))
  }

  // TODO investigate further cucumber https://github.com/JetBrains/intellij-community/blob/master/plugins/git4idea/test-stepdefs/git4idea/GitCucumberWorld.java
  def testIntellij() {
    PlatformTestCase.initPlatformLangPrefix()
    IdeaTestApplication.getInstance(null)
   // println("" + PathManager.getBinPath())
    //println("" + PathManager.getHomePath())
   // println(classOf[IdeaWin32].getClass)
   // println(classOf[IdeaWin32].getClass.getClassLoader)
  }

}
