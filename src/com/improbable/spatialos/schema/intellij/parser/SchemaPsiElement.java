package com.improbable.spatialos.schema.intellij.parser;

import com.improbable.spatialos.schema.intellij.parser.nodes.EnumNode;
import com.improbable.spatialos.schema.intellij.parser.nodes.FileNode;
import com.improbable.spatialos.schema.intellij.parser.nodes.StringableNode;
import com.improbable.spatialos.schema.intellij.parser.nodes.types.EnumInstanceEntryNode;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.ElementManipulators;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SchemaPsiElement extends ASTWrapperPsiElement implements PsiNameIdentifierOwner {
    private String name = null;
    public SchemaPsiElement(@NotNull ASTNode node) {
        super(node);
        if(node.getElementType() instanceof StringableNode) {
            this.name = ((StringableNode) node.getElementType()).name();
        }
    }

    @Override
    public PsiReference getReference() {
        if(this.getNode().getElementType() instanceof EnumInstanceEntryNode) {
            return new SchemaReference(this, false);
//
        }
        return null;
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return null;
    }

    @Override
    public PsiElement setName(@NotNull String s) throws IncorrectOperationException {
        return null;
    }
}
