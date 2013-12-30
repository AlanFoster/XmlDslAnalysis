/*
 * Warning - Please do not edit manually
 */
package foo.language.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static foo.language.CamelTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import foo.language.psi.*;

public class CamelCamelFuncBodyImpl extends ASTWrapperPsiElement implements CamelCamelFuncBody {

  public CamelCamelFuncBodyImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CamelVisitor) ((CamelVisitor)visitor).visitCamelFuncBody(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public CamelFunctionCall getFunctionCall() {
    return findChildByClass(CamelFunctionCall.class);
  }

  @Override
  @Nullable
  public CamelVariableAccess getVariableAccess() {
    return findChildByClass(CamelVariableAccess.class);
  }

}
