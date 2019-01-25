package com.improbable.spatialos.schema.intellij.parser.nodes.types;

import com.improbable.spatialos.schema.intellij.parser.nodes.FileNode;
import com.improbable.spatialos.schema.intellij.parser.nodes.SchemaNode;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class NewInstanceNode extends ArrayEntryNode {
    public String className;
    public List<ArrayEntryNode> entries = new LinkedList<>();
    public NewInstanceNode() {
        super("Instance Creation");
    }

    @Override
    public void highlight(@NotNull PsiElement element, @NotNull AnnotationHolder holder, FileNode root) {
        SchemaNode node = root.getNode(this.className);
        if(node == null) {
            holder.createErrorAnnotation(element, "Could not find type " + this.className).setTextAttributes(HighlighterColors.BAD_CHARACTER);
        } else {
            int off = element.getTextOffset();
            for (String clazz : this.className.split("\\.")) {
                holder.createErrorAnnotation(new TextRange(off, off + clazz.length()), null).setTextAttributes(DefaultLanguageHighlighterColors.METADATA);
                off += clazz.length() + 1;
            }
        }
    }
}
