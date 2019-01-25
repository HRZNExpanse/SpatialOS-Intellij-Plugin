package com.improbable.spatialos.schema.intellij.parser.nodes.types;

import com.improbable.spatialos.schema.intellij.parser.nodes.EnumNode;
import com.improbable.spatialos.schema.intellij.parser.nodes.FileNode;
import com.improbable.spatialos.schema.intellij.parser.nodes.SchemaNode;
import com.improbable.spatialos.schema.intellij.parser.nodes.TypeNode;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class EnumInstanceEntryNode extends ArrayEntryNode {
    public String name;
    public EnumInstanceEntryNode() {
        super("Enum Or Empty Instance");
    }

    @Override
    public void highlight(@NotNull PsiElement element, @NotNull AnnotationHolder holder, FileNode root) {
        SchemaNode node = root.getNode(this.name);
        if(node == null) {
            holder.createErrorAnnotation(element, "Could not find type " + this.name).setTextAttributes(HighlighterColors.BAD_CHARACTER);
        } else if(node instanceof EnumNode.EnumEntryNode) {
            String[] names = this.name.split("\\.");
            int off = element.getTextOffset();
            for (int i = 0; i < names.length; i++) {
                String clazz = names[i];
                holder.createErrorAnnotation(new TextRange(off, off + clazz.length()), null).setTextAttributes(i == names.length - 1 ? DefaultLanguageHighlighterColors.STATIC_FIELD : DefaultLanguageHighlighterColors.METADATA);
                off += clazz.length() + 1;
            }

        } else if(node instanceof TypeNode) {
            int off = element.getTextOffset();
            for (String clazz : this.name.split("\\.")) {
                holder.createErrorAnnotation(new TextRange(off, off + clazz.length()), null).setTextAttributes(DefaultLanguageHighlighterColors.METADATA);
                off += clazz.length() + 1;
            }
        }
    }
}
