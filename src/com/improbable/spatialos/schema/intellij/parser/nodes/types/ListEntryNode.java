package com.improbable.spatialos.schema.intellij.parser.nodes.types;

import java.util.LinkedList;
import java.util.List;

public class ListEntryNode extends ArrayEntryNode {
    public List<ArrayEntryNode> entries = new LinkedList<>();
    public ListEntryNode() {
        super("Array Entry");
    }
}
