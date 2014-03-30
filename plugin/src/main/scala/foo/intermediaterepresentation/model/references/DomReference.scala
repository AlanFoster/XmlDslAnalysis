package foo.intermediaterepresentation.model.references

import foo.dom.Model.ProcessorDefinition

/**
 * A concrete implementation of Reference which is assocaited with a DomReference.
 */
case class DomReference(element: ProcessorDefinition) extends Reference {
  override def toString: String = {
    // Strip off anything that is not needed, ie in `FromProcessorDefinition$$EnhancerByCGLIB$$28a504a3`
    val classReferenceName = element.getClass.getSimpleName.takeWhile(_ != '$')
    s"DomReference(${classReferenceName})"
  }
}