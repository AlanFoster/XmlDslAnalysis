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

public class CamelLiteralImpl extends ASTWrapperPsiElement implements CamelLiteral {

  public CamelLiteralImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CamelVisitor) ((CamelVisitor)visitor).visitLiteral(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public CamelNully getNully() {
    return findChildByClass(CamelNully.class);
  }

  @Override
  @Nullable
  public CamelTruthy getTruthy() {
    return findChildByClass(CamelTruthy.class);
  }

  @Override
  @Nullable
  public PsiElement getNumber() {
    return findChildByType(NUMBER);
  }

  @Override
  @Nullable
  public PsiElement getString() {
    return findChildByType(STRING);
  }

}
