package com.improbable.spatialos.schema.intellij.parser;

import com.improbable.spatialos.schema.intellij.parser.nodes.EnumNode;
import com.improbable.spatialos.schema.intellij.parser.nodes.FileNode;
import com.improbable.spatialos.schema.intellij.parser.nodes.SchemaNode;
import com.improbable.spatialos.schema.intellij.parser.nodes.types.EnumInstanceEntryNode;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SchemaReference extends PsiReferenceBase<PsiElement> {

    public SchemaReference(@NotNull PsiElement element, boolean soft) {
        super(element, soft);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        FileNode root = null;
        for (ASTNode child : this.myElement.getContainingFile().getNode().getChildren(null)) {
            if(child.getElementType() instanceof FileNode) {
                root = (FileNode) child.getElementType();
            }
        }
        if(root == null) {
            return null;
        }

        IElementType type = this.myElement.getNode().getElementType();
        if(type instanceof EnumInstanceEntryNode) {
            SchemaNode node = root.getNode(((EnumInstanceEntryNode) type).name);
            return node.element;
        }
        return null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[] {
                this.resolve()
        };
    }
}
