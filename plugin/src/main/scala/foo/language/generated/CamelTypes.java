/*
 * Warning - Please do not edit manually
 */
package foo.language.generated;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import foo.language.elements.CamelElementType;
import foo.language.elements.CamelTokenType;
import foo.language.generated.psi.impl.*;

public interface CamelTypes {

  IElementType CAMEL_EXPRESSION = new CamelElementType("CAMEL_EXPRESSION");
  IElementType CAMEL_FUNCTION = new CamelElementType("CAMEL_FUNCTION");
  IElementType CAMEL_FUNC_BODY = new CamelElementType("CAMEL_FUNC_BODY");
  IElementType EXPRESSION = new CamelElementType("EXPRESSION");
  IElementType FQCN = new CamelElementType("FQCN");
  IElementType FUNCTION_ARG = new CamelElementType("FUNCTION_ARG");
  IElementType FUNCTION_ARGS = new CamelElementType("FUNCTION_ARGS");
  IElementType FUNCTION_CALL = new CamelElementType("FUNCTION_CALL");
  IElementType FUNCTION_NAME = new CamelElementType("FUNCTION_NAME");
  IElementType LITERAL = new CamelElementType("LITERAL");
  IElementType OPERATOR = new CamelElementType("OPERATOR");
  IElementType VARIABLE_ACCESS = new CamelElementType("VARIABLE_ACCESS");

  IElementType AND_AND = new CamelTokenType("&&");
  IElementType COMMA = new CamelTokenType(",");
  IElementType DOT = new CamelTokenType(".");
  IElementType EQ_EQ = new CamelTokenType("==");
  IElementType FUNC_BEGIN = new CamelTokenType("${");
  IElementType FUNC_END = new CamelTokenType("}");
  IElementType GT = new CamelTokenType(">");
  IElementType GT_EQ = new CamelTokenType(">=");
  IElementType IDENTIFIER = new CamelTokenType("IDENTIFIER");
  IElementType LEFT_PAREN = new CamelTokenType("(");
  IElementType LEFT_SQUARE_BRACE = new CamelTokenType("[");
  IElementType LT = new CamelTokenType("<");
  IElementType LT_EQ = new CamelTokenType("<=");
  IElementType NUMBER = new CamelTokenType("NUMBER");
  IElementType OR_OR = new CamelTokenType("||");
  IElementType QUESTION_MARK = new CamelTokenType("?");
  IElementType RIGHT_PAREN = new CamelTokenType(")");
  IElementType RIGHT_SQUARE_BRACE = new CamelTokenType("]");
  IElementType STRING = new CamelTokenType("STRING");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
       if (type == CAMEL_EXPRESSION) {
        return new CamelCamelExpressionImpl(node);
      }
      else if (type == CAMEL_FUNCTION) {
        return new CamelCamelFunctionImpl(node);
      }
      else if (type == CAMEL_FUNC_BODY) {
        return new CamelCamelFuncBodyImpl(node);
      }
      else if (type == EXPRESSION) {
        return new CamelExpressionImpl(node);
      }
      else if (type == FQCN) {
        return new CamelFqcnImpl(node);
      }
      else if (type == FUNCTION_ARG) {
        return new CamelFunctionArgImpl(node);
      }
      else if (type == FUNCTION_ARGS) {
        return new CamelFunctionArgsImpl(node);
      }
      else if (type == FUNCTION_CALL) {
        return new CamelFunctionCallImpl(node);
      }
      else if (type == FUNCTION_NAME) {
        return new CamelFunctionNameImpl(node);
      }
      else if (type == LITERAL) {
        return new CamelLiteralImpl(node);
      }
      else if (type == OPERATOR) {
        return new CamelOperatorImpl(node);
      }
      else if (type == VARIABLE_ACCESS) {
        return new CamelVariableAccessImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
