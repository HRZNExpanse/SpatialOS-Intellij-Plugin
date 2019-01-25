package com.improbable.spatialos.schema.intellij.parser.nodes;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MiscNode extends SchemaNode {
    private final TextAttributesKey attribute;

    public MiscNode() {
        this(DefaultLanguageHighlighterColors.KEYWORD);
    }

    public MiscNode(@Nullable TextAttributesKey attribute) {
        super("Keyword");
        this.attribute = attribute;
    }

    @Override
    public void highlight(@NotNull PsiElement element, @NotNull AnnotationHolder holder, FileNode root) {
        if(this.attribute != null) {
            holder.createInfoAnnotation(element, null).setTextAttributes(this.attribute);
            super.highlight(element, holder, root);
        }
    }
}
