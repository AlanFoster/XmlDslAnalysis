package foo.dom.inspections

import com.intellij.util.xml.highlighting.BasicDomElementsInspection
import foo.dom.Model.Blueprint

/**
 * Provides error highlighting within the Dom Model. Note this registered
 * via the plugins.xml file, and hooks into the DOM model metadata, ie
 * annotations such as @Required etc.
 */
class BlueprintDomElementInspector extends BasicDomElementsInspection[Blueprint](classOf[Blueprint])