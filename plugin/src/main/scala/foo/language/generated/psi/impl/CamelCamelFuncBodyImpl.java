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
import foo.language.psi.impl.ICamelCamelFuncBodyImpl;
import foo.language.generated.psi.*;
import foo.language.psi.Util;

public class CamelCamelFuncBodyImpl extends ICamelCamelFuncBodyImpl implements CamelCamelFuncBody {

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
