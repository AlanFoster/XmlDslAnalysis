package foo.language.psi.impl

object ElementSplitter {

  /**
   * Splits the given text by full stops into a list of substrings,
   * and returns the indices associated with the string indexes
   * @param text The given text, ie 'foo.bar.Baz'
   * @return The list of substrings ie foo, bar, baz
   *         and the relevent indices within the original text string
   */
  def split(text:String): List[SplitTextRange] = {
    var start = 0
    var end = 0
    var ranges = List[SplitTextRange]()
    while(text.lift(end).isDefined) {

      if(text(end) == '.') {
        ranges ::= SplitTextRange(text.substring(start, end), start, end)
        end = end + 1
        start = end
      } else

      end = end + 1
    }

    ranges ::= SplitTextRange(text.substring(start, end), start, end)

    ranges.filter(t => t.start != t.end)
  }
}