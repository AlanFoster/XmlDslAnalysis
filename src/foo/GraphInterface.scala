package foo

import com.intellij.openapi.ui.{DialogBuilder, DialogWrapper}
import com.intellij.openapi.project.Project
import javax.swing.{JPanel, JLabel, JComponent}
import org.jetbrains.annotations.Nullable
import com.intellij.psi.PsiFile
import com.intellij.util.ui.OptionsDialog

class GraphInterface {
  init()

  def init() {
/*   val builder = new DialogBuilder(null.asInstanceOf[Project])
    builder.setCenterPanel({
      val panel = new JPanel()
      panel.setSize(300, 300)
      panel.add(new JLabel("WHY???"))
      panel
    })

    builder.showNotModal()*/

/*    val builder = new DialogBuilder(null.asInstanceOf[Project])
    builder.setTitle("hello world")
    builder.showNotModal()*/

  }




}

/*

class GraphInterface() extends DialogWrapper(true) {
  def this(psiFile: PsiFile) = {
    this()
    setTitle("Hello World! " + psiFile.getName)
    setModal(true)
    setSize(300, 300)

    init()
  }

  def createCenterPanel(): JComponent = {
    val panel = new JPanel()
    panel.setSize(300, 300)
    panel.add(new JLabel("WHY???"))
    panel
  }
}
*/
