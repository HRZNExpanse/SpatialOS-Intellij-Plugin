package com.improbable.spatialos.schema.intellij.parser.nodes;

import java.util.ArrayList;
import java.util.List;

public class TypeNode extends SchemaNode {

    public String name;
    public String singleName;

    public final List<FieldNode> fields = new ArrayList<>();
    public final List<TypeNode> nestedTypes = new ArrayList<>();
    public final List<EnumNode> nestedEnums = new ArrayList<>();

    public TypeNode() {
        super("Type");
    }
}
