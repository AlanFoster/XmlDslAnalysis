/*
 * Warning - Please do not edit manually
 */
package foo.language.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;

public class CamelVisitor extends PsiElementVisitor {

  public void visitFQCN(@NotNull CamelFQCN o) {
    visitICamelJavaFQCN(o);
  }

  public void visitCamelExpression(@NotNull CamelCamelExpression o) {
    visitPsiElement(o);
  }

  public void visitCamelFuncBody(@NotNull CamelCamelFuncBody o) {
    visitPsiElement(o);
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

  public void visitOperator(@NotNull CamelOperator o) {
    visitPsiElement(o);
  }

  public void visitVariableAccess(@NotNull CamelVariableAccess o) {
    visitPsiElement(o);
  }

  public void visitICamelJavaFQCN(@NotNull ICamelJavaFQCN o) {
    visitElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
