package foo.language.psi

import com.intellij.psi.{PsiNameIdentifierOwner, PsiElement}

/**
 * Represents a trait of IACamelVariableAccess which will be mixed in with the grammar,
 * and a concrete implementation will be provided within foo.language.psi.impl.ACamelVariableAccessImpl
 */
trait IACamelVariableAccess extends PsiElement /*extends PsiNameIdentifierOwner */{

}
