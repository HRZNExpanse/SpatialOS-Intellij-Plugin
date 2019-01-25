package com.improbable.spatialos.schema.intellij.parser.nodes;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class FileNode extends SchemaNode {
    @Nullable
    public PackageNode packageNode;
    public final List<ImportNode> importNodes = new ArrayList<>();
    public final List<TypeNode> typeNodes = new ArrayList<>();
    public final List<EnumNode> enumNodes = new ArrayList<>();
    public final List<ComponentNode> components = new ArrayList<>();

    public FileNode() {
        super("Schema File");
    }
    
    @Nullable
    public SchemaNode getNode(String name) {
        String[] names = name.split("\\.");

        SchemaNode node = null;

        if(names.length == 2) {
            for (EnumNode enumNode : this.enumNodes) {
                if(enumNode.singleName.equals(names[0])) {
                    for (EnumNode.EnumEntryNode entry : enumNode.entries) {
                        if(entry.name.equals(names[1])) {
                            node = entry;
                        }
                    }
                }
            }
        }
        if(node == null) {
            TypeNode ref = null;
            for (TypeNode typeNode : this.typeNodes) {
                if(typeNode.singleName.equals(names[0])) {
                    ref = typeNode;
                }
            }
            if(ref != null) {
                for (int i = 1; i < names.length; i++) {
                    if(i == names.length - 2) { //Second last one
                        for (EnumNode nestedEnum : ref.nestedEnums) {
                            if(nestedEnum.singleName.equals(names[i])) {
                                for (EnumNode.EnumEntryNode entry : nestedEnum.entries) {
                                    if(entry.name.equals(names[i + 1])) {
                                        node = nestedEnum;
                                    }
                                }
                            }
                        }
                    }
                    boolean set = false;
                    for (TypeNode typeNode : ref.nestedTypes) {
                        if(typeNode.singleName.equals(names[i])) {
                            ref = typeNode;
                            set = true;
                        }
                    }
                    if(!set) {
                        ref = null;
                        break;
                    }
                }
            }
            if(node == null) {
                node = ref;
            }
        }


        return node;
    }
}
