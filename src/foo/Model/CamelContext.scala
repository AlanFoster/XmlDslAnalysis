package foo.Model

import com.intellij.util.xml.model.DomModel

/**
 * Created with IntelliJ IDEA.
 * User: alan
 * Date: 03/11/13
 * Time: 01:42
 * To change this template use File | Settings | File Templates.
 */
trait CamelContext extends DomModel {
  def getRoutes: java.util.List[Route]
}
