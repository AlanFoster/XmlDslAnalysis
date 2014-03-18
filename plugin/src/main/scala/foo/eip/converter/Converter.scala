package foo.eip.converter

import foo.eip.model.Route

/**
 * Represents a convert which will take a known data type and convert it
 * into the Abstract model representation of the same data
 * @tparam T The data format of the input type
 */
trait Converter[T] {
  /**
   * Converts the given data model into an abstract representation
   * @param input The input data model
   * @return The abstract representation of the same data model
   */
  def convert(input: T): Route
}
