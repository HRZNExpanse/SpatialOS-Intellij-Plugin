package com.improbable.spatialos.schema.intellij.parser.nodes.types;

import java.util.HashMap;
import java.util.Map;

public class MapEntryNode extends ArrayEntryNode {
    public Map<ArrayEntryNode, ArrayEntryNode> map = new HashMap<>();
    public MapEntryNode() {
        super("Map Entry");
    }
}
