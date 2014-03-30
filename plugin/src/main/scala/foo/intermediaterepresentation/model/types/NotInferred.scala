package foo.intermediaterepresentation.model.types

/**
 * Represents the single instance of TypeInformation that is not yet
 * resolved.
 *
 * This could be used under the scenario of having creating an IR, but not
 * yet placing any semantic information into the tree yet.
 */
object NotInferred extends TypeInformation {
  override def toString: String = "NotInferred"
}