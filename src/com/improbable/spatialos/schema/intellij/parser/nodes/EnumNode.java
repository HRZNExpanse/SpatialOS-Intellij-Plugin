package com.improbable.spatialos.schema.intellij.parser.nodes;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EnumNode extends SchemaNode {

    public String name;
    public String singleName;
    public final List<EnumEntryNode> entries = new ArrayList<>();

    public EnumNode() {
        super("Enum");
    }

    public static class EnumEntryNode extends SchemaNode {
       public final String name;
       public int ID;
        public EnumEntryNode(String name) {
            super("Enum Entry");
            this.name = name;
        }

        @Override
        public void highlight(@NotNull PsiElement element, @NotNull AnnotationHolder holder, FileNode root) {
            holder.createInfoAnnotation(element, null).setTextAttributes(DefaultLanguageHighlighterColors.STATIC_FIELD);
        }
    }
}
