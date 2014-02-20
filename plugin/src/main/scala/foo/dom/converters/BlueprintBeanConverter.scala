package foo.dom.converters

import com.intellij.util.xml.{ResolvingConverter, ConvertContext, Converter}
import foo.dom.Model.BlueprintBean
import java.util
import foo.dom.DomFileAccessor
import collection.JavaConverters._

/**
 * Blueprint bean reference converter which provides the list of available
 * blueprint beans within the current blueprint file.
 *
 * Note this class provides variant completion by implementing ResolvingConverter
 */
class BlueprintBeanConverter extends ResolvingConverter[BlueprintBean] {
  /**
   * {@inheritdoc}
   */
  override def toString(bean: BlueprintBean, context: ConvertContext): String = {
    bean.getId.getStringValue
  }

  /**
   * {@inheritdoc}
   */
  override def fromString(s: String, context: ConvertContext): BlueprintBean = {
    val matchingResult: BlueprintBean =
      getVariants(context)
        .asScala
        .find(_.getId.getStringValue == s)
        .getOrElse(null)
    matchingResult
  }

  /**
   * Returns the list of all possible blueprint beans which are available within
   * the current blueprint file
   * @param context The context
   * @return The variants assocaited within the dom file
   */
  override def getVariants(context: ConvertContext): util.Collection[BlueprintBean] = {
    val (project, virtualFile) = (context.getProject, context.getFile)

    val domFile = DomFileAccessor.getBlueprintDomFile(project, virtualFile).get
    domFile.getBlueprintBeans
  }
}
