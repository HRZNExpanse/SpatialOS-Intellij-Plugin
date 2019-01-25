package com.improbable.spatialos.schema.intellij.parser.nodes.types;

import com.improbable.spatialos.schema.intellij.parser.nodes.FileNode;
import com.improbable.spatialos.schema.intellij.parser.nodes.SchemaNode;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class ArrayEntryNode extends SchemaNode {
    public SchemaNode fromType;
    public int index;
    public ArrayEntryNode(@NotNull String debugName) {
        super(debugName);
    }

    @Override
    public void highlight(@NotNull PsiElement element, @NotNull AnnotationHolder holder, FileNode root) {
        holder.createInfoAnnotation(element, null).setTextAttributes(DefaultLanguageHighlighterColors.KEYWORD); //todo remove
    }
}
