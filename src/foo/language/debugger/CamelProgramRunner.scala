package foo.language.debugger

import com.intellij.debugger.impl.GenericDebuggerRunner
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ProgramRunner.Callback
import com.intellij.execution.configurations.{RunProfileState, RunnerSettings}
import com.intellij.execution.ExecutionResult
import com.intellij.openapi.project.Project
import com.intellij.execution.ui.RunContentDescriptor
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.xdebugger._
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider

/**
 * Created by alan on 23/12/13.
 */
/*class CamelProgramRunner extends GenericDebuggerRunner {
  override def getRunnerId: String = "howdy"

  val processStart = new XDebugProcessStarter {
    def start(session: XDebugSession): XDebugProcess = new CamelDebugProcess(session)
  }

  override def doExecute(project: Project,
                         state: RunProfileState,
                         contentToReuse: RunContentDescriptor,
                         env: ExecutionEnvironment): RunContentDescriptor = {
    FileDocumentManager.getInstance().saveAllDocuments();

/*    val session = XDebuggerManager.getInstance(project).startSession(this, env, contentToReuse, processStarter)*/


    //super.doExecute(project, state, contentToReuse, env)
  }
}*/

/*

class CamelDebugProcess(session: XDebugSession) extends XDebugProcess(session) {

}*/
