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
      text(end) match {
        case '?' if text.lift(end + 1) == Some('.') =>
          ranges ::= SplitTextRange(text.substring(start, end), start, end)
          end = end + 2
          start = end
        case '[' =>
          ranges ::= SplitTextRange(text.substring(start, end), start, end)
          end = end + 1
          while(text.lift(end).isDefined && text.lift(end) != Some(']') ) {
            end = end + 1
          }
          if(text.lift(end) == Some(']') || text.lift(end).isEmpty) {
            end = end + 1
            start = end
          }
        case '.' =>
          ranges ::= SplitTextRange(text.substring(start, end), start, end)
          end = end + 1
          start = end
        case _=>
          end = end + 1
      }
    }

    // Consume the remaining text
    if(start < end) {
      ranges ::= SplitTextRange(text.substring(start, end), start, end)
    }

    ranges.filter(t => t.start != t.end)
  }
}