package foo.intermediaterepresentation.typeInference

import foo.intermediaterepresentation.model.processors.Route

/**
 * Performs type inference on a given Abstract Model representation
 */
trait AbstractModelTypeInference {
  /**
   * Performs type inference on the given model
   * @param route The untyped model
   * @return The typed model
   */
  def performTypeInference(route: Route): Route
}