package com.improbable.spatialos.schema.intellij.parser;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class SchemaReference extends PsiReferenceBase<PsiElement> {

    public SchemaReference(@NotNull PsiElement element, boolean soft) {
        super(element, soft);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        if(this.myElement.getNode().getElementType() == SchemaParser.FIELD_REFERNCE) {
            PsiElement type = SchemaAnnotator.resolveElement(this.myElement, this.myElement.getParent().getParent().getChildren()[0].getText() + "." + this.myElement.getText());
            if(type != null) {
                return type.getChildren()[1];
            }
            return null;
        }
        if(this.myElement.getNode().getElementType() == SchemaParser.ANNOTATION_TYPE_NAME) {
            PsiElement type =  SchemaAnnotator.resolveElement(this.myElement, this.myElement.getText());
            if(type != null) {
                return type.getChildren()[1];
            }
            return null;
        }
        if(this.myElement.getNode().getElementType() == SchemaParser.FIELD_TYPE) {
            PsiElement type =  SchemaAnnotator.resolveElement(this.myElement, this.myElement.getText());
            if(type != null) {
                return type.getChildren()[1];
            }
            return null;
        }
        if(this.myElement.getNode().getElementType() == SchemaParser.FIELD_NEWINSTANCE_NAME) {
            return SchemaAnnotator.resolveElement(this.myElement, this.myElement.getText());
        }
        System.out.println(this.myElement);
        return null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[] {
                "test"
        };
    }
}
