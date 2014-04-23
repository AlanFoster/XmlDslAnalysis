package foo.intermediaterepresentation.model

import com.intellij.psi.{PsiMethod, JavaPsiFacade, PsiClass}
import com.intellij.openapi.project.Project
import com.intellij.psi.search.GlobalSearchScope
import foo.intermediaterepresentation.model.types.CamelStaticTypes.ACSLFqcn

/**
 * A trait for access to global readonly type environments
 */
trait ReadonlyTypeEnvironment {
  /**
   * Function which provides access to resolve a given text to a resolved class.
   * @param fqcnText The FQCN text
   * @param project The associated project
   * @return the associated type information for this fqcn, otherwise None
   */
  def omega(fqcnText: String, project: Project): Option[PsiClass] = {
    val resolvedClass =  Option(JavaPsiFacade.getInstance(project).findClass(fqcnText, GlobalSearchScope.allScope(project)))
    resolvedClass
  }

  /**
   * Extracts the return type from the given PsiMethod
   */
  def mR(psiElementOption: Option[PsiMethod]): ACSLFqcn = {
    psiElementOption
      .map(_.getReturnType.getCanonicalText)
      .getOrElse(CoreConstants.DEFAULT_INFERRED_TYPE)
  }
}
