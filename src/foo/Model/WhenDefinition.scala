package foo.Model

import com.intellij.util.xml._
import org.jetbrains.annotations.NotNull

/**
 * Created with IntelliJ IDEA.
 * User: alan
 * Date: 09/11/13
 * Time: 23:55
 * To change this template use File | Settings | File Templates.
 */
trait WhenDefinition extends DomElement with ExpressionDefinition{
  @NotNull
  @Attribute("id")
  def getId: GenericAttributeValue[String]

  @SubTagsList(Array("to", "inOut", "setBody", "choice"))
  def getComponents: java.util.List[ProcessorDefinition]

  @SubTagList("to")
  def getTos: java.util.List[ToProcessorDefinition]

  @SubTagList("inOut")
  def getInOuts: java.util.List[ToProcessorDefinition]

  @SubTagList("setBody")
  def getBodies: java.util.List[SetBodyProcessorDefinition]

  @SubTagList("choice")
  def getChoices: java.util.List[ChoiceProcessorDefinition]


}
