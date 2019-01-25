package com.improbable.spatialos.schema.intellij.parser;

import com.improbable.spatialos.schema.intellij.parser.nodes.FileNode;
import com.improbable.spatialos.schema.intellij.parser.nodes.SchemaNode;
import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class SchemaAnnotator implements Annotator {
    public static final List<String> OPTION_VALUES = Arrays.asList("true", "false");
    private static final List<String> BUILT_IN_GENERIC_TYPES = Arrays.asList("option", "list", "map");
    private static final List<String> BUILT_IN_TYPES = Arrays.asList(
            "double", "float", "string", "bytes", "int32", "int64", "uint32", "uint64", "sint32", "sint64", "fixed32",
            "fixed64", "sfixed32", "sfixed64", "bool"
    );

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if(element.getNode().getElementType() instanceof SchemaNode) {
            for (ASTNode child : element.getContainingFile().getNode().getChildren(null)) {
                if(child.getElementType() instanceof FileNode) {
                    ((SchemaNode) element.getNode().getElementType()).highlight(element, holder, (FileNode)child.getElementType());
                    break;
                }
            }
        }
//        if(element.getNode().getElementType() instanceof SchemaParser.RangedNode) {
//            for (SchemaParser.RangedNodeEntry entry : ((SchemaParser.RangedNode) element.getNode().getElementType()).entries) {
//                highlight(holder, element, entry.attributes, entry.from, entry.to);
//            }
//        }
//        if (element.getNode().getElementType() == SchemaParser.KEYWORD) {
//            highlight(holder, element, DefaultLanguageHighlighterColors.KEYWORD);
//        }
//        if (element.getNode().getElementType() == SchemaParser.OPTION_VALUE &&
//            OPTION_VALUES.contains(element.getText())) {
//            highlight(holder, element, DefaultLanguageHighlighterColors.NUMBER);
//        }
//        if (element.getNode().getElementType() == SchemaParser.TYPE_NAME &&
//            BUILT_IN_GENERIC_TYPES.contains(element.getText())) {
//            highlight(holder, element, DefaultLanguageHighlighterColors.KEYWORD);
//        }
//        if (element.getNode().getElementType() == SchemaParser.TYPE_PARAMETER_NAME ||
//             element.getNode().getElementType() == SchemaParser.TYPE_NAME) {
//            if (BUILT_IN_TYPES.contains(element.getText())) {
//                highlight(holder, element, DefaultLanguageHighlighterColors.KEYWORD);
//            } else {
//                highlight(holder, element, DefaultLanguageHighlighterColors.METADATA);
//            }
//        }
//        if(element.getNode().getElementType() == SchemaParser.COMMAND_NAME) {
//            highlight(holder, element, DefaultLanguageHighlighterColors.INSTANCE_METHOD);
//        }
//        if(element.getNode().getText().equals("{") || element.getNode().getText().equals("}") ||
//           element.getNode().getText().equals("[") || element.getNode().getText().equals("]")) {
//            highlight(holder, element, DefaultLanguageHighlighterColors.BRACKETS);
//        }
//        if(element.getNode().getText().equals("(") || element.getNode().getText().equals(")")) {
//            highlight(holder, element, DefaultLanguageHighlighterColors.PARENTHESES);
//        }
//        if(element.getNode().getText().equals(",")) {
//            highlight(holder, element, DefaultLanguageHighlighterColors.COMMA);
//        }
//        if(element.getNode().getText().equals(";")) {
//            highlight(holder, element, DefaultLanguageHighlighterColors.SEMICOLON);
//        }
    }

    private void highlight(@NotNull AnnotationHolder holder, @NotNull PsiElement element,
                           @NotNull TextAttributesKey attributes) {
        holder.createInfoAnnotation(element, null).setTextAttributes(attributes);
    }

    private void highlight(@NotNull AnnotationHolder holder, @NotNull PsiElement element,
                           @NotNull TextAttributesKey attributes, int start, int end) {
        holder.createInfoAnnotation(new TextRange(element.getTextOffset() + start, element.getTextOffset() + end), null).setTextAttributes(attributes);
    }
}