package foo.dom.Model.impl

import foo.dom.Model.{ExpressionDefinition, Expression}

/**
 * Represents an abstract, yet concrete implementation of a camel expression
 * definition.
 */
abstract class ExpressionDefinitionImpl extends ExpressionDefinition {
  /**
   * Finds the matching expression within the current tag element
   * @return
   */
  // TODO Is there not an annotation available for this ? Can we hook into the existing API?
  def getExpression: Expression ={
    // Attempt to find the first existing tag and return it, otherwise default to getConstant to stop any NPEs
    List(getConstant, getSimple)
      .filter(_.exists())
      .lift(0).getOrElse(getConstant)
  }
}
