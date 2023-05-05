package com.fiit.dsa.bdd;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Node {

    private String value;
    private final int level;

    private Node[] parents;
    private Node lowChild;
    private Node highChild;

    public Node(Node parent, String value, int level) {
        this.parents = new Node[1];
        this.value = value;
        this.level = level;
        this.parents[0] = parent;
    }

    public Node deleteParent(Node parent) {
        for (int i = 0; i < this.parents.length; i++) {
            if (this.parents[i] != null && this.parents[i].equals(parent))
                this.parents[i] = null;
        }

        return null;
    }

    public void setParents(Node[] parentsForNode, Node parent) {
        Node[] _parents = new Node[this.parents.length + parentsForNode.length];
        int pos = 0;

        for (Node node : this.parents) {
            _parents[pos++] = node;
        }

        for (Node parentForNode : parentsForNode) {
            _parents[pos++] = parentForNode;

            if (parent != null && parentForNode != null) {
                if (parentForNode.getLowChild().equals(parent))
                    parentForNode.setLowChild(this);
                else parentForNode.setHighChild(this);
            }
        }

        this.parents = _parents;
    }
}
