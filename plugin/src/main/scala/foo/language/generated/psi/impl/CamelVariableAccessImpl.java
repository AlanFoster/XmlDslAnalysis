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
import foo.language.psi.impl.ACamelVariableAccessImpl;
import foo.language.generated.psi.*;
import foo.language.psi.Util;

public class CamelVariableAccessImpl extends ACamelVariableAccessImpl implements CamelVariableAccess {

  public CamelVariableAccessImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CamelVisitor) ((CamelVisitor)visitor).visitVariableAccess(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<CamelVariableAccess> getVariableAccessList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, CamelVariableAccess.class);
  }

  @Override
  @NotNull
  public PsiElement getIdentifier() {
    return findNotNullChildByType(IDENTIFIER);
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
