package foo.tooling.graphing.strategies.icons

import foo.tooling.graphing.EipProcessor
import javax.swing.Icon
import foo.intermediaterepresentation.model.EipType

abstract class EipIconLoader extends IconLoader {
  /**
   * Private cache for previously loaded images. Note this still preserves referential transparency
   * The key is the string and whether or not the icon is selected, which maps to the loaded icon
   */
  val cache = collection.mutable.Map[(String, Boolean), Icon]()

  /**
   * Loads the appropriate icon for the given EIP component
   * @param component The EIP component to load an icon for
   * @param isSelected The currently selected flag
   *                   True if the current processor is selected within the given graph, false otherwise.
   * @return The associated icon with this EIP processor
   */
  def loadIcon(component: EipProcessor, isSelected: Boolean): Icon = component match {
    case component@EipProcessor (_, _, eipTypeValue, _) =>
      val eipType = EipType.getString(eipTypeValue)
      cache.getOrElseUpdate((eipType, isSelected), {
        if (isSelected) loadPickedIcon (eipType)
        else loadUnpickedIcon (eipType)
      })
  }
}
