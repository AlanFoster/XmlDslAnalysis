package foo.language.fqcn

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import junit.framework.Assert._
import com.intellij.psi.{PsiPackage, PsiClass}
import foo.{CommonTestClasses, JavaJDK1_7TestBase}

/**
 * Tests to ensure that FQCNs are resolved as expected
 */
class FqcnResolveTest
  extends LightCodeInsightFixtureTestCase
  with JavaJDK1_7TestBase
  with CommonTestClasses {

  /**
   * {@inheritdoc}
   */
  override def getTestDataPath: String = testDataMapper("/foo/language/fqcn/resolve")

  /**
   * Test to ensure that classes are resolved as expected
   */
  def testClassResolve() {
    val element = getElement()

    // Note, it is not possible ot be DRY with this test, as PsiPackage/PsiClass do not share a base getQualifiedName function
    assertTrue("The element should be a psi class", element.isInstanceOf[PsiClass])
    assertEquals("The qualified name should be as expected", element.asInstanceOf[PsiClass].getQualifiedName, "java.lang.String")
  }

  /**
   * Tests to ensure packages are resolved as expected
   */
  def testPackageResolve() {
    val element = getElement()

    // Note, it is not possible ot be DRY with this test, as PsiPackage/PsiClass do not share a base getQualifiedName function
    assertTrue("The element should be a psi class", element.isInstanceOf[PsiPackage])
    assertEquals("The qualified name should be as expected", element.asInstanceOf[PsiPackage].getQualifiedName, "java.lang")
  }

  def getElement() = {
    myFixture.configureByFile(s"${getTestName(false)}.Camel")
    val element = myFixture.getElementAtCaret
    element
  }

}
