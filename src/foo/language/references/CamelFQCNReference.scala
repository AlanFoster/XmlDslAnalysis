package foo.language.references

import com.intellij.psi._
import com.intellij.codeInsight.lookup.{LookupElement, LookupElementBuilder}
import com.intellij.openapi.util.TextRange
import com.intellij.psi.search.{PsiSearchHelper, GlobalSearchScope}
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.codeInsight.completion.JavaCompletionUtil
import com.intellij.ide.util.{PlatformPackageUtil, PackageUtil}
import com.intellij.codeInsight.ClassUtil
import com.intellij.ide.ClassUtilCore
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import com.intellij.openapi.roots.impl.DirectoryIndex

/**
 * Provides the ReferenceContribution for a Fully Qualified Class Name in the Apache Camel
 * Simple language
 *
 * @param element The PsiElement
 */
class CamelFQCNReference(element: PsiElement)
  extends PsiReferenceBase[PsiElement](element, new TextRange(0, element.getTextLength))
  with PsiPolyVariantReference {

  def multiResolve(incompleteCode: Boolean): Array[ResolveResult] = {
    Array[ResolveResult]()
  }

  /**
   * Generates all the possible variants for the current element.
   * Note - this class does <strong>not</strong> inspect the current element, instead
   * it looks 'to the left'. This is due to the expected behaviour of a PsiReference
   * implementation in which <strong>no</strong> refining of results should be performed
   *
   * @return The possible list of variants which could be used - ignoring the current
   *         element's value
   */
  def getVariants: Array[AnyRef] = {
    val module = ModuleUtilCore.findModuleForPsiElement(myElement)
    val scope = GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module)

    val project = element.getProject

    val psiManager = PsiManager.getInstance(project)
    val directoryIndex = DirectoryIndex.getInstance(project)
    val javaDirectoryService =  JavaDirectoryService.getInstance

    def findPackages(path: String):Iterable[PsiPackage] = path match {
      case s if path.nonEmpty => {
        // Find all files within scope
         val scopedVirtualFiles =
          directoryIndex.getDirectoriesByPackageName(s, false)
            .filter(scope.contains)

        // Turn the Virtual files into real directories that are valid
        val validDirectories =
          scopedVirtualFiles
            .map(psiManager.findDirectory)
            .filter(directory => {
              directory != null && directory.isValid
            })

        // Combine all sub directories from the parent directory
        val combinedSubDirectories = validDirectories.flatMap(_.getSubdirectories)

        // Map into the expected PsiPackage implementation, so we have access to the FQN for intellisense :)
        combinedSubDirectories.map(javaDirectoryService.getPackage)
      }
      case _ => List[PsiPackage]()
    }

    val packages = findPackages("java")

    val suggestions = packages.map(directory =>
      LookupElementBuilder
        .create(directory)
    )

   suggestions.toArray
  }

  def resolve(): PsiElement = {
    val results = multiResolve(incompleteCode = false)
    if(results.length == 1) results(0).getElement
    else null
  }
}
