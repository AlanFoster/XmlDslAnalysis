package foo.language.parser

import com.intellij.lang.{Language, PsiParser, ASTNode, ParserDefinition}
import com.intellij.lang.ParserDefinition.SpaceRequirements
import com.intellij.psi.{TokenType, PsiElement, PsiFile, FileViewProvider}
import com.intellij.psi.tree.{IFileElementType, TokenSet}
import com.intellij.openapi.project.Project
import com.intellij.lexer.Lexer
import foo.language.implementation.lexing.CamelLanguageLexerAdapter
import foo.language.generated.{CamelTypes, CamelLanguageParser}
import foo.language.Core.{CamelPsiFile, CamelLanguage}

/**
 * A concrete implementation of a ParserDefinition.
 *
 * A lexing definition allows for a parsing and lexing to be created which
 * intellij core can use to initially produce an Abstract Syntax Tree (AST)
 * Each node within the AST tree has an associated IElement.
 *
 * It is within the parserDefinition that allows the AST to be turned into an
 * PSI (Program Structure Interface). This conversion is supplied by the method
 * createElement.
 *
 * The root node will be composed of a special PSI type, which is used to
 * represent the File element. This is available via the createFile method.
 */
class CamelParserDefinition extends ParserDefinition {
  /**
   * @param project The project
   * @return The lexer
   */
  def createLexer(project: Project): Lexer = new CamelLanguageLexerAdapter()

  /**
   *
   * @param project The project
   * @return The parser
   */
  def createParser(project: Project): PsiParser = new CamelLanguageParser()

  /**
   * The IFileElementType is used to represent the absolute topmost root in the
   * Intellij AST file
   * @return The base element of an camel AST tree
   */
  def getFileNodeType: IFileElementType = CamelParserDefinitionTokens.fileNodeType

  /**
   * {@inheritDoc}
   */
  def getWhitespaceTokens: TokenSet = CamelParserDefinitionTokens.WhiteSpaces

  /**
   * {@inheritDoc}
   */
  def getCommentTokens: TokenSet = CamelParserDefinitionTokens.Comments

  /**
   * {@inheritDoc}
   */
  def getStringLiteralElements: TokenSet = CamelParserDefinitionTokens.stringLiterals

  /**
   * Provides support for converting an AST Node into an element
   *
   * @param node The AST node, which contains no semantic constructs
   * @return A PSI element which holds a reference to the provided ASTNode
   */
    def createElement(node: ASTNode): PsiElement = CamelTypes.Factory.createElement(node)

  /**
   * {@inheritDoc}
   */
  def createFile(viewProvider: FileViewProvider): PsiFile = new CamelPsiFile(viewProvider)

  /**
   * {@inheritDoc}
   */
  def spaceExistanceTypeBetweenTokens(left: ASTNode, right: ASTNode): SpaceRequirements = SpaceRequirements.MAY
}

/**
 * Represents the TokenSets which can be shared within the construction of
 * an AST, which has associated PSI elements.
 */
object CamelParserDefinitionTokens {
  val WhiteSpaces = TokenSet.create(TokenType.WHITE_SPACE)
  val Comments = TokenSet.EMPTY
  val stringLiterals = TokenSet.create(CamelTypes.STRING)
  val fileNodeType = new IFileElementType(CamelLanguage)
}
