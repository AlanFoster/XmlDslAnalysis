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

public class CamelCamelExpressionImpl extends ASTWrapperPsiElement implements CamelCamelExpression {

  public CamelCamelExpressionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CamelVisitor) ((CamelVisitor)visitor).visitCamelExpression(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<CamelCamelExpression> getCamelExpressionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, CamelCamelExpression.class);
  }

  @Override
  @NotNull
  public CamelCamelFunction getCamelFunction() {
    return findNotNullChildByClass(CamelCamelFunction.class);
  }

  @Override
  @Nullable
  public CamelExpression getExpression() {
    return findChildByClass(CamelExpression.class);
  }

  @Override
  @Nullable
  public CamelOperator getOperator() {
    return findChildByClass(CamelOperator.class);
  }

}
