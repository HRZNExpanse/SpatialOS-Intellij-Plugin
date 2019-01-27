package com.improbable.spatialos.schema.intellij.parser;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulator;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SchemaElementManipulator implements ElementManipulator<PsiElement> {

    public SchemaElementManipulator() {

    }
    @Nullable
    @Override
    public PsiElement handleContentChange(@NotNull PsiElement psiElement, @NotNull TextRange textRange, String s) throws IncorrectOperationException {
        return null;
    }

    @Nullable
    @Override
    public PsiElement handleContentChange(@NotNull PsiElement psiElement, String s) throws IncorrectOperationException {
        return null;
    }

    @NotNull
    @Override
    public TextRange getRangeInElement(@NotNull PsiElement psiElement) {
        return TextRange.from(0, psiElement.getTextLength());

    }
}
