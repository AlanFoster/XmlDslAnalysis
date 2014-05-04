package foo.language.functions

/**
 * Defines the currently known list of functions, and associated type information
 * for argument types that apache camel allows
 */
object CamelFunctions {
  val knownFunctions = List(
    CamelFunction("bodyAs", List(CamelPackage("type")), "Hello world"),
    CamelFunction("mandatoryBodyAs", List(CamelPackage("type")), "Baz"),
    CamelFunction("headerAs", List(CamelString("key"), CamelPackage("type")), "Hi")
  )
}
