package com.improbable.spatialos.schema.intellij.parser.nodes;

public class EventNode extends SchemaNode {
    public String name;
    public FieldTypeNode node;

    public EventNode() {
        super("Event Node");
    }
}
