package foo.tooling.editor

import com.intellij.openapi.fileEditor._
import com.intellij.openapi.project.{Project, DumbAware}
import org.jdom.Element
import com.intellij.openapi.vfs.VirtualFile
import foo.dom.DomFileAccessor
import DomFileAccessor._
import com.intellij.openapi.components.ServiceManager
import foo.tooling.graphing.GraphHolder

/**
 * Represents the EipEditorProvider, which acts as a factory for FileEditors.
 * This adds an additional tab to the bottom of the file editor.
 * Registered via the extension point fileEditorProvider
 */
class EipEditorProvider extends FileEditorProvider with DumbAware {
  /**
   * The unique Editor ID.
   */
  val editorId = "EipEditorProvider"

  /**
   * The EIP editor is stateless. As such, this is noop.
   *
   * @param state
   * @param project
   * @param targetElement
   */
  def writeState(state: FileEditorState, project: Project, targetElement: Element) {

  }

  /**
   * Disposes the resources associated with the editor
   * @param editor
   */
  def disposeEditor(editor: FileEditor) = editor.dispose()

  /**
   * Accepts all files which are known to be able to produce an EIP diagram.
   * If this file is accepted, createdEditor will be called.
   *
   * @param project
   * @param file Never null
   * @return True if the given VirtualFile is a blueprint file, otherwise false
   */
  def accept(project: Project, file: VirtualFile): Boolean = {
    val blueprintDomFile = getBlueprintDomFile(project, file)
    blueprintDomFile.isDefined
  }

  /**
   * Creates the EIP Graph editor associated with the virtual file.
   * Note this method should only be called after accept results in true.
   *
   * @param project
   * @param file Never null, additionally guaranteed to be a Blueprint file
   * @return The new file editor associated to the given file
   */
  def createEditor(project: Project, file: VirtualFile): FileEditor = {
    // Gain access to all registered graph creators
    val graphHolder = ServiceManager.getService(classOf[GraphHolder])
    val graphCreators = graphHolder.graphs

    new EipEditor(project, file, graphCreators)
  }

  /**
   * Loads the state from the specified sourceElement.
   * The EIP editor is stateless. As such, this is noop.
   *
   * @param sourceElement
   * @param project
   * @param file
   * @return The default FileEditorState instance
   */
  def readState(sourceElement: Element, project: Project, file: VirtualFile): FileEditorState = FileEditorState.INSTANCE

  /**
   *
   * @return The unique EipEditorProvider ID
   */
  def getEditorTypeId: String = editorId

  /**
   * Defines in which order our editor tab should appear.
   * By default we wish to allow the user to choose the EIP tab themselves.
   * @return #FileEditorPolicy.PLACE_AFTER_DEFAULT_EDITOR
   */
  def getPolicy: FileEditorPolicy = FileEditorPolicy.PLACE_AFTER_DEFAULT_EDITOR
}
