package foo.language

import com.intellij.psi.{InjectedLanguagePlaces, PsiLanguageInjectionHost}
import com.intellij.patterns.XmlPatterns._
import com.intellij.patterns.DomPatterns._
import foo.Model.SimpleExpression
import com.intellij.psi.xml.{XmlText, XmlTag}
import foo.language.Core.CamelLanguage
import com.intellij.openapi.util.TextRange

/**
 * Injects the apache camel language into the DOM elements which are 'Simple' expressions
 */
class CamelSimpleLanguageInjector extends com.intellij.psi.LanguageInjector {
  /**
   * Injects the apache camel language into all XmlTags which are SimpleExpression dom elements
   * @param host The PsiElement host which will have the simple language possibly injected into it
   * @param injectionPlacesRegistrar The injection registrar
   */
  def getLanguagesToInject(host: PsiLanguageInjectionHost, injectionPlacesRegistrar: InjectedLanguagePlaces) {
    val isSimple = xmlText().withParent(withDom(domElement(classOf[SimpleExpression]))).accepts(host)

    if(isSimple) {
      val xmlText = host.asInstanceOf[XmlText]
      // Injection is relative to the parent, therefore range {0, n}
      val injectionRange = new TextRange(0, xmlText.getValue.size)
      injectionPlacesRegistrar.addPlace(CamelLanguage, injectionRange, null, null)
    }
  }
}
