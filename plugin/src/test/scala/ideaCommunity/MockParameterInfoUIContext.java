/*
 * Copyright 2000-2009 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ideaCommunity;

import com.intellij.lang.parameterInfo.ParameterInfoUIContext;
import com.intellij.psi.PsiElement;

import java.awt.*;
import java.util.concurrent.atomic.AtomicMarkableReference;

/**
 * @author Maxim.Mossienko
 */
public class MockParameterInfoUIContext<T extends PsiElement> implements ParameterInfoUIContext {
    private boolean enabled;
    private String text;
    private int highlightStart;
    private final T myFunction;
    private int parameterIndex;

    public MockParameterInfoUIContext(final T function) {
        myFunction = function;
    }

    @Override
    public String setupUIComponentPresentation(String _text, int highlightStartOffset, int highlightEndOffset, boolean isDisabled, boolean strikeout,
                                               boolean isDisabledBeforeHighlight, Color background){
        text = _text;
        highlightStart = highlightStartOffset;
        return _text;
    }

    @Override
    public boolean isUIComponentEnabled() {
        return enabled;
    }

    @Override
    public void setUIComponentEnabled(final boolean _enabled) {
        enabled = _enabled;
    }

    @Override
    public int getCurrentParameterIndex() {
        return parameterIndex;
    }

    public void setCurrentParameterIndex(final int parameterIndex) {
        this.parameterIndex = parameterIndex;
    }

    @Override
    public PsiElement getParameterOwner() {
        return myFunction;
    }

    @Override
    public Color getDefaultParameterColor() {
        return null;
    }

    public String getText() {
        return text;
    }

    public int getHighlightStart() {
        return highlightStart;
    }
}