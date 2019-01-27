package com.improbable.spatialos.schema.intellij.parser;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulator;
import com.intellij.psi.ElementManipulators;
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
        //No fucking idea why this happenes. For some reason the returned text range is shifted to the right one more than it should be. TODO: figure out why and do proper fix
        if(psiElement.getNode().getElementType() == SchemaParser.ANNOTATION_TYPE_NAME) {
            return psiElement.getTextRangeInParent().shiftLeft(1);
        }
        return psiElement.getTextRangeInParent();
    }
}
