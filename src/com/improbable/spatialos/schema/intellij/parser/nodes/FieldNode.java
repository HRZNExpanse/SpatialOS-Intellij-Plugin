package com.improbable.spatialos.schema.intellij.parser.nodes;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class FieldNode extends SchemaNode implements StringableNode {
    public String name;
    public FieldTypeNode type;
    public int ID;
    public FieldNode() {
        super("Field");
    }

    @Override
    public void highlight(@NotNull PsiElement element, @NotNull AnnotationHolder holder, FileNode root) {
        holder.createInfoAnnotation(element, null).setTextAttributes(DefaultLanguageHighlighterColors.INSTANCE_FIELD);
        super.highlight(element, holder, root);
    }

    @Override
    public String name() {
        return this.name;
    }
}
