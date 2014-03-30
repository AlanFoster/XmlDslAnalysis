package foo.intermediaterepresentation.model.processors

import com.intellij.psi.PsiMethod
import foo.dom.Model.BlueprintBean
import com.intellij.util.xml.GenericAttributeValue
import foo.intermediaterepresentation.model.types.{NotInferred, TypeInformation}
import foo.intermediaterepresentation.model.references.Reference
import foo.intermediaterepresentation.model.expressions.Expression
import foo.intermediaterepresentation.model.EipName
import foo.intermediaterepresentation.model.EipName.EipName

/*************************************************************************
 * Defines the currently defined set of processors within the intermediate
 * representation of the apache camel language
 *************************************************************************/

case class Route(children: List[Processor], reference:Reference, typeInformation: TypeInformation = NotInferred) extends Processor{
  val eipType: EipName = EipName.Route
}
case class From(uri: String, reference:Reference, typeInformation: TypeInformation = NotInferred) extends Processor {
  val eipType: EipName = EipName.From
}
case class To(uri: String, reference:Reference, typeInformation: TypeInformation = NotInferred) extends Processor{
  val eipType: EipName = EipName.To
}
case class SetBody(expression: Expression, reference:Reference, typeInformation: TypeInformation = NotInferred) extends Processor{
  val eipType: EipName = EipName.Translator
}
case class SetHeader(headerName: String, expression: Expression, reference:Reference, typeInformation: TypeInformation = NotInferred) extends Processor{
  val eipType: EipName = EipName.Translator
}
case class Choice(whens: List[When], reference:Reference, typeInformation: TypeInformation = NotInferred) extends Processor{
  val eipType: EipName = EipName.choice
}
case class When(expression: Expression, children: List[Processor], reference:Reference, typeInformation: TypeInformation = NotInferred) extends Processor{
  val eipType: EipName = EipName.When
}
case class Otherwise(children: List[Processor], reference:Reference, typeInformation: TypeInformation = NotInferred) extends Processor{
  val eipType: EipName = EipName.Otherwise
}
case class Bean(ref: Option[GenericAttributeValue[BlueprintBean]], method: Option[GenericAttributeValue[PsiMethod]], reference:Reference, typeInformation: TypeInformation = NotInferred) extends Processor {
  val eipType: EipName = EipName.To
}