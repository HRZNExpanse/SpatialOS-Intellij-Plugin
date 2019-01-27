package com.improbable.spatialos.schema.intellij.parser;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiReference;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class SchemaPsiElement extends ASTWrapperPsiElement implements PsiNameIdentifierOwner {
    public SchemaPsiElement(@NotNull ASTNode node) {
        super(node);
    }


    @Override
    public PsiReference getReference() {
        List<IElementType> accepted = Arrays.asList(
                SchemaParser.FIELD_ENUM_OR_INSTANCE,
                SchemaParser.FIELD_NEWINSTANCE_NAME,
                SchemaParser.ANNOTATION_TYPE_NAME,
                SchemaParser.FIELD_TYPE,
                SchemaParser.FIELD_REFERNCE
        );//todo: move this to a field
        if(accepted.contains(this.getNode().getElementType())) {
            return new SchemaReference(this, false);
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
