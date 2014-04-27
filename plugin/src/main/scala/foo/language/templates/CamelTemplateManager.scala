package foo.language.templates

import com.intellij.codeInsight.template.impl.TemplateImpl
import foo.FunctionalUtil._
/**
 * Provides access to creating common Camel Templates.
 * To use a Template, you will have to place the caret in the appropriate
 * place, and trigger it with a template manager
 *
 */
object CamelTemplateManager {

  /**
   * Creates a new instance of a set header XML snippet
   * @param headerName The preferred Header name to be shown to the user, if any
   * @param expressionTag The preferred expression tag to be shown to the user, if any
   * @param expressionValue The preferred expression value to be shown to the user, if any
   * @return The created template. Note this hasn't been inserted in to the document yet
   */
  def createSetHeader(headerName: Option[String],
                       expressionTag: Option[String],
                       expressionValue: Option[String]) = {

    val templateText = """<setHeader headerName="$newHeaderName$">
                         |	<$expressionType$>$expressionText$</$expressionType$>
                         |</setHeader>
                         |
                         |""".stripMargin

    val template = mutate(new TemplateImpl("", templateText, "other"))(
      _.setDescription("createSetHeaderTemplate"),
      _.setToIndent(true),
      _.setToReformat(true)
    )

    // Bind the appropriate variables, taking into consideration default values
    val binder = variableBinder(template) _

    binder("newHeaderName", headerName, "id")
    binder("expressionType", expressionTag, "constant")
    binder("expressionText", expressionValue, "Hello World")

    template
  }

  /**
   * Provides a mecchanism for binding variables to a given template
   * @param template The given templateo bind variables to
   * @param expressionName The name of the expression within the template, ie $foo$
   * @param providedValue The preferred value to be shown to the user, if any
   * @param default The default value to use if the provided value is empty
   */
  private def variableBinder(template: TemplateImpl)(expressionName: String, providedValue: Option[String], default: String) {
    // Values have to be wrapped in quotes, otherwise IJ ignores the default values
    val value = "\"" + providedValue.getOrElse(default) + "\""

    template.addVariable(expressionName, value, value, providedValue.isEmpty)
  }
}
