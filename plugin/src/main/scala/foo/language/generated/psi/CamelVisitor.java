/*
 * Warning - Please do not edit manually
 */
package foo.language.generated.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;
import foo.language.psi.ICamelJavaFQCN;
import foo.language.psi.ICamelCamelFuncBody;
import foo.language.psi.IACamelVariableAccess;

public class CamelVisitor extends PsiElementVisitor {

  public void visitFqcn(@NotNull CamelFqcn o) {
    visitICamelJavaFQCN(o);
  }

  public void visitCamelExpression(@NotNull CamelCamelExpression o) {
    visitPsiElement(o);
  }

  public void visitCamelFuncBody(@NotNull CamelCamelFuncBody o) {
    visitICamelCamelFuncBody(o);
  }

  public void visitCamelFunction(@NotNull CamelCamelFunction o) {
    visitPsiElement(o);
  }

  public void visitExpression(@NotNull CamelExpression o) {
    visitPsiElement(o);
  }

  public void visitFunctionArg(@NotNull CamelFunctionArg o) {
    visitPsiElement(o);
  }

  public void visitFunctionArgs(@NotNull CamelFunctionArgs o) {
    visitPsiElement(o);
  }

  public void visitFunctionCall(@NotNull CamelFunctionCall o) {
    visitPsiElement(o);
  }

  public void visitFunctionName(@NotNull CamelFunctionName o) {
    visitPsiElement(o);
  }

  public void visitLiteral(@NotNull CamelLiteral o) {
    visitPsiElement(o);
  }

  public void visitNully(@NotNull CamelNully o) {
    visitPsiElement(o);
  }

  public void visitOperator(@NotNull CamelOperator o) {
    visitPsiElement(o);
  }

  public void visitTruthy(@NotNull CamelTruthy o) {
    visitPsiElement(o);
  }

  public void visitVariableAccess(@NotNull CamelVariableAccess o) {
    visitIACamelVariableAccess(o);
  }

  public void visitIACamelVariableAccess(@NotNull IACamelVariableAccess o) {
    visitElement(o);
  }

  public void visitICamelCamelFuncBody(@NotNull ICamelCamelFuncBody o) {
    visitElement(o);
  }

  public void visitICamelJavaFQCN(@NotNull ICamelJavaFQCN o) {
    visitElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
