package foo.language

import com.intellij.psi._
import com.intellij.patterns.XmlPatterns._
import com.intellij.patterns.DomPatterns._
import foo.Model.SimpleExpression
import com.intellij.psi.xml.{XmlText, XmlTag}
import foo.language.Core.CamelLanguage
import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PsiJavaPatterns._
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.search.PsiShortNamesCache

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
      // Injection is relative to the parent, therefore range {0, n}
      val injectionRange = new TextRange(0, xmlText.getValue.size)
      injectionPlacesRegistrar.addPlace(CamelLanguage, injectionRange, null, null)
    }
  }
}
