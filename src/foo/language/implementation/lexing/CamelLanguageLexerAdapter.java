package foo.language.implementation.lexing;

import com.intellij.lexer.FlexAdapter;

/**
 * Provides a wrapper implementation of the CamelLanguageLexer.
 * Specifically this FlexAdapter provides the ability to maintain its
 * state with an arbitrary index position which correlates to the underlying
 * document.
 */
public class CamelLanguageLexerAdapter extends FlexAdapter {
    public CamelLanguageLexerAdapter() {
        super(new CamelLanguageLexer());
    }
}
