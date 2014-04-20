/*
 * Warning - Please do not edit manually
 */
package foo.language.generated;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import com.intellij.openapi.diagnostic.Logger;
import static foo.language.generated.CamelTypes.*;
import static foo.language.implementation.parsing.CamelLanguageParserUtil.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class CamelLanguageParser implements PsiParser {

  public static final Logger LOG_ = Logger.getInstance("foo.language.generated.CamelLanguageParser");

  public ASTNode parse(IElementType root_, PsiBuilder builder_) {
    boolean result_;
    builder_ = adapt_builder_(root_, builder_, this, null);
    Marker marker_ = enter_section_(builder_, 0, _COLLAPSE_, null);
    if (root_ == FQCN) {
      result_ = FQCN(builder_, 0);
    }
    else if (root_ == CAMEL_EXPRESSION) {
      result_ = camel_expression(builder_, 0);
    }
    else if (root_ == CAMEL_FUNC_BODY) {
      result_ = camel_func_body(builder_, 0);
    }
    else if (root_ == CAMEL_FUNCTION) {
      result_ = camel_function(builder_, 0);
    }
    else if (root_ == EXPRESSION) {
      result_ = expression(builder_, 0);
    }
    else if (root_ == FUNCTION_ARG) {
      result_ = functionArg(builder_, 0);
    }
    else if (root_ == FUNCTION_ARGS) {
      result_ = functionArgs(builder_, 0);
    }
    else if (root_ == FUNCTION_CALL) {
      result_ = functionCall(builder_, 0);
    }
    else if (root_ == FUNCTION_NAME) {
      result_ = functionName(builder_, 0);
    }
    else if (root_ == LITERAL) {
      result_ = literal(builder_, 0);
    }
    else if (root_ == NULLY) {
      result_ = nully(builder_, 0);
    }
    else if (root_ == OPERATOR) {
      result_ = operator(builder_, 0);
    }
    else if (root_ == TRUTHY) {
      result_ = truthy(builder_, 0);
    }
    else if (root_ == VARIABLE_ACCESS) {
      result_ = variableAccess(builder_, 0);
    }
    else {
      result_ = parse_root_(root_, builder_, 0);
    }
    exit_section_(builder_, 0, marker_, root_, result_, true, TRUE_CONDITION);
    return builder_.getTreeBuilt();
  }

  protected boolean parse_root_(final IElementType root_, final PsiBuilder builder_, final int level_) {
    return camel_file(builder_, level_ + 1);
  }

  /* ********************************************************** */
  // IDENTIFIER (DOT IDENTIFIER)*
  public static boolean FQCN(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "FQCN")) return false;
    if (!nextTokenIs(builder_, IDENTIFIER)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, IDENTIFIER);
    result_ = result_ && FQCN_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, FQCN, result_);
    return result_;
  }

  // (DOT IDENTIFIER)*
  private static boolean FQCN_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "FQCN_1")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!FQCN_1_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "FQCN_1", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // DOT IDENTIFIER
  private static boolean FQCN_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "FQCN_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, DOT, IDENTIFIER);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // IDENTIFIER arrayOrMapIndices
  static boolean arrayAccess(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "arrayAccess")) return false;
    if (!nextTokenIs(builder_, IDENTIFIER)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, IDENTIFIER);
    result_ = result_ && arrayOrMapIndices(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // LEFT_SQUARE_BRACE (NUMBER | STRING | variableAccess) RIGHT_SQUARE_BRACE
  static boolean arrayOrMapIndices(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "arrayOrMapIndices")) return false;
    if (!nextTokenIs(builder_, LEFT_SQUARE_BRACE)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, LEFT_SQUARE_BRACE);
    result_ = result_ && arrayOrMapIndices_1(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, RIGHT_SQUARE_BRACE);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // NUMBER | STRING | variableAccess
  private static boolean arrayOrMapIndices_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "arrayOrMapIndices_1")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, NUMBER);
    if (!result_) result_ = consumeToken(builder_, STRING);
    if (!result_) result_ = variableAccess(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // GT_EQ | GT
  //     | LT_EQ | LT
  //     | EQ_EQ
  static boolean binary_operator(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "binary_operator")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GT_EQ);
    if (!result_) result_ = consumeToken(builder_, GT);
    if (!result_) result_ = consumeToken(builder_, LT_EQ);
    if (!result_) result_ = consumeToken(builder_, LT);
    if (!result_) result_ = consumeToken(builder_, EQ_EQ);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // expression
  static boolean camelInterpolation(PsiBuilder builder_, int level_) {
    return expression(builder_, level_ + 1);
  }

  /* ********************************************************** */
  // camel_function operator expression (logic_operator camel_expression)*
  //     | camel_function
  public static boolean camel_expression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "camel_expression")) return false;
    if (!nextTokenIs(builder_, FUNC_BEGIN)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = camel_expression_0(builder_, level_ + 1);
    if (!result_) result_ = camel_function(builder_, level_ + 1);
    exit_section_(builder_, marker_, CAMEL_EXPRESSION, result_);
    return result_;
  }

  // camel_function operator expression (logic_operator camel_expression)*
  private static boolean camel_expression_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "camel_expression_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = camel_function(builder_, level_ + 1);
    result_ = result_ && operator(builder_, level_ + 1);
    result_ = result_ && expression(builder_, level_ + 1);
    result_ = result_ && camel_expression_0_3(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // (logic_operator camel_expression)*
  private static boolean camel_expression_0_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "camel_expression_0_3")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!camel_expression_0_3_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "camel_expression_0_3", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // logic_operator camel_expression
  private static boolean camel_expression_0_3_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "camel_expression_0_3_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = logic_operator(builder_, level_ + 1);
    result_ = result_ && camel_expression(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // camelInterpolation
  static boolean camel_file(PsiBuilder builder_, int level_) {
    return camelInterpolation(builder_, level_ + 1);
  }

  /* ********************************************************** */
  // functionCall | variableAccess
  public static boolean camel_func_body(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "camel_func_body")) return false;
    if (!nextTokenIs(builder_, IDENTIFIER)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = functionCall(builder_, level_ + 1);
    if (!result_) result_ = variableAccess(builder_, level_ + 1);
    exit_section_(builder_, marker_, CAMEL_FUNC_BODY, result_);
    return result_;
  }

  /* ********************************************************** */
  // FUNC_BEGIN camel_func_body FUNC_END
  public static boolean camel_function(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "camel_function")) return false;
    if (!nextTokenIs(builder_, FUNC_BEGIN)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, FUNC_BEGIN);
    result_ = result_ && camel_func_body(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, FUNC_END);
    exit_section_(builder_, marker_, CAMEL_FUNCTION, result_);
    return result_;
  }

  /* ********************************************************** */
  // camel_expression | literal
  public static boolean expression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "expression")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<expression>");
    result_ = camel_expression(builder_, level_ + 1);
    if (!result_) result_ = literal(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, EXPRESSION, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // literal | FQCN | camel_function
  public static boolean functionArg(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "functionArg")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<function arg>");
    result_ = literal(builder_, level_ + 1);
    if (!result_) result_ = FQCN(builder_, level_ + 1);
    if (!result_) result_ = camel_function(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, FUNCTION_ARG, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // COMMA functionArg
  static boolean functionArgTail(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "functionArgTail")) return false;
    if (!nextTokenIs(builder_, COMMA)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, COMMA);
    pinned_ = result_; // pin = 1
    result_ = result_ && functionArg(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, null, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // functionArg (functionArgTail)*
  public static boolean functionArgs(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "functionArgs")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<function args>");
    result_ = functionArg(builder_, level_ + 1);
    result_ = result_ && functionArgs_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, FUNCTION_ARGS, result_, false, null);
    return result_;
  }

  // (functionArgTail)*
  private static boolean functionArgs_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "functionArgs_1")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!functionArgs_1_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "functionArgs_1", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // (functionArgTail)
  private static boolean functionArgs_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "functionArgs_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = functionArgTail(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // functionName LEFT_PAREN functionArgs? RIGHT_PAREN
  public static boolean functionCall(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "functionCall")) return false;
    if (!nextTokenIs(builder_, IDENTIFIER)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = functionName(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, LEFT_PAREN);
    result_ = result_ && functionCall_2(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, RIGHT_PAREN);
    exit_section_(builder_, marker_, FUNCTION_CALL, result_);
    return result_;
  }

  // functionArgs?
  private static boolean functionCall_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "functionCall_2")) return false;
    functionArgs(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // IDENTIFIER
  public static boolean functionName(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "functionName")) return false;
    if (!nextTokenIs(builder_, IDENTIFIER)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, IDENTIFIER);
    exit_section_(builder_, marker_, FUNCTION_NAME, result_);
    return result_;
  }

  /* ********************************************************** */
  // IDENTIFIER
  static boolean identifierAccess(PsiBuilder builder_, int level_) {
    return consumeToken(builder_, IDENTIFIER);
  }

  /* ********************************************************** */
  // NUMBER | STRING | truthy | nully
  public static boolean literal(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "literal")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<literal>");
    result_ = consumeToken(builder_, NUMBER);
    if (!result_) result_ = consumeToken(builder_, STRING);
    if (!result_) result_ = truthy(builder_, level_ + 1);
    if (!result_) result_ = nully(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, LITERAL, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // AND_AND | OR_OR
  static boolean logic_operator(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "logic_operator")) return false;
    if (!nextTokenIs(builder_, "", AND_AND, OR_OR)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, AND_AND);
    if (!result_) result_ = consumeToken(builder_, OR_OR);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // NULL
  public static boolean nully(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "nully")) return false;
    if (!nextTokenIs(builder_, NULL)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, NULL);
    exit_section_(builder_, marker_, NULLY, result_);
    return result_;
  }

  /* ********************************************************** */
  // binary_operator | logic_operator
  public static boolean operator(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "operator")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<operator>");
    result_ = binary_operator(builder_, level_ + 1);
    if (!result_) result_ = logic_operator(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, OPERATOR, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // TRUE | FALSE
  public static boolean truthy(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "truthy")) return false;
    if (!nextTokenIs(builder_, "<truthy>", FALSE, TRUE)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<truthy>");
    result_ = consumeToken(builder_, TRUE);
    if (!result_) result_ = consumeToken(builder_, FALSE);
    exit_section_(builder_, level_, marker_, TRUTHY, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // (arrayAccess | identifierAccess)
  //     // Allow for object access to support both a.b and a?.b for null safe access
  //     (QUESTION_MARK? DOT variableAccess)*
  public static boolean variableAccess(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "variableAccess")) return false;
    if (!nextTokenIs(builder_, IDENTIFIER)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = variableAccess_0(builder_, level_ + 1);
    result_ = result_ && variableAccess_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, VARIABLE_ACCESS, result_);
    return result_;
  }

  // arrayAccess | identifierAccess
  private static boolean variableAccess_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "variableAccess_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = arrayAccess(builder_, level_ + 1);
    if (!result_) result_ = identifierAccess(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // (QUESTION_MARK? DOT variableAccess)*
  private static boolean variableAccess_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "variableAccess_1")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!variableAccess_1_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "variableAccess_1", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // QUESTION_MARK? DOT variableAccess
  private static boolean variableAccess_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "variableAccess_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = variableAccess_1_0_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, DOT);
    result_ = result_ && variableAccess(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // QUESTION_MARK?
  private static boolean variableAccess_1_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "variableAccess_1_0_0")) return false;
    consumeToken(builder_, QUESTION_MARK);
    return true;
  }

}
