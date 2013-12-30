/*
 * Warning - Please do not edit manually
 */
package foo.language.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface CamelCamelExpression extends PsiElement {

  @NotNull
  List<CamelCamelExpression> getCamelExpressionList();

  @NotNull
  CamelCamelFunction getCamelFunction();

  @Nullable
  CamelExpression getExpression();

  @Nullable
  CamelOperator getOperator();

}
