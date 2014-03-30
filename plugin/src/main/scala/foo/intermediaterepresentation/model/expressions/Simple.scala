package foo.intermediaterepresentation.model.expressions

import foo.intermediaterepresentation.model.references.Reference

/**
 * Created by a on 30/03/14.
 */
// TODO Maybe result type should be more statically typed instead of a string
case class Simple(value: String, resultType: Option[String], reference: Reference) extends Expression
