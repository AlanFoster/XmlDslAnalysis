/*

 * Warning - Please do not edit manually

 */
package foo.language.generated.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import foo.language.generated.psi.CamelCamelFuncBody;
import foo.language.generated.psi.CamelFunctionCall;
import foo.language.generated.psi.CamelVariableAccess;
import foo.language.generated.psi.CamelVisitor;
import foo.language.psi.impl.ICamelCamelFuncBodyImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
