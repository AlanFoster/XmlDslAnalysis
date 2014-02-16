package foo.language

import com.intellij.psi._
import com.intellij.patterns.XmlPatterns._
import com.intellij.patterns.DomPatterns._
import com.intellij.psi.xml.{XmlText, XmlTag}
import foo.language.Core.CamelLanguage
import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PsiJavaPatterns._
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.search.PsiShortNamesCache
import java.net.URLDecoder
import org.apache.commons.lang.StringEscapeUtils
import foo.dom.Model.SimpleExpression

/**
 * Injects the apache camel language into the DOM elements which are 'Simple' expressions
 */
class CamelSimpleLanguageInjector extends com.intellij.psi.LanguageInjector {
  /**
   * Injects the apache camel language into all relevant places
   * @param host The PsiElement host which will have the simple language possibly injected into it
   * @param injectionPlacesRegistrar The injection registrar
   */
  def getLanguagesToInject(host: PsiLanguageInjectionHost, injectionPlacesRegistrar: InjectedLanguagePlaces) {
    injectJava(host, injectionPlacesRegistrar)
    injectDOM(host, injectionPlacesRegistrar)
  }

  /**
   * Injects the apache camel language into all Camel DSL simple method calls
   * @param host The PsiElement host which will have the simple language possibly injected into it
   * @param injectionPlacesRegistrar The injection registrar
   */
  def injectJava(host: PsiLanguageInjectionHost, injectionPlacesRegistrar: InjectedLanguagePlaces) {
    val isLiteral = host.isInstanceOf[PsiLiteral]
    if (!isLiteral) {
      return
    }

    val methodCall = PsiTreeUtil.getParentOfType(host, classOf[PsiMethodCallExpression])
    if (methodCall == null) {
      return
    }

    val methodName = methodCall.getMethodExpression.getReferenceName
    if (methodName.equals("simple")) {
      val literal = host.asInstanceOf[PsiLiteral]

      val injectionRange = new TextRange(1, literal.getTextLength - 1)
      injectionPlacesRegistrar.addPlace(CamelLanguage, injectionRange, null, null)
    }
  }

  /**
   * Injects the apache camel language into all XmlTags which are SimpleExpression dom elements
   * @param host The PsiElement host which will have the simple language possibly injected into it
   * @param injectionPlacesRegistrar The injection registrar
   */
  def injectDOM(host: PsiLanguageInjectionHost, injectionPlacesRegistrar: InjectedLanguagePlaces) {
    val isSimple = xmlText().withParent(withDom(domElement(classOf[SimpleExpression]))).accepts(host)

    if (isSimple) {
      val xmlText = host.asInstanceOf[XmlText]
      val hostText = xmlText.getText

      /*val parentText = host.getParent.getText
      val simpleRangeInParent = calculateInjectionLength(parentText, hostText)*/

      // Injection is relative to the host element, therefore range {0, n}
      val injectionRange = new TextRange(0, hostText.length)
      injectionPlacesRegistrar.addPlace(CamelLanguage, injectionRange, null, null)
    }
  }

  /**
   *  When doing XML injection there are edge cases we need to be aware of
    * For instance in the scenario of `<simple>${body} < 10</simple>` this is actually malformed XML
   * instead '<simple>${body} &lt; 10</simple>' should be used, however we shouldn't throw exceptions under any scenario
   * @param parentText The entire parent element's text value from the injection host, ie `<simple>...</simple>`
   * @param childText The child text supplied by intellij's injection
   */
  def calculateInjectionLength(parentText:String, childText: String) = {
    // Extract all text which occurs after the xml tag
    val remainingText = parentText.dropWhile(_ != '>').tail
    val simpleRangeInParent = remainingText.indexOf("<")

    // Ensure that remaining text, converted to entities, starts with the childText
    val decodedParentText = replaceEntityEncoding(remainingText)
    if(decodedParentText.startsWith(childText)) simpleRangeInParent
    else 0
  }

  /**
   * Converts a string which may contain XML entity encoding into the expected
   * string
   * @param string The string to convert
   */
  def replaceEntityEncoding(string: String) =
    StringEscapeUtils.unescapeXml(string)
}
