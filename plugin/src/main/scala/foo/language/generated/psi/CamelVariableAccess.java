/*
 * Warning - Please do not edit manually
 */
package foo.language.generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import foo.language.psi.IACamelVariableAccess;

public interface CamelVariableAccess extends IACamelVariableAccess {

  @NotNull
  List<CamelVariableAccess> getVariableAccessList();

  @NotNull
  PsiElement getIdentifier();

  @Nullable
  PsiElement getNumber();

  @Nullable
  PsiElement getString();

}
