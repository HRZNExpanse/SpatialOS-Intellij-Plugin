package com.improbable.spatialos.schema.intellij.parser.nodes;

import com.improbable.spatialos.schema.intellij.SchemaLanguage;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

//Look. I know this is not the right way to do this. I know element types are meant to be singletons,
//and not dynamic types. In ideal world, I'd have the PsiElement figure out what part is what from the
//parsed node, rather than directly add it all into the element type. I get that it doesn't really make
//much sense for it to be how it is at the moment. I want to rewrite this as soon as i can, i just need
//to finish other parts of it first.
// </rant>
public class SchemaNode extends IElementType {
    public PsiElement element;
    public SchemaNode(@NotNull String debugName) {
        super(debugName, SchemaLanguage.SCHEMA_LANGUAGE);
    }

    public void highlight(@NotNull PsiElement element, @NotNull AnnotationHolder holder, FileNode root) {
        this.element = element;
    }
}
