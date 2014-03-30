package foo.intermediaterepresentation.typeInference

import foo.intermediaterepresentation.model.{TypeEnvironment, Processor, Route}

/**
 * Performs type inference on a given Abstract Model representation
 */
trait AbstractModelTypeInference {
  /**
   * Performs type inference on the given model
   * @param route The untyped model
   * @return The typed model
   */
  def performTypeInference(route: Route,
                           interceptor: (Processor) => (() => TypeEnvironment) => TypeEnvironment = identityInterceptor): Route

  def identityInterceptor(processor: Processor)
                         (fallback: () => TypeEnvironment): TypeEnvironment = {
    fallback()
  }
}