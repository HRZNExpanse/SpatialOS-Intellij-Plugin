package com.improbable.spatialos.schema.intellij.parser.nodes;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class PackageNode extends SchemaNode {
    public String packageName;
    public PackageNode() {
        super("Package");
    }

    @Override
    public void highlight(@NotNull PsiElement element, @NotNull AnnotationHolder holder, FileNode root) {
//        holder.createInfoAnnotation(element, null).setTextAttributes(DefaultLanguageHighlighterColors.KEYWORD);
    }
}
