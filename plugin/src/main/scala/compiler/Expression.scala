trait Expr
case class Integer(n: Int) extends Expr
case class Real(n: Float) extends Expr
case class Sum(left: Expr, right: Expr) extends Expr
case class Product(left: Expr, right: Expr) extends Expr
case class VarDeclaration[T <: TypeInfo](typeInfo: T, identifier: String) extends Expr
case class Var(identifier: String) extends Expr
case class Assignment(v: Var, rhs: Expr) extends Expr
case class Program(statements: List[Expr]) extends Expr
case class Output(expr: Expr) extends Expr
case class Block(statements: List[Expr]) extends Expr

// Possible types within the type system
sealed abstract class TypeInfo
case object IntType extends TypeInfo
case object RealType extends TypeInfo

/*
def eval(e: Expr): Float =
  e match {
    case Integer(n) => n
    case Real(n) => n
    case Sum(e1, e2) => eval(e1) + eval(e2)
    case Var(id) => 5 // TODO
    case Product(e1, e2) => eval(e1) * eval(e2)
    case Assignment(Var(id), e) =>
       val value = eval(e)
        3f
    case Output(e1) => {
      println(eval(e1))
      1f
    }
  }*/
/*def runProgram(program: Program) {
  program match {
    case Program(statements) =>
      statements.foreach(eval)
  }
}
val complex = Sum(
  Sum(Integer(5), Real(10)),
  Integer(10)
)*/

// A valid symbol within the location
// Name, Kind, Type, 'Other'
// foo, Func, Int * Int -> Int, extern
// bar, var, Int, const
// baz, parameter, int, wat
case class Symbol[T <: TypeInfo](typeInfo: T, identifier: String)

// Semantic analysis requires multiple passes !!
case class SymbolTable(
          previousScope: Option[SymbolTable],
          symbols: Map[String, Symbol[TypeInfo]]
        ) {
  // starts a new nested scope
  def enterScope():SymbolTable = {
    SymbolTable(Some(this), Map[String, Symbol[TypeInfo]]())
  }

  // Finds an existing symbol, or None, based on its identifier
  def find(identifier: String): Option[Symbol[TypeInfo]] = {
    symbols.lift(identifier).orElse(previousScope match {
      case None => None
      case Some(symbolTable) => symbolTable.find(identifier)
    })
  }

  // Adds a new symbol to the table
  def add(typeInfo: TypeInfo, identifier: String) = {
    SymbolTable(previousScope, symbols + (identifier -> Symbol(typeInfo, identifier)))
  }

  // Boolean function which states whether or not the identifier is in scope
  // Note this searches the *current* scope intially, then attempts to work its way down
  // if we reach an empty symbol stack, then the variable is not defined within our scope
  // and it is a semantic error
  def inScope(identifier: String): Boolean = {
    find(identifier).isDefined
  }

  def inTopmostScope(identifier: String): Boolean = {
    symbols.lift(identifier).isDefined
  }

  // Enters the previous scope
  def exitScope(): SymbolTable = {
    if(previousScope.isEmpty) throw new Error("No more scopes to pop! :(")
    previousScope.get
  }
}

object Main {

  def semanticAnalysis(symbolTable: SymbolTable, expr: Expr): SymbolTable ={
    println("Perform semantic analysis!")
    println(s"\t${symbolTable}")
    expr match {
      case Program(statements) =>
        statements.foldLeft(symbolTable)((currentScope, expr) => {
          println("Program Starting with :: " + currentScope)
          semanticAnalysis(symbolTable, expr)
        })

        println("**********************************************")
        println("Successfully performed semantic analysis")
        println("**********************************************")

        symbolTable
      case Block(statements) =>
        // Enter in a new scope
        val newScope = symbolTable.enterScope()
        println("Entered new scope")
        // Traverse the remaining statements - passing the returned scope to each expr
        statements
          .foldLeft(newScope)((currentScope, expr) => {
            println("Block Starting with :: " + currentScope)
            semanticAnalysis(currentScope, expr)
          })
        // Return the original symbol table for further analysis
        // IE Clear any symbols from within this scope
        newScope.exitScope()
      case VarDeclaration(typeInfo, identifier) =>
        if(symbolTable.inTopmostScope(identifier)) {
          throw new Error(s"${expr} already defined in scope!")
        }
        val newSymbolTable = symbolTable.add(typeInfo, identifier)
        //newSymbolTable
        println("Created new symbol - " + newSymbolTable)
        newSymbolTable
      case Assignment(varReference, e) =>
        // Perform semantic analysis on our expr firstly
        semanticAnalysis(symbolTable, e)
        // Perform the assignment if the types match - secondary pass?
        val lhs = semanticAnalysis(symbolTable, varReference)

        // Use the old symbol table as expected
        symbolTable
      case Integer(n) =>
        /// Should type checking be performed separately?
        symbolTable
      case Real(n) =>
        /// Should type checking be performed separately?
        symbolTable
      case Var(identifier) =>
        val symbol = symbolTable.find(identifier)
        // Throw an error if it does not exist as expected
        if(!symbol.isDefined) {
          throw new Error(s"Cannot assign to undefined variable ${identifier} within expression : ${expr}")
        }
        symbolTable
      case Sum(e1, e2) =>
        // Perform analysis on the LHS/RHS
        val s1 = semanticAnalysis(symbolTable, e1)
        val s2 = semanticAnalysis(symbolTable, e2)

        // Don't mutate the symbol table
        symbolTable
    }
  }

  def main(args: Array[String]) {
    val program = Program(List(
      Block(List(
        VarDeclaration(IntType, "x"),
        Assignment(Var("x"), Integer(1)),
        Assignment(Var("x"), Sum(Integer(1), Var("x"))),
        Block(List(
          // Should break type semantics on assignment
          VarDeclaration(IntType, "x"),
          Assignment(Var("x"), Real(1))
        )),
        Block(List(
          VarDeclaration(IntType, "y"),
          Assignment(Var("x"), Sum(Integer(1), Var("y")))
        ))
      ))
    ))

    // Perform the analysis on our program with an empty symbol table
    // This would contain our 'global' variables :)
    semanticAnalysis(SymbolTable(None, Map()), program)
  }

}
