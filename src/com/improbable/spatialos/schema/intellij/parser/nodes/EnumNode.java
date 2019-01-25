package com.improbable.spatialos.schema.intellij.parser.nodes;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EnumNode extends SchemaNode implements StringableNode {

    public String name;
    public String singleName;
    public final List<EnumEntryNode> entries = new ArrayList<>();

    public EnumNode() {
        super("Enum");
    }

    @Override
    public String name() {
        return this.name;
    }

    public static class EnumEntryNode extends SchemaNode implements StringableNode {
       public final String name;
       public int ID;
        public EnumEntryNode(String name) {
            super("Enum Entry");
            this.name = name;
        }

        @Override
        public void highlight(@NotNull PsiElement element, @NotNull AnnotationHolder holder, FileNode root) {
            holder.createInfoAnnotation(element, null).setTextAttributes(DefaultLanguageHighlighterColors.STATIC_FIELD);
            super.highlight(element, holder, root);
        }

        @Override
        public String name() {
            return this.name;
        }
    }
}
