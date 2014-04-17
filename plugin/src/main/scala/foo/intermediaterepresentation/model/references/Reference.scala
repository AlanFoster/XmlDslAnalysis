package foo.intermediaterepresentation.model.references

import foo.dom.Model.ProcessorDefinition
import com.intellij.psi.xml.XmlTag

/**
 * Represents the trait associated with a Reference, IE a pointer to an existing
 * structure, such as a Dom/Psi element
 */
sealed trait Reference

/**
 * A concrete implementation of Reference which is assocaited with a DomReference.
 */
final case class DomReference(element: ProcessorDefinition) extends Reference {
  override def toString: String = {
    // Strip off anything that is not needed, ie in `FromProcessorDefinition$$EnhancerByCGLIB$$28a504a3`
    val classReferenceName = element.getClass.getSimpleName.takeWhile(_ != '$')
    s"DomReference(${classReferenceName})"
  }
}

/**
 * Represents the singleton instance of a 'NoReference'.
 * That is to say that the given processor has no associated Dom/Psi reference.
 */
object NoReference extends Reference



/**
 * Represents an ExpressionReference. This would be used under the scenario of setHeader,
 * or any arbitrary reference to an expression element
 */
final case class ExpressionReference(element: XmlTag) extends Reference