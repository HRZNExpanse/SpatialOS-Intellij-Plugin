package com.improbable.spatialos.schema.intellij.parser.nodes.types;

import com.improbable.spatialos.schema.intellij.parser.nodes.FileNode;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PrimitiveEntryNode extends ArrayEntryNode {
    private final AttributeType type;
    public List<String> value = new ArrayList<>();
    public PrimitiveEntryNode(AttributeType type) {
        super("Primitive Entry");
        this.type = type;
    }

    @Override
    public void highlight(@NotNull PsiElement element, @NotNull AnnotationHolder holder, FileNode root) {
        holder.createInfoAnnotation(element, null).setTextAttributes(this.type.attribute);
    }

    public enum AttributeType {
        BOOLEAN(DefaultLanguageHighlighterColors.KEYWORD),
        NUMBER(DefaultLanguageHighlighterColors.NUMBER),
        STRING(DefaultLanguageHighlighterColors.STRING);

        private final TextAttributesKey attribute;

        AttributeType(TextAttributesKey attribute) {
            this.attribute = attribute;
        }
    }
}
