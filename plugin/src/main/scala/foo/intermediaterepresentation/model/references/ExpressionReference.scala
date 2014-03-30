package foo.intermediaterepresentation.model.references

import com.intellij.psi.xml.XmlTag

/**
 * Represents an ExpressionReference. This would be used under the scenario of setHeader,
 * or any arbitrary reference to an expression element
 */
case class ExpressionReference(element: XmlTag) extends Reference