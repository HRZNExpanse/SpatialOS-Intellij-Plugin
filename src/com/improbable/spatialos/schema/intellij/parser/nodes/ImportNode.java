package com.improbable.spatialos.schema.intellij.parser.nodes;

public class ImportNode extends SchemaNode {
    public String fileName;
    public ImportNode() {
        super("Import");
    }
}
