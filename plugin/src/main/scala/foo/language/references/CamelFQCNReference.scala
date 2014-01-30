package foo.language.references

import com.intellij.psi._
import com.intellij.openapi.util.TextRange
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.openapi.module.ModuleUtilCore
import scala.collection.JavaConversions._
import com.intellij.openapi.roots.impl.DirectoryIndex
import foo.language.Core.CamelFileType
import com.intellij.psi.util.PsiTreeUtil
import foo.language.generated.psi.CamelFQCN

/**
 * Provides the ReferenceContribution for a Fully Qualified Class Name in the Apache Camel
 * Simple language
 *
 * @param element The PsiElement
 */
class CamelFQCNReference(element: PsiElement, range: TextRange)
  // Note this reference is a hard reference, ie if it doesn't resolve, it's an error!
  extends PsiReferenceBase[PsiElement](element, range, false)
  with PsiPolyVariantReference {

  def multiResolve(incompleteCode: Boolean): Array[ResolveResult] = {
    val text = element.getText.substring(0, getRangeInElement.getEndOffset)
    val packages = availablePackagesAndClasses().filter({
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
    availablePackagesAndClasses().toArray
  }


  /**
   * Handles an element rename. This will create an entirely new FQCN element
   * and replace the existing element
   *
   * @param newElementName The renamed subset of the string, ie perhaps just 'String'
   * @return The newly replaced PsiElement
   */
  override def handleElementRename(newElementName: String): PsiElement = {
    val newFQCN = getRangeInElement.replace(myElement.getText, newElementName)
    val newElem = createReplacementFQCN(newFQCN, myElement)

    // Force a replacement of the current elem - Note this also returns our response.
    myElement.replace(newElem)
  }

  // IntelliJ allows you to create new elements by simply creating new temp files which are lexed/parsed etc
  // TODO Is there any functional composition gained from this ATM
  def createReplacementFQCN(newText: String, existingElement: PsiElement) = {
    val tempName = s"__${newText}_${existingElement.getContainingFile.getName}_replace.Camel"
    val fileText = s"""$${headerAs('temp', ${newText})}"""
    val newPsiFile = PsiFileFactory.getInstance(myElement.getProject)
      .createFileFromText(tempName, CamelFileType, fileText)
     val newFQCNElem = PsiTreeUtil.findChildOfType(newPsiFile, classOf[CamelFQCN])
    newFQCNElem
  }

  /**
   * Finds all available packages and classes for the given TextRange within this element
   * @return The classes and packages which are available within the given TextRange of this element
   */
  def availablePackagesAndClasses() = {
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
