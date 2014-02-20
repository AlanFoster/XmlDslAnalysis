package foo.language.Core

import com.intellij.openapi.fileTypes.{FileTypeConsumer, FileTypeFactory}

/**
 * Associates a CamelFileTypeFactory which is registered via the plugin.xml
 * extension points
 */
class CamelFileTypeFactory extends FileTypeFactory {
  /**
   * {@inheritdoc}
   */
  def createFileTypes(consumer: FileTypeConsumer) =
    consumer.consume(CamelFileType, LanguageConstants.extension)
}
