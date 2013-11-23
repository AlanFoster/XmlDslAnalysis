An important feature which can be implemented at lexer level is mixing languages within a file (for example, embedding fragments
of Java code in some template language). If a language supports embedding its fragments in another language, it needs to define
the chameleon token types for different types of fragments which can be embedded, and these token types need to implement the
ILazyParseableElementType interface. The lexer of the enclosing language needs to return the entire fragment of the embedded
language as a single chameleon token, of the type defined by the embedded language. To parse the contents of the chameleon token,
IDEA will call the parser of the embedded language through a call to ILazyParseableElementType.parseContents().