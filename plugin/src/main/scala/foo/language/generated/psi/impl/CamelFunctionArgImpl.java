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

public class CamelFunctionArgImpl extends ASTWrapperPsiElement implements CamelFunctionArg {

  public CamelFunctionArgImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CamelVisitor) ((CamelVisitor)visitor).visitFunctionArg(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public CamelFqcn getFqcn() {
    return findChildByClass(CamelFqcn.class);
  }

  @Override
  @Nullable
  public CamelLiteral getLiteral() {
    return findChildByClass(CamelLiteral.class);
  }

}
