- An important feature which can be implemented at lexer level is mixing languages within a file (for example, embedding fragments
of Java code in some template language). If a language supports embedding its fragments in another language, it needs to define
the chameleon token types for different types of fragments which can be embedded, and these token types need to implement the
ILazyParseableElementType interface. The lexer of the enclosing language needs to return the entire fragment of the embedded
language as a single chameleon token, of the type defined by the embedded language. To parse the contents of the chameleon token,
IDEA will call the parser of the embedded language through a call to ILazyParseableElementType.parseContents().

- http://www.jetbrains.com/idea/webhelp/viewing-psi-structure.html
    idea.is.internal=true

PsiMethodReferenceUtil.checkReturnType

Good example code of PSI manipulation http://devnet.jetbrains.com/message/5102890#5102890

Useful:
    JavClassReferenceSet
    JavaClassListReferenceProvider
    JavaClassReferenceProvider
    JavaClassReference
    JavaPackageReference
    JavaLookupElementBuilder

Validation/Annotators
 -   http://devnet.jetbrains.com/message/5214206#5214206
        You should implement custom highlighter for your reference, either
        Annotator or LocalInspectionTool. And to prevent from compilation,
        you'll need to create your own validating compiler or use existing
        InspectionValidator.

Cookbook
    http://devnet.jetbrains.com/message/5501099#5501099
    https://code.google.com/p/ide-examples/wiki/IntelliJIdeaPsiCookbook

Creating a class
   JavaDirectoryService.getInstance().checkCreateClass()

A list of identifiers which reference a piece of java code
    PsiJavaCodeReferenceElementImpl

PsiShortNamesCache.getInstance(element.getProject)
      .getClassesByName(element.getText, GlobalSearchScope.allScope(element.getProject))

-     // http://devnet.jetbrains.com/message/5242172#5242172
      //JavaPsiFacade.getInstance(null).getResolveHelper.p
      // PsiShortNamesCache.getInstance(null).getAllClassNames
      PsiShortNamesCache.getInstance(null).getCl


PSI Reference Contribution / Psi Reference Provider
----------------------------------------------------

http://devnet.jetbrains.com/message/5493753#5493753

PsiReferenceContributor allows you to inject a PsiReference into psi elements that you don't own; therefore for custom
languages, there is no need for this class.

If you are providing your own support for contribution you can simply implement getReference(), and return your
implementation of PsiReference base. If you are using an implementation which implements PsiPolyVariantReference
there appears to be duplicate implementation in the IJ codebase already

- com.intellij.psi.PsiReferenceBase.Poly
- com.intellij.psi.PsiPolyVariantReferenceBase

getVariants will produce the lookup list, whilst resolve should provide the PsiElement reference - where returning null
will suggest no reference exists. See com.intellij.psi.PsiReference which has all of the required header docs.

Inplace renaming - lang.refactoringSupport extension point
provide RefactoringSupportProvider, isMemberInplaceRenameAvailable