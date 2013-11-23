package foo.language.Highlighting

import com.intellij.openapi.fileTypes.{SyntaxHighlighter, SyntaxHighlighterFactory}
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

/**
 * An implementation of the Syntax Highlighter factory language for
 * apache camel
 */
class CamelSyntaxHighlighterFactory extends SyntaxHighlighterFactory {
  /**
   * Creates a new CamelSyntaxHighlighter instance, which is not in anyway coupled
   * to a virtual file
   * @param project The project
   * @param virtualFile The virtual file
   * @return A new instance of the CamelSyntaxHighlighter class
   */
  def getSyntaxHighlighter(project: Project, virtualFile: VirtualFile): SyntaxHighlighter =
    new CamelSyntaxHighlighter
}
