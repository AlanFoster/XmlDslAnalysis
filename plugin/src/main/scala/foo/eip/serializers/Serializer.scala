package foo.eip.serializers

/**
 * Represents a Serializer trait.
 *
 * @tparam T The generic type of the serializer within this list
 */
trait Serializer[T] {
  /**
   * Performs the serialization of the given entity
   * @return The serialized value
   */
  def serialize(entity: T): String
}
