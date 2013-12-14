package foo.language.references

import com.intellij.psi._
import com.intellij.codeInsight.lookup.{LookupElement, LookupElementBuilder}
import com.intellij.openapi.util.TextRange
import com.intellij.psi.search.{PsiSearchHelper, GlobalSearchScope}
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.codeInsight.completion.{JavaLookupElementBuilder, JavaCompletionUtil}
import com.intellij.ide.util.{PlatformPackageUtil, PackageUtil}
import com.intellij.codeInsight.ClassUtil
import com.intellij.ide.ClassUtilCore
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import com.intellij.openapi.roots.impl.DirectoryIndex
import com.intellij.openapi.util.text.StringUtil

/**
 * Provides the ReferenceContribution for a Fully Qualified Class Name in the Apache Camel
 * Simple language
 *
 * @param element The PsiElement
 */
class CamelFQCNReference(element: PsiElement, range: TextRange)
  extends PsiReferenceBase[PsiElement](element, range, true)
  with PsiPolyVariantReference {

  def multiResolve(incompleteCode: Boolean): Array[ResolveResult] = {
    val text = element.getText.substring(0, getRangeInElement.getEndOffset)
    val packages = foo().filter({
      case psiClass: PsiClass => psiClass.getQualifiedName.equals(text)
      case directory: PsiPackage => directory.getQualifiedName.equals(text)
    })
    packages.map(new PsiElementResolveResult(_)).toArray
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
    val packages = foo()

    val suggestions = packages.map({
      case psiClass: PsiClass => //LookupElementBuilder.create(psiClass)
     //   JavaLookupElementBuilder.forClass(psiClass, psiClass.getQualifiedName, true).withPresentableText(StringUtil.getShortName(psiClass.getQualifiedName))
        psiClass
      case directory: PsiPackage => //LookupElementBuilder.create(directory)
        directory
    })

    suggestions.toArray
  }


  def foo() = {
    val searchText = {
      val text = myElement.getText.substring(0, getRangeInElement.getEndOffset)
      val lastIndex = text.lastIndexOf(".")
      if(lastIndex == -1) ""
      else myElement.getText.substring(0, lastIndex)
    }

    val module = ModuleUtilCore.findModuleForPsiElement(myElement)
    val project = element.getProject
    val scope = GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module)
    val psiManager = PsiManager.getInstance(project)
    val directoryIndex = DirectoryIndex.getInstance(project)
    val javaDirectoryService =  JavaDirectoryService.getInstance

    def findRootDirectories(packageName: String):Iterable[PsiDirectory] = {
        // Find all files within scope
        val scopedVirtualFiles =
          directoryIndex.getDirectoriesByPackageName(packageName, false)
            .filter(scope.contains)

        // Turn the Virtual files into real directories that are valid
        val validDirectories =
          scopedVirtualFiles
            .map(psiManager.findDirectory)
            .filter(directory => {
            directory != null && directory.isValid
          })
        validDirectories
    }

    def findSubpackages(rootDirectories: Iterable[PsiDirectory]): Iterable[PsiPackage] = {
      // Combine all sub directories from the parent directory
      val combinedSubDirectories = rootDirectories.flatMap(_.getSubdirectories)

      // Map into the expected PsiPackage implementation, so we have access to the FQN for intellisense :)
      combinedSubDirectories.map(javaDirectoryService.getPackage)
    }

    val rootDirectories = findRootDirectories(searchText)

    val packages = findSubpackages(rootDirectories)
    packages

    val classes =
      rootDirectories
        .map(javaDirectoryService.getPackage)
        .flatMap(_.getClasses(scope))

    packages ++ classes
  }

  def resolve(): PsiElement = {
    val results = multiResolve(incompleteCode = false)
    // TODO Multi-resolve as required
    if(results.length > 0) results(0).getElement
    else null
  }


}