package foo

import com.intellij.openapi.actionSystem.{PlatformDataKeys, LangDataKeys, AnActionEvent, AnAction}
import com.intellij.psi.PsiClass
import javax.swing.{JLabel, JFrame}

/**
 * Created with IntelliJ IDEA.
 * User: alan
 * Date: 02/11/13
 * Time: 00:16
 * To change this template use File | Settings | File Templates.
 */
class GenerateGraphAction extends AnAction {

  def actionPerformed(event: AnActionEvent) {
    // TODO Only sure menu if the file is our file
    val psiFile = Option(event.getData(LangDataKeys.PSI_FILE))
   // val editor = Option(event.getData(PlatformDataKeys.EDITOR))

    if(psiFile.isEmpty) {
      event.getPresentation.setEnabled(false)
      return
    }

    val frame = new JFrame("hello world")
    frame.getContentPane.add(new JLabel("Hello world"))
    frame.setSize(300, 300)
    frame.setVisible(true)

   // val dialog = new GraphInterface(psiFile.get)
   // dialog.show()
  }
}
