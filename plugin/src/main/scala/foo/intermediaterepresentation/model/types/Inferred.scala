package foo.intermediaterepresentation.model.types

/**
 * Represents the scenario in which type information has been inferred for a given node.
 * Note that the before/after information may not have been successfully inferred yet.
 */
case class Inferred(before: TypeEnvironment, after: TypeEnvironment) extends TypeInformation