package foo.intermediaterepresentation.model

import com.intellij.psi.CommonClassNames

object CoreConstants {
  /**
   * Represents the default inferred type of an object when it can not be inferred,
   * either by a malformed expression, or there is a limitation in the static analysis.
   *
   * In a future release it would be better to use an 'Any/NotInferrable' type into the
   * system instead of defaulting to java.lang.Object :)
   */
  val DEFAULT_INFERRED_TYPE = CommonClassNames.JAVA_LANG_OBJECT
}
