package com.improbable.spatialos.schema.intellij.parser.nodes;

import java.util.ArrayList;
import java.util.List;

public class ComponentNode extends SchemaNode {
    public String name;
    public int ID;

    public final List<FieldNode> fieldNodes = new ArrayList<>();
    public final List<EventNode> eventNodes = new ArrayList<>();
    public final List<FieldTypeNode> dataNodes = new ArrayList<>();
    public final List<CommandNode> commandNodes = new ArrayList<>();

    public ComponentNode() {
        super("Component");
    }
}
