package com.improbable.spatialos.schema.intellij.parser.nodes;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class FieldTypeNode extends SchemaNode implements StringableNode {
    private static final List<String> KEYWORD_TYPES = Arrays.asList(
            "option", "list", "map", "double", "float", "string", "bytes", "int32", "int64", "uint32", "uint64",
            "sint32", "sint64", "fixed32", "fixed64", "sfixed32", "sfixed64", "bool");
    public String name;

    public final List<FieldTypeNode> generics = new ArrayList<>();
    private final TextAttributesKey attribute;


    public FieldTypeNode() {
        this(null);
    }


    public FieldTypeNode(TextAttributesKey attribute) {
        super("Field Node");
        this.attribute = attribute;
    }

    @Override
    public void highlight(@NotNull PsiElement element, @NotNull AnnotationHolder holder, FileNode root) {
        if(this.attribute != null) {
            holder.createInfoAnnotation(element, null).setTextAttributes(this.attribute);
        } else if(KEYWORD_TYPES.contains(this.name)) {
            holder.createInfoAnnotation(element, null).setTextAttributes(DefaultLanguageHighlighterColors.KEYWORD);
        }
        super.highlight(element, holder, root);
    }

    public String generateName() {
        StringBuilder out = new StringBuilder(this.name);
        if(!this.generics.isEmpty()) {
            out.append("<");

            Iterator<FieldTypeNode> iterator = this.generics.iterator();
            while(true) {
                out.append(iterator.next().name);
                if(iterator.hasNext()) {
                    out.append(", ");
                    continue;
                }
                break;
            }

            out.append(">");
        }
        return out.toString();
    }

    @Override
    public String name() {
        return this.name;
    }
}
