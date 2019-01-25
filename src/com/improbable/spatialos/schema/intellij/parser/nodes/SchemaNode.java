package com.improbable.spatialos.schema.intellij.parser.nodes;

import com.improbable.spatialos.schema.intellij.SchemaLanguage;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

public class SchemaNode extends IElementType {
    public SchemaNode(@NotNull String debugName) {
        super(debugName, SchemaLanguage.SCHEMA_LANGUAGE);
    }

    public void highlight(@NotNull PsiElement element, @NotNull AnnotationHolder holder, FileNode root) {

    }
}
