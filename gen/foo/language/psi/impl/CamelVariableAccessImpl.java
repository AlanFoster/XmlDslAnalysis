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

public class CamelVariableAccessImpl extends ASTWrapperPsiElement implements CamelVariableAccess {

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

}
