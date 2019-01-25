package com.improbable.spatialos.schema.intellij.parser;

import com.improbable.spatialos.schema.intellij.SchemaLanguage;
import com.improbable.spatialos.schema.intellij.parser.nodes.*;
import com.improbable.spatialos.schema.intellij.parser.nodes.types.*;
import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.regex.Pattern;

public class SchemaParser implements PsiParser {
    public static final SchemaParser SCHEMA_PARSER = new SchemaParser();

    public static final String KEYWORD_PACKAGE = "package";
    public static final String KEYWORD_IMPORT = "import";
    public static final String KEYWORD_ENUM = "enum";
    public static final String KEYWORD_TYPE = "type";
    public static final String KEYWORD_COMPONENT = "component";
    public static final String KEYWORD_OPTION = "option";
    public static final String KEYWORD_ID = "id";
    public static final String KEYWORD_DATA = "data";
    public static final String KEYWORD_EVENT = "event";
    public static final String KEYWORD_COMMAND = "command";
    public static final String KEYWORD_ANNOTATION_START = "[";

    public static final IFileElementType SCHEMA_FILE = new IFileElementType(SchemaLanguage.SCHEMA_LANGUAGE);

    public static final IElementType KEYWORD = new Node("Keyword");
    public static final IElementType DEFINITION_NAME = new Node("Definition Name");

    public static final IElementType PACKAGE_DEFINITION = new Node("Package Definition");
    public static final IElementType PACKAGE_NAME = new Node("Package Name");

    public static final IElementType IMPORT_DEFINITION = new Node("Import Definition");
    public static final IElementType IMPORT_FILENAME = new Node("Import Filename");

    public static final IElementType OPTION_DEFINITION = new Node("Option Definition");
    public static final IElementType OPTION_NAME = new Node("Option Name");
    public static final IElementType OPTION_VALUE = new Node("Option Value");

    public static final IElementType TYPE_NAME = new Node("Type Name");
    public static final IElementType TYPE_PARAMETER_NAME = new Node("Type Parameter Name");

    public static final IElementType FIELD_TYPE = new Node("Field Type");
    public static final IElementType FIELD_NAME = new Node("Field Name");
    public static final IElementType FIELD_NUMBER = new Node("Field Number");

    public static final IElementType ENUM_DEFINITION = new Node("Enum Definition");
    public static final IElementType ENUM_VALUE_DEFINITION = new Node("Enum Value Definition");

    public static final IElementType DATA_DEFINITION = new Node("Data Definition");
    public static final IElementType FIELD_DEFINITION = new Node("Field Definition");
    public static final IElementType EVENT_DEFINITION = new Node("Event Definition");

    public static final IElementType TYPE_DEFINITION = new Node("Type Definition");
    public static final IElementType COMPONENT_DEFINITION = new Node("Component Definition");
    public static final IElementType COMPONENT_ID_DEFINITION = new Node("Component ID Definition");

    public static final IElementType COMMAND_DEFINITION = new Node("Command Definition");
    public static final IElementType COMMAND_NAME = new Node("Command Name");
    public static final IElementType ANNOTATION = new Node("Annotation Definition");
    public static final IElementType ANNOTATION_FIELD = new Node("Annotation Field");
    public static final IElementType FIELD_ARRAY = new Node("Field Array");

    public static final Pattern OPTION_BOOL = Pattern.compile("(?i)(?:true|false)");
    public static final Pattern OPTION_INTEGER = Pattern.compile("\\d");
    public static final Pattern OPTION_DECIMAL = Pattern.compile("\\d+\\.\\d+");
    public static final Pattern OPTION_STRING =  Pattern.compile("\".*?\"");

    public static final List<String> OPTION_BOOL_TYPE = Collections.singletonList("bool");
    public static final List<String> OPTION_INTEGER_TYPE = Arrays.asList("uint32", "uint64", "int32", "int64", "sint32", "sint64", "fixed32", "fixed64", "sfixed32", "sfixed64");
    public static final List<String> OPTION_DECIMAL_TYPE = Arrays.asList("float", "double");
    public static final List<String> OPTION_STRING_TYPE = Arrays.asList("string", "bytes");


    private static class Node extends IElementType {
        public Node(String debugName) {
            super(debugName, SchemaLanguage.SCHEMA_LANGUAGE);
        }
    }

    public static class RangedNode extends Node {

        public final List<RangedNodeEntry> entries = new ArrayList<>();

        public RangedNode(String debugName) {
            super(debugName);
        }

        public RangedNode addEntry(int from, int to, TextAttributesKey attributes) {
            entries.add(new RangedNodeEntry(from, to, attributes));
            return this;
        }
    }

    public static class RangedNodeEntry {
        public final int from;
        public final int to;
        public final TextAttributesKey attributes;

        private RangedNodeEntry(int from, int to, TextAttributesKey attributes) {
            this.from = from;
            this.to = to;
            this.attributes = attributes;
        }
    }

    @Override
    public @NotNull ASTNode parse(@NotNull IElementType root, @NotNull PsiBuilder builder) {
        Instance instance = new Instance(builder);
        instance.parseSchemaFile(root);
        return builder.getTreeBuilt();
    }

    private static class Instance {
        private PsiBuilder builder;
        private enum Construct {
            STATEMENT,
            BRACES,
            TOP_LEVEL,
        }

        public Instance(@NotNull PsiBuilder builder) {
            this.builder = builder;
        }

        private void error(Construct construct, String s, Object... args) { //tood: remove
            this.error(null, null, construct, s, args);
        }

            private void error(@Nullable PsiBuilder.Marker marker, @Nullable IElementType type, Construct construct, String s, Object... args) {
            if(marker != null) {
                marker.done(type == null ? new MiscNode(null) : type);
            }
            String errorMessage = String.format(s, args);
            PsiBuilder.Marker errorMarker = builder.mark();

            while (builder.getTokenType() != null && !builder.eof()) {
                if ((construct == Construct.STATEMENT || construct == Construct.TOP_LEVEL) &&
                    isToken(SchemaLexer.SEMICOLON)) {
                    errorMarker.error(errorMessage);
                    builder.advanceLexer();
                    return;
                }
                if ((construct == Construct.BRACES || construct == Construct.TOP_LEVEL) &&
                    isToken(SchemaLexer.RBRACE)) {
                    errorMarker.error(errorMessage);
                    builder.advanceLexer();
                    return;
                }
                if (construct == Construct.STATEMENT && isToken(SchemaLexer.RBRACE)) {
                    errorMarker.error(errorMessage);
                    return;
                }
                builder.advanceLexer();
            }
            errorMarker.error(errorMessage);
        }

        private String getTokenText() {
            return builder.getTokenText() == null ? "<EOF>" : builder.getTokenText();
        }

        private String getIdentifier() {
            return builder.getTokenText() == null ? "" : builder.getTokenText();
        }

        private int getInteger() {
            if (builder.getTokenText() == null) {
                return 0;
            }
            try {
                return Integer.parseInt(builder.getTokenText());
            } catch (NumberFormatException e) {
                return 0;
            }
        }

        private String removeQuotes() {
            if (!isToken(SchemaLexer.STRING)) {
                return "";
            }
            String text = builder.getTokenText();
            return text != null && text.length() > 2 ? text.substring(1, text.length() - 2) : "";
        }

        private boolean isToken(IElementType token) {
            return builder.getTokenType() == token;
        }

        private boolean isIdentifier(@NotNull String identifier) {
            return builder.getTokenType() == SchemaLexer.IDENTIFIER &&
                    builder.getTokenText() != null && builder.getTokenText().equals(identifier);
        }

        private void consumeTokenAs(@Nullable IElementType nodeType) {
            PsiBuilder.Marker marker = nodeType == null ? null : builder.mark();
            builder.advanceLexer();
            if (marker != null) {
                marker.done(nodeType);
            }
        }

        private FieldTypeNode parseType(FieldTypeNode dest) {
            PsiBuilder.Marker typeMarker = builder.mark();
            dest.name = this.getIdentifier();
            consumeTokenAs(dest);
            if (!isToken(SchemaLexer.LANGLE)) { //No generics
                typeMarker.done(dest);
                return dest;
            }
            FieldTypeNode firstGen = new FieldTypeNode();
            consumeTokenAs(null); //<
            if (!isToken(SchemaLexer.IDENTIFIER)) {
                typeMarker.drop();
                error(typeMarker, firstGen, Construct.STATEMENT, "Expected typename after '%s'.", dest.name);
                return dest;
            }
            firstGen.name = this.getIdentifier();
            dest.generics.add(firstGen);
            consumeTokenAs(firstGen);
            while (true) {
                if (isToken(SchemaLexer.RANGLE)) {
                    consumeTokenAs(null);
                    break;
                }
                if (isToken(SchemaLexer.COMMA)) {
                    consumeTokenAs(null);
                    if (!isToken(SchemaLexer.IDENTIFIER)) {
                        typeMarker.drop();
                        error(typeMarker, dest, Construct.STATEMENT, "Expected typename after ','.");
                        return dest;
                    }
                    FieldTypeNode generic = new FieldTypeNode();
                    generic.name = this.getIdentifier();
                    dest.generics.add(generic);
                    consumeTokenAs(generic);
                    continue;
                }
                typeMarker.drop();
                error(typeMarker, dest, Construct.STATEMENT, "Invalid '%s' inside <>.", this.getTokenText());
                return dest;
            }
            typeMarker.done(FIELD_TYPE);
            return dest;
        }

        private FieldNode parseFieldDefinition() {
            PsiBuilder.Marker marker = builder.mark();
            FieldNode node = new FieldNode();
            FieldTypeNode type = new FieldTypeNode();
            parseType(type);
            node.type = type;
            String typeName = type.generateName();
            if (!isToken(SchemaLexer.IDENTIFIER)) {
                error(marker, node, Construct.STATEMENT, "Expected field name after '%s'.", typeName);
                return node;
            }
            node.name = this.getIdentifier();
            consumeTokenAs(node);
            if (!isToken(SchemaLexer.EQUALS)) {
                error(marker, null, Construct.STATEMENT,
                      "Expected '=' after '%s %s'.", typeName, node.name);
                return node;
            }
            consumeTokenAs(null);
            if (!isToken(SchemaLexer.INTEGER)) {
                error(marker, null, Construct.STATEMENT,
                      "Expected field number after '%s %s = '.", typeName, node.name);
                return node;
            }
            node.ID = this.getInteger();
            consumeTokenAs(null);
            if (!isToken(SchemaLexer.SEMICOLON)) {
                error(marker, null, Construct.STATEMENT,
                      "Expected ';' after '%s %s = %d'.", typeName, node.name, node.ID);
                return node;
            }
            consumeTokenAs(null);
            marker.done(FIELD_DEFINITION);
            return node;
        }

        private int parseComponentIdDefinition() {
            PsiBuilder.Marker marker = builder.mark();
            consumeTokenAs(new MiscNode());
            if (!isToken(SchemaLexer.EQUALS)) {
                error(marker, null, Construct.STATEMENT,
                      "Expected '=' after '%s'.", KEYWORD_ID);
                return 0;
            }
            consumeTokenAs(null);
            if (!isToken(SchemaLexer.INTEGER)) {
                error(marker, null, Construct.STATEMENT,
                      "Expected integer ID value after '%s = '.", KEYWORD_ID);
                return 0;
            }
            int value = getInteger();
            consumeTokenAs(null);
            if (!isToken(SchemaLexer.SEMICOLON)) {
                error(marker, null, Construct.STATEMENT,
                      "Expected ';' after '%s = %d'.", KEYWORD_ID, value);
                return 0 ;
            }
            consumeTokenAs(null);
            marker.done(COMPONENT_ID_DEFINITION);
            return value;
        }

        private FieldTypeNode parseDataDefinition() {
            PsiBuilder.Marker marker = builder.mark();
            FieldTypeNode node = new FieldTypeNode();
            consumeTokenAs(new MiscNode());
            if (!isToken(SchemaLexer.IDENTIFIER)) {
                error(marker, node, Construct.STATEMENT, "Expected typename after '%s'.", KEYWORD_DATA);
                return node;
            }
            parseType(node);
            if (!isToken(SchemaLexer.SEMICOLON)) {
                error(marker, null, Construct.STATEMENT,
                        "Expected ';' after '%s %s'.", KEYWORD_DATA, node.generateName());
                return node;
            }
            consumeTokenAs(null);
            marker.done(DATA_DEFINITION);
            return node;
        }

        private EventNode parseEventDefinition() {
            PsiBuilder.Marker marker = builder.mark();
            EventNode node = new EventNode();

            consumeTokenAs(new MiscNode());
            if (!isToken(SchemaLexer.IDENTIFIER)) {
                error(marker, new FieldTypeNode(), Construct.STATEMENT, "Expected typename after '%s'.", KEYWORD_EVENT);
                return node;
            }
            FieldTypeNode fieldType = node.node = parseType(new FieldTypeNode());
            String typeName = fieldType.generateName();
            if (!isToken(SchemaLexer.IDENTIFIER)) {
                error(marker, node, Construct.STATEMENT,
                      "Expected field name after '%s %s'.", KEYWORD_EVENT, typeName);
                return node;
            }
            node.name = this.getIdentifier();
            consumeTokenAs(node);
            if (!isToken(SchemaLexer.SEMICOLON)) {
                error(marker, null, Construct.STATEMENT,
                        "Expected ';' after '%s %s %s'.", KEYWORD_EVENT, typeName, node.name);
                return node;
            }
            consumeTokenAs(null);
            marker.done(EVENT_DEFINITION);
            return node;
        }

        private AnnotationNode parseAnnotation() {
            PsiBuilder.Marker marker = builder.mark();
            AnnotationNode node = new AnnotationNode();
            consumeTokenAs(null);

            if (!isToken(SchemaLexer.IDENTIFIER)) {
                error(marker, node, Construct.STATEMENT, "Expected name after '['.");
                return node;
            }

            node.name = this.getIdentifier();
            consumeTokenAs(node);
            if(isToken(SchemaLexer.LPARENTHESES)) { //If the annotation has fields
                if(builder.lookAhead(2) == SchemaLexer.EQUALS) { //fully-qualified names
                    consumeTokenAs(null);
                    while(true) {
                        if (!isToken(SchemaLexer.IDENTIFIER)) {
                            error(marker, null, Construct.STATEMENT, "Expected field identifier");
                            return node;
                        }
                        consumeTokenAs(null);
                        if (!isToken(SchemaLexer.EQUALS)) {
                            error(marker, null, Construct.STATEMENT, "Expected '='");
                            return node;
                        }
                        consumeTokenAs(null);
                        parseAnnotationField();

                        if(isToken(SchemaLexer.RPARENTHESES)) {
                            consumeTokenAs(null);
                            break;
                        }

                        if(!isToken(SchemaLexer.COMMA)) {
                            error(marker, null, Construct.STATEMENT, "Expected ',' or end of annotation");
                            return node;
                        }
                        consumeTokenAs(null);
                    }
                } else {
                    node.entries.addAll(parseAnnotationFieldArray(node.entries, node));
                }
            }

            if(!isToken(SchemaLexer.RBRACKET)) {
                error(marker, null, Construct.STATEMENT, "Expected end of annotation ']'");
                return node;
            }
            consumeTokenAs(null);

            marker.done(ANNOTATION);
            return node;
        }

        private List<ArrayEntryNode> parseAnnotationFieldArray(List<ArrayEntryNode> entries, SchemaNode fromNode) {
            PsiBuilder.Marker marker = builder.mark();
            consumeTokenAs(null);
            if(!isToken(SchemaLexer.RPARENTHESES)) { //Empty array
                while (true) {
                    if(builder.getTokenText() == null) { //Something gone wrong. Invalid input?
                        break;
                    }
                    entries.add(parseAnnotationField());
                    if(isToken(SchemaLexer.RPARENTHESES)) {
                        break;
                    }
                    if(!isToken(SchemaLexer.COMMA)) {
                        error(marker, null, Construct.STATEMENT, "Expected ',' or end of array");
                        return entries;
                    }
                    consumeTokenAs(null);
                }
            }
            consumeTokenAs(null);
            marker.done(FIELD_ARRAY);

            for (int i = 0; i < entries.size(); i++) {
                ArrayEntryNode entry = entries.get(i);
                entry.index = i;
                entry.fromType = fromNode;
            }

            return entries;
        }

        private ArrayEntryNode parseAnnotationField() {
            PsiBuilder.Marker marker = builder.mark();
            ArrayEntryNode node;
            for(;;) {
                if(isToken(SchemaLexer.LBRACKET)) { //Array
                    ListEntryNode privNode = new ListEntryNode();
                    node = privNode;
                    consumeTokenAs(null);
                    if(isToken(SchemaLexer.RBRACKET)) { //Empty array
                        consumeTokenAs(privNode);
                    } else {
                        while(true) {
                            ArrayEntryNode entry = parseAnnotationField();
                            entry.fromType = privNode;
                            privNode.entries.add(entry);
                            if(isToken(SchemaLexer.RBRACKET)) {
                                consumeTokenAs(entry);
                                break;
                            }
                            if(!isToken(SchemaLexer.COMMA)) {
                                error(marker, entry, Construct.STATEMENT, "Expected ',' or end of array");
                                return node;
                            }
                            consumeTokenAs(entry);
                        }
                    }
                    break;
                }

                if(isToken(SchemaLexer.LBRACE)) { //Map
                    MapEntryNode privNode = new MapEntryNode();
                    node = privNode;
                    consumeTokenAs(null);
                    if(isToken(SchemaLexer.RBRACE)) { //Empty map
                        consumeTokenAs(privNode);
                    } else {
                        while(true) {
                            ArrayEntryNode keyNode = parseAnnotationField();
                            if(!isToken(SchemaLexer.COLON)) {
                                error(marker, keyNode, Construct.STATEMENT, "Expected ':' in map");
                                return node;
                            }
                            consumeTokenAs(TYPE_NAME); // ':'
                            ArrayEntryNode entryNode = parseAnnotationField();

                            keyNode.fromType = node;
                            entryNode.fromType = node;

                            privNode.map.put(keyNode, entryNode);

                            if(isToken(SchemaLexer.RBRACE)) {
                                consumeTokenAs(null);
                                break;
                            }
                            if(!isToken(SchemaLexer.COMMA)) {
                                error(marker, entryNode, Construct.STATEMENT, "Expected ',' or end of map");
                                return node;
                            }
                            consumeTokenAs(null);
                        }
                    }
                    break;
                }

                if(builder.getTokenText() != null) {
                    String text = builder.getTokenText();
                    if(OPTION_BOOL.matcher(text).find()) {
                        PrimitiveEntryNode privNode = new PrimitiveEntryNode(PrimitiveEntryNode.AttributeType.BOOLEAN);
                        privNode.value.addAll(OPTION_BOOL_TYPE);
                        consumeTokenAs(privNode);
                        node = privNode;
                        break;
                    }
                    if(OPTION_INTEGER.matcher(text).find()) {
                        PrimitiveEntryNode privNode = new PrimitiveEntryNode(PrimitiveEntryNode.AttributeType.NUMBER);
                        node = privNode;
                        privNode.value.addAll(OPTION_INTEGER_TYPE);
                        consumeTokenAs(privNode);
                        if(isIdentifier(".")) {
                            consumeTokenAs(privNode);
                            if(!isToken(SchemaLexer.INTEGER)) {
                                error(marker, privNode, Construct.STATEMENT, "Illegal number");
                                return node;
                            }
                            consumeTokenAs(privNode);
                        }
                        break;
                    }
                    if(OPTION_STRING.matcher(text).find()) {
                        PrimitiveEntryNode privNode = new PrimitiveEntryNode(PrimitiveEntryNode.AttributeType.STRING);
                        privNode.value.addAll(OPTION_STRING_TYPE);
                        consumeTokenAs(privNode);
                        node = privNode;
                        break;
                    }
                }

                if(isToken(SchemaLexer.IDENTIFIER)) {
                    if(builder.lookAhead(1) == SchemaLexer.LPARENTHESES) { //Initiate a new object
                        NewInstanceNode priveNode = new NewInstanceNode();
                        priveNode.className = this.getIdentifier();
                        consumeTokenAs(priveNode);
                        parseAnnotationFieldArray(priveNode.entries, priveNode);
                        node = priveNode;
                        break;
                    } else { //Enum or empty method value
                        EnumInstanceEntryNode emen = new EnumInstanceEntryNode();
                        emen.name = builder.getTokenText();
                        node = emen;
                        consumeTokenAs(node);
                    }
                } else {
                    error((PsiBuilder.Marker) null,null, Construct.STATEMENT, "Error constructing array field. Could not figure out type for %s", builder.getTokenText());
                    return new ArrayEntryNode("~<INVALID>~");
                }
                break;
            }
            marker.done(ANNOTATION_FIELD);
            return node;
        }


        private CommandNode parseCommandDefinition() {
            PsiBuilder.Marker marker = builder.mark();

            CommandNode node = new CommandNode();

            consumeTokenAs(new MiscNode());
            if (!isToken(SchemaLexer.IDENTIFIER)) {
                error(marker, new FieldTypeNode(), Construct.STATEMENT, "Expected command response after 'command'.");
                return node;
            }
            node.reply = parseType(new FieldTypeNode());
            if (!isToken(SchemaLexer.IDENTIFIER)) {
                error(marker, node, Construct.STATEMENT,
                        "Expected command name after 'command %s'.", node.reply.name);
                return node;
            }
            node.name = this.getIdentifier();
            consumeTokenAs(node);
            if (!isToken(SchemaLexer.LPARENTHESES)) {
                error(marker, null, Construct.STATEMENT,
                        "Expected '(' after 'command %s %s'.", node.reply.name, node.name);
                return node;
            }
            consumeTokenAs(null);
            if (!isToken(SchemaLexer.IDENTIFIER)) {
                error(marker, new FieldTypeNode(), Construct.STATEMENT,
                        "Expected command request after 'command %s %s('.", node.reply.name, node.name);
                return node;
            }
            node.request = parseType(new FieldTypeNode());
            if (!isToken(SchemaLexer.RPARENTHESES)) {
                error(marker, null, Construct.STATEMENT,
                        "Expected ')' after 'command %s %s(%s'.", node.reply.name, node.name, node.request.name);
                return node;
            }
            consumeTokenAs(null);
            if (!isToken(SchemaLexer.SEMICOLON)) {
                error(marker, null, Construct.STATEMENT,
                        "Expected ';' after 'command %s %s(%s)'.", node.reply.name, node.name, node.request.name);
                return node;
            }
            consumeTokenAs(null);
            marker.done(COMMAND_DEFINITION);
            return node;
        }

        private void parseComponentContents(ComponentNode node) {
            while (true) {
                if (isIdentifier(KEYWORD_ID)) {
                    node.ID = parseComponentIdDefinition();
                    continue;
                }
                if (isIdentifier(KEYWORD_DATA)) {
                    node.dataNodes.add(parseDataDefinition());
                    continue;
                }
                if (isIdentifier(KEYWORD_EVENT)) {
                    node.eventNodes.add(parseEventDefinition());
                    continue;
                }
                if(isIdentifier(KEYWORD_COMMAND)) {
                    node.commandNodes.add(parseCommandDefinition());
                    continue;
                }
                if (isToken(SchemaLexer.IDENTIFIER)) {
                    node.fieldNodes.add(parseFieldDefinition());
                    continue;
                }
                return;
            }
        }

        private ComponentNode parseComponentDefinition() {
            PsiBuilder.Marker marker = builder.mark();
            ComponentNode node = new ComponentNode();
            consumeTokenAs(new MiscNode());
            if (!isToken(SchemaLexer.IDENTIFIER)) {
                error(marker, node, Construct.BRACES,
                      "Expected identifier after '%s'.", KEYWORD_COMPONENT);
                return node;
            }
            node.name = this.getIdentifier();
            consumeTokenAs(node);
            if (!isToken(SchemaLexer.LBRACE)) {
                error(marker, null, Construct.BRACES,
                      "Expected '{' after '%s %s'.", KEYWORD_COMPONENT, node.name);
                return node;
            }
            consumeTokenAs(null);
            parseComponentContents(node);
            if (!isToken(SchemaLexer.RBRACE)) {
                error(marker, null, Construct.BRACES,
                      "Invalid '%s' inside %s %s.", getTokenText(), KEYWORD_COMPONENT, node.name);
                return node;
            }
            consumeTokenAs(null);
            marker.done(COMPONENT_DEFINITION);
            return node;
        }

        private PackageNode parsePackageDefinition() {
            PsiBuilder.Marker marker = builder.mark();
            PackageNode node = new PackageNode();

            consumeTokenAs(new MiscNode());
            if (!isToken(SchemaLexer.STRING)) {
                error(marker, node, Construct.STATEMENT,
                        "Expected a package name after '%s'.", KEYWORD_PACKAGE);
                return node;
            }
            node.packageName = builder.getTokenText();
            consumeTokenAs(node);
            IElementType type = new MiscNode(DefaultLanguageHighlighterColors.SEMICOLON);
            if (!isToken(SchemaLexer.SEMICOLON)) {
                error(marker, type, Construct.STATEMENT,
                        "Expected ';' after %s definition.", KEYWORD_PACKAGE);
                return node;
            }
            consumeTokenAs(type);
            marker.done(PACKAGE_DEFINITION);
            return node;
        }

        private ImportNode parseImportDefinition() {
            PsiBuilder.Marker marker = builder.mark();
            ImportNode node = new ImportNode();

            consumeTokenAs(new MiscNode());
            if (!isToken(SchemaLexer.STRING)) {
                System.out.println(builder.getTokenType());
                error(marker, node, Construct.STATEMENT,
                        "Expected a quoted filename after '%s'.", KEYWORD_IMPORT);
                return node;
            }
            node.fileName = this.removeQuotes();
            consumeTokenAs(node);
            IElementType type = new MiscNode(DefaultLanguageHighlighterColors.SEMICOLON);
            if (!isToken(SchemaLexer.SEMICOLON)) {
                error(marker, type, Construct.STATEMENT,
                        "Expected ';' after '%s \"%s\"'.", KEYWORD_IMPORT, node.fileName);
                return node;
            }
            consumeTokenAs(type);
            marker.done(IMPORT_DEFINITION);
            return node;
        }

        private TypeNode parseTypeDefinition(@Nonnull String dir) {
            PsiBuilder.Marker marker = builder.mark();
            TypeNode node = new TypeNode();

            consumeTokenAs(new MiscNode());
            if (!isToken(SchemaLexer.IDENTIFIER)) {
                error(marker, node, Construct.BRACES, "Expected identifier after '%s'.", KEYWORD_TYPE);
                return node;
            }
            node.singleName = this.getIdentifier();
            node.name = dir + node.singleName;
            consumeTokenAs(node);
            if (!isToken(SchemaLexer.LBRACE)) {
                error(marker, null, Construct.BRACES, "Expected '{' after '%s %s'.", KEYWORD_TYPE, node.name);
                return node;
            }
            consumeTokenAs(null);
            parseTypeContents(node);
            if (!isToken(SchemaLexer.RBRACE)) {
                error(marker, null, Construct.BRACES,
                        "Invalid '%s' inside %s %s.", getTokenText(), KEYWORD_TYPE, node.name);
                return node;
            }
            consumeTokenAs(null);
            marker.done(TYPE_DEFINITION);
            return node;
        }

        private void parseTypeContents(TypeNode parent) {
            while (true) {
                if (isIdentifier(KEYWORD_ENUM)) { //Nested enum
                    parent.nestedEnums.add(parseEnumDefinition(parent.name + "."));
                    continue;
                }
                if (isIdentifier(KEYWORD_TYPE)) { //Nested types
                    parent.nestedTypes.add(parseTypeDefinition(parent.name + "."));
                    continue;
                }
                if (isToken(SchemaLexer.IDENTIFIER)) { //Fields
                    parent.fields.add(this.parseFieldDefinition());
                    continue;
                }

                return;
            }
        }

        private EnumNode parseEnumDefinition(@Nonnull String dir) {
            PsiBuilder.Marker marker = builder.mark();
            EnumNode node = new EnumNode();
            consumeTokenAs(new MiscNode());
            if (!isToken(SchemaLexer.IDENTIFIER)) {
                error(marker, node, Construct.BRACES, "Expected identifier after '%s'.", KEYWORD_ENUM);
                return node;
            }
            node.singleName = this.getIdentifier();
            node.name = dir + node.singleName;
            consumeTokenAs(node);
            if (!isToken(SchemaLexer.LBRACE)) {
                error(marker, null, Construct.BRACES, "Expected '{' after '%s %s'.", KEYWORD_ENUM, node.name);
                return node;
            }
            consumeTokenAs(null);
            parseEnumContents(node);
            if (!isToken(SchemaLexer.RBRACE)) {
                error(marker, null, Construct.BRACES,
                        "Invalid '%s' inside %s %s.", getTokenText(), KEYWORD_ENUM, node.name);
                return node;
            }
            consumeTokenAs(null);
            marker.done(ENUM_DEFINITION);
            return node;
        }

        private void parseEnumContents(EnumNode parent) {
            while (isToken(SchemaLexer.IDENTIFIER)) {
                PsiBuilder.Marker marker = builder.mark();
                EnumNode.EnumEntryNode node = new EnumNode.EnumEntryNode(this.getIdentifier());
                consumeTokenAs(node);
                if (!isToken(SchemaLexer.EQUALS)) {
                    error(marker, null, Construct.STATEMENT, "Expected '=' after '%s'.", node.name);
                    continue;
                }
                consumeTokenAs(null);
                if (!isToken(SchemaLexer.INTEGER)) {
                    error(marker, null, Construct.STATEMENT,
                            "Expected integer enum value after '%s = '.", node.name);
                    continue;
                }
                node.ID = this.getInteger();
                consumeTokenAs(null);
                if (!isToken(SchemaLexer.SEMICOLON)) {
                    error(marker, null, Construct.STATEMENT,
                            "Expected ';' after '%s = %d'.", node.name, node.ID);
                    continue;
                }
                consumeTokenAs(null);
                marker.done(ENUM_VALUE_DEFINITION);
                parent.entries.add(node);
            }
        }
        private List<AnnotationNode> stackedAnnotations = new LinkedList<>(); //TODO: attach annotations

        @Nullable
        private void parseTopLevelDefinition(FileNode node) {
            if (isIdentifier(KEYWORD_PACKAGE)) {
                node.packageNode = parsePackageDefinition();
            } else if (isIdentifier(KEYWORD_IMPORT)) {
                node.importNodes.add(parseImportDefinition());
            } else if (isIdentifier(KEYWORD_ENUM)) {
                node.enumNodes.add(parseEnumDefinition(""));
            } else if (isIdentifier(KEYWORD_TYPE)) {
                node.typeNodes.add(parseTypeDefinition(""));
            } else if (isIdentifier(KEYWORD_COMPONENT)) {
               node.components.add(parseComponentDefinition());
            } else if(builder.getTokenText() != null && builder.getTokenText().equals(KEYWORD_ANNOTATION_START)) {
                stackedAnnotations.add(parseAnnotation());
            }
            else {
                error((PsiBuilder.Marker)null, null, Construct.TOP_LEVEL,
                      "Expected '%s', '%s', '%s', '%s', '%s' or an annotation definition at top-level.",
                      KEYWORD_PACKAGE, KEYWORD_IMPORT, KEYWORD_ENUM, KEYWORD_TYPE, KEYWORD_COMPONENT);
            }
        }

        public void parseSchemaFile(@NotNull IElementType root) {
            PsiBuilder.Marker outerMarker = builder.mark();
            PsiBuilder.Marker marker = builder.mark();
            FileNode node = new FileNode();
            while (builder.getTokenType() != null && !builder.eof()) {
                parseTopLevelDefinition(node);
            }
            marker.done(node);
            outerMarker.done(root);
        }
    }
}
