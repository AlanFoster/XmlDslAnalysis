package foo.language.debugger

import com.intellij.debugger.ui.breakpoints.{Breakpoint, BreakpointPropertiesPanel, BreakpointFactory}
import com.intellij.openapi.project.Project
import javax.swing.Icon
import com.intellij.openapi.util.Key
import org.jdom.Element
import foo.Icons

/**
 * Created by alan on 22/12/13.
 */
class CamelBreakpointFactory extends BreakpointFactory {
  val camelBreakpointCategory = Key.create("camel")

  def createBreakpoint(project: Project, element: Element): Breakpoint = null

  def getBreakpointCategory: Key[_ <: Breakpoint] = camelBreakpointCategory

  def getIcon: Icon = Icons.Camel

  def getDisabledIcon: Icon = Icons.Camel

  def getHelpID: String = "wat"

  def getDisplayName: String = "moooooo"

  def createBreakpointPropertiesPanel(project: Project, compact: Boolean): BreakpointPropertiesPanel = ???
}
