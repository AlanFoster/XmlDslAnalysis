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

public class CamelFunctionCallImpl extends ASTWrapperPsiElement implements CamelFunctionCall {

  public CamelFunctionCallImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CamelVisitor) ((CamelVisitor)visitor).visitFunctionCall(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public CamelFunctionArgs getFunctionArgs() {
    return findChildByClass(CamelFunctionArgs.class);
  }

  @Override
  @NotNull
  public CamelFunctionName getFunctionName() {
    return findNotNullChildByClass(CamelFunctionName.class);
  }

}
