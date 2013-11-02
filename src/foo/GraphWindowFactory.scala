package foo

import com.intellij.openapi.wm.{ToolWindow, ToolWindowFactory}
import com.intellij.openapi.project.Project
import com.intellij.ui.content.ContentFactory
import javax.swing.JLabel

/**
  * Created with IntelliJ IDEA.
  * User: alan
  * Date: 02/11/13
  * Time: 01:35
  * To change this template use File | Settings | File Templates.
  */
class GraphWindowFactory extends ToolWindowFactory {

   def createToolWindowContent(project: Project, toolWindow: ToolWindow) = {
     val content = ContentFactory.SERVICE.getInstance().createContent(new JLabel("Hello World"), "DisplayName123", false)
     toolWindow.getContentManager.addContent(content)
   }
 }
