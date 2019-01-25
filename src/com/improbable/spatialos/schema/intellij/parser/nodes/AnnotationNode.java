package com.improbable.spatialos.schema.intellij.parser.nodes;

import com.improbable.spatialos.schema.intellij.parser.nodes.types.ArrayEntryNode;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class AnnotationNode extends SchemaNode {
    public String name;
    public List<ArrayEntryNode> entries = new LinkedList<>();
    public AnnotationNode() {
        super("Annotation");
    }

    @Override
    public void highlight(@NotNull PsiElement element, @NotNull AnnotationHolder holder, FileNode root) {
        SchemaNode node = root.getNode(this.name);
        if(node == null) {
            holder.createErrorAnnotation(element, "Could not find type " + this.name).setTextAttributes(HighlighterColors.BAD_CHARACTER);
        } else {
            holder.createInfoAnnotation(element, null).setTextAttributes(DefaultLanguageHighlighterColors.METADATA);
        }
    }
}
