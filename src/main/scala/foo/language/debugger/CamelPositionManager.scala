package foo.language.debugger

import com.intellij.debugger.{DebuggerManager, SourcePosition, PositionManager}
import com.intellij.debugger.engine.{SuspendContext, DebugProcessListener, DebugProcess}
import com.intellij.debugger.requests.ClassPrepareRequestor
import com.sun.jdi.request.ClassPrepareRequest
import com.sun.jdi.{ThreadReference, Location, ReferenceType}
import java.util
import java.util.Collections
import com.intellij.execution.ui.{RunContentDescriptor, RunContentListener}
import com.intellij.execution.configurations.{RemoteConnection, RunProfileState}
import com.intellij.execution.ExecutionException

/**
 * Created by alan on 22/12/13.
 */
class CamelPositionManager(process: DebugProcess) extends PositionManager {
  def getSourcePosition(location: Location): SourcePosition = null

  def getAllClasses(classPosition: SourcePosition): util.List[ReferenceType] = Collections.emptyList()

  def locationsOfLine(`type`: ReferenceType, position: SourcePosition): util.List[Location] = Collections.emptyList()

  def createPrepareRequest(requestor: ClassPrepareRequestor, position: SourcePosition): ClassPrepareRequest = null
}
