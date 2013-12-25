package foo.eip.serializers

/**
 * Represents a Serializer trait.
 *
 * @tparam T The generic type of the serializer within this list
 * @tparam U Represents an intermediate data type for a serializer.
 *           For instance a JSON/XML element data type
 */
trait Serializer[T, U] {
  /**
   * Adds an item to be serialized
   * @param item The item to be added to this serializer
   */
  def addItem(item: T): Serializer[T, U]

  /**
   * Creates an intermediate representation of the serialize with the given data type
   * @return An intermediate data structure; IE an XML/JSON element representation
   */
  def build:U

  /**
   * Performs the serialization of the given items
   * @return The serialized value
   */
  def serialize: String
}
