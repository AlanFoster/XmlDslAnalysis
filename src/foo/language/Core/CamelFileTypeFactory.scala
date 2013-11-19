package foo.language.Core

import com.intellij.openapi.fileTypes.{FileTypeConsumer, FileTypeFactory}

/**
 * Created with IntelliJ IDEA.
 * User: alan
 * Date: 19/11/13
 * Time: 20:10
 * To change this template use File | Settings | File Templates.
 */
class CamelFileTypeFactory extends FileTypeFactory {
  def createFileTypes(consumer: FileTypeConsumer) =
    consumer.consume(CamelFileType, LanguageConstants.extensions)
}
