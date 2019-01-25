package com.improbable.spatialos.schema.intellij.parser.nodes;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class CommandNode extends SchemaNode {
    public String name;
    public FieldTypeNode request;
    public FieldTypeNode reply;

    public CommandNode() {
        super("Command");
    }

    @Override
    public void highlight(@NotNull PsiElement element, @NotNull AnnotationHolder holder, FileNode root) {
        holder.createInfoAnnotation(element, null).setTextAttributes(DefaultLanguageHighlighterColors.INSTANCE_METHOD);
        super.highlight(element, holder, root);
    }
}
