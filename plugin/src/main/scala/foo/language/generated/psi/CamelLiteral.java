/*
 * Warning - Please do not edit manually
 */
package foo.language.generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface CamelLiteral extends PsiElement {

  @Nullable
  CamelNully getNully();

  @Nullable
  CamelTruthy getTruthy();

  @Nullable
  PsiElement getNumber();

  @Nullable
  PsiElement getString();

}
