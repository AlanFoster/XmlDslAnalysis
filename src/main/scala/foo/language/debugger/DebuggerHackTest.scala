package foo.language.debugger
/*

import com.intellij.openapi.components.{ProjectComponent, ApplicationComponent}
import java.lang.String
import com.intellij.debugger.{DebuggerManagerEx, DebuggerManager}
import com.intellij.openapi.project.Project
import com.intellij.debugger.impl.{DebuggerSession, DebuggerManagerListener}
import com.intellij.debugger.ui.breakpoints.BreakpointManagerListener


/**
 * Created by alan on 23/12/13.
 */
class DebuggerHackTest(project: Project) extends ProjectComponent {
  def initComponent() {


    val manager = DebuggerManagerEx.getInstanceEx(project)
    manager.getBreakpointManager.addBreakpointManagerListener(new BreakpointManagerListener {
      def breakpointsChanged(): Unit = {
        val breakPoints = manager.getBreakpointManager.getBreakpoints
      }
    })
      manager.addDebuggerManagerListener(new DebuggerManagerListener {
      def sessionRemoved(session: DebuggerSession): Unit = {
        println("test")
      }

      def sessionDetached(session: DebuggerSession): Unit = {
        println("test")
      }

      def sessionAttached(session: DebuggerSession): Unit = {
        println("test")
      }

      def sessionCreated(session: DebuggerSession): Unit = {
        println("test")
      }
    })

  }

  def disposeComponent() {
  }

  def getComponentName: String = "DebuggerHackTest"

  def projectOpened(): Unit = {}

  def projectClosed(): Unit = {}
}
*/

