/*
 * Warning - Please do not edit manually
 */
package foo.language.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface CamelFunctionArg extends PsiElement {

  @Nullable
  CamelFQCN getFQCN();

  @Nullable
  CamelLiteral getLiteral();

}
