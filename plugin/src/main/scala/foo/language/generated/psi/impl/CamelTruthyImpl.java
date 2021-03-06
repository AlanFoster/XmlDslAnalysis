/*
 * Warning - Please do not edit manually
 */
package foo.language.generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static foo.language.generated.CamelTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import foo.language.generated.psi.*;
import foo.language.psi.Util;

public class CamelTruthyImpl extends ASTWrapperPsiElement implements CamelTruthy {

  public CamelTruthyImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CamelVisitor) ((CamelVisitor)visitor).visitTruthy(this);
    else super.accept(visitor);
  }

}
