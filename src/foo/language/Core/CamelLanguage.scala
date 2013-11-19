package foo.language.Core

import com.intellij.lang.{InjectableLanguage, Language}

/**
 * Represents the singleton instantiation of the CamelLanguage.
 * This represents the basic Language that can exist. It is not
 * explicitly associated with any single known FileType/Injection etc
 * by default.
 *
 * This language has been explicitly registered via the IJ-plugin
 * xml under com.intellij.openapi.fileTypes.LanguageFileType
 */
object CamelLanguage extends Language(LanguageConstants.languageName) with InjectableLanguage