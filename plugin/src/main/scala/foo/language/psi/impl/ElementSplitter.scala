package foo.language.psi.impl

object ElementSplitter {
  // TODO More FP way of doing this!!
  def split(text:String): List[((String, Int, Int))] = {
    var start = 0
    var end = 0
    var ranges = List[(String, Int, Int)]()
    var cutNext = false
    while(text.lift(end).isDefined) {

      if(text(end) == '.') {
        ranges ::= (text.substring(start, end), start, end)
        end = end + 1
        start = end
      } else

      end = end + 1
    }

    ranges ::= (text.substring(start, end), start, end)

    ranges.filter(t => t._2 != t._3)
  }

  def main(args: Array[String]) {
    println(split("java.lang.String").mkString)
  }
}
