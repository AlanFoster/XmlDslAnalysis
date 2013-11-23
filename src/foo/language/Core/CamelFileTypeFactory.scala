package foo.language.Core

import com.intellij.openapi.fileTypes.{FileTypeConsumer, FileTypeFactory}

/**
 * TODO
 */
class CamelFileTypeFactory extends FileTypeFactory {
  def createFileTypes(consumer: FileTypeConsumer) =
    consumer.consume(CamelFileType, LanguageConstants.extension)
}
