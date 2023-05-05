package com.fiit.dsa.bdd;

import lombok.Data;

import static com.fiit.dsa.bdd.Exception.*;
import static com.fiit.dsa.bdd.Hash.hash;

@Data
public class BDD {

    private String[] varTypes;
    private String expression;
    private double realNumOfNodes;
    private double maxNumOfNodes;


    private Node root;
    private Node[] previous;
    private Node[] current;

    public BDD create(String expression, String order) {

        if (!validateVarTypes(expression, order) || expression.isBlank())
            return null;

        this.expression = expression;
        this.varTypes = order.toUpperCase().split("");
        this.realNumOfNodes = 1;

        this.previous = new Node[1];
        this.current = new Node[2];
        this.setRoot(this.previous[0] = new Node(null, this.expression, 0));

        for (int i = 0; i < this.varTypes.length; i++)
            this.maxNumOfNodes += Math.pow(2, i);

        int num, pos = 0;
        while (++pos <= this.varTypes.length) {
            num = 0;
            for (Node node : this.previous) {
                if (node == null)
                    continue;

                Node low = reduceExp(node, node.getValue(), pos, this.varTypes, true);
                Node high = reduceExp(node, node.getValue(), pos, this.varTypes, false);

                node.setLowChild(this.current[num++] = low);
                node.setHighChild(this.current[num++] = high);

                if (pos != this.varTypes.length)
                    this.realNumOfNodes += 2;
            }

            handleReduction(this);
            this.previous = this.current;
            this.current = new Node[this.previous.length * 2];
        }

        return this;
    }

    public String use(String input) {
        Node current = this.getRoot();
        String[] parts = input.split("");
        int pos = current.getLevel() - 1;

        if (parts.length != this.getVarTypes().length || !validateInput(input))
            return "-1";

        while (++pos < this.getVarTypes().length) {
            int var = current.getLevel();

            if (pos == var)
                current = parts[var].equals("0") ? current.getLowChild() : current.getHighChild();
        }

        return current.getValue();
    }

    private static void handleReduction(BDD bdd) {
        Node[] hash = new Node[bdd.current.length * 2];

        for (int i = 0; i < bdd.current.length; i++) {
            if (bdd.current[i] == null)
                continue;

            int pos = hash(bdd.current[i].getValue(), hash.length);

            while (hash[pos] != null) {
                if (hash[pos].getValue().equals(bdd.current[i].getValue()))
                    break;

                if (++pos >= hash.length)
                    pos = 0;
            }

            if (hash[pos] != null) {
                bdd.deleteNode(hash[pos], i);
                if (hash[pos].getLevel() != bdd.varTypes.length)
                    bdd.realNumOfNodes -= 1;
            } else
                hash[pos] = bdd.current[i];
        }

        for (int i = 0; i < bdd.previous.length; i++) {
            if (bdd.previous[i] == null)
                continue;

            Node low = bdd.previous[i].getLowChild();
            Node[] parents = bdd.previous[i].getParents();

            if (low.equals(bdd.previous[i].getHighChild())) {
                bdd.realNumOfNodes -= 1;
                if (bdd.previous[i].equals(bdd.getRoot()))
                    bdd.setRoot(low);

                low.setParents(parents, bdd.previous[i]);
                bdd.previous[i] = low.deleteParent(bdd.previous[i]);
            }
        }
    }

    private void deleteNode(Node first, int second) {
        Node[] parents = this.current[second].getParents();

        for (Node parent : parents) {
            if (parent != null) {
                if (parent.getLowChild().equals(this.current[second]))
                    parent.setLowChild(first);
                else
                    parent.setHighChild(first);
                first.setParents(parents, null);
            }
        }

        this.current[second] = null;
    }

    private static Node reduceExp(Node parent, String expression, int varPos, String[] varTypes, boolean flag) {
        if (varPos == varTypes.length + 1)
            return new Node(
                    parent,
                    expression.equals("1") ? "1" : "0",
                    varTypes.length
            );

        return new Node(
                parent,
                handleException(expression, varTypes[varPos - 1], flag),
                varPos
        );
    }

    public void traverseDiagram() {
        this.traverse(this.root, this.root.getLevel());
    }

    private void traverse(Node node, int level) {
        if (node != null) {
            this.traverse(node.getHighChild(), (node.getHighChild() == null ? this.varTypes.length : node.getHighChild().getLevel()));
            for (int i = 0; i < level; i++) {
                System.out.print("      ");
            }
            System.out.println((level == 0 ? "" : this.varTypes[level - 1] + ": ") + "(" + node.getValue() + ")" + " -- " + node + "\n");
            this.traverse(node.getLowChild(), (node.getLowChild() == null ? this.varTypes.length : node.getLowChild().getLevel()));
        }
    }

}
