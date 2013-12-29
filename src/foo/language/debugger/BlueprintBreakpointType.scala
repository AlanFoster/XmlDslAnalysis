package foo.language.debugger

import com.intellij.xdebugger.breakpoints.{XBreakpointProperties, XLineBreakpointType}
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.project.Project
import foo.dom.DomFileAccessor

/**
 * Represents an implementation of the XLineBreakpoingType which allow the user to place breakpoints
 * within Blueprint XML documents.
 *
 * Note as the generic type of XBreakpointProperties is Nothing, this implies that no additional properties are required
 * for this breakpoint implementation class.
 */
class BlueprintBreakpointType extends XLineBreakpointType[XBreakpointProperties[Nothing]]("CamelId", "CamelBreakpoint") {
  /**
   * Creates the given breakpoint properties.
   * No additional breakpoint properties are required by this implementation.
   *
   * @param file the current file
   * @param line The current line
   * @return Always null, as no additional properties are required by this implementation
   */
  def createBreakpointProperties(file: VirtualFile, line: Int): XBreakpointProperties[Nothing] = null

  /**
   * Defines whether or not a breakpoint can be added within the given VirtualFile
   *
   * @param file The current virtual file
   * @param line The current line triggered
   * @param project The current project
   * @return True if the given file is a blueprint XML/DOM file, otherwise false.
   *         Note this method does not currently attempt to accurately understand if we are
   *         placing the breakpoint within a 'valid' place - IE within a camel route for instance.
   */
  override def canPutAt(file: VirtualFile, line: Int, project: Project): Boolean =
    DomFileAccessor.getBlueprintDomFile(project, file).isDefined



}
