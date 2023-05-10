package com.fiit.dsa.bdd;

import lombok.Data;

import java.util.*;

import static com.fiit.dsa.bdd.Expression.*;

@Data
public class BDD {

    private String[] varTypes;
    private String expression;
    private double numberOfNodesAfterReduction;
    private double numberOfNodes;


    private Node root;
    private Node[] previous;
    private Node[] current;

    /**
     * Creates a BDD from an expression and a variable order
     *
     * @param expression Expression to be used
     * @param order      Variable order to be used
     * @return BDD created from the expression and variable order
     */
    public BDD create(String expression, String order) {

        if (validateVarTypes(expression, order) || expression.isBlank())
            return null;

        this.expression = expression;
        this.varTypes = order.toUpperCase().split("");
        this.numberOfNodesAfterReduction = 1;

        this.previous = new Node[1];
        this.current = new Node[2];
        this.setRoot(this.previous[0] = new Node(null, this.expression, 0));

        for (int i = 0; i < this.varTypes.length; i++)
            this.numberOfNodes += Math.pow(2, i);

        int num, pos = 0;
        while (++pos <= this.varTypes.length) {
            num = 0;
            for (Node node : this.previous) {
                if (node == null)
                    continue;

                Node low = reduceExpression(node, node.getValue(), pos, this.varTypes, true);
                Node high = reduceExpression(node, node.getValue(), pos, this.varTypes, false);

                node.setLowChild(this.current[num++] = low);
                node.setHighChild(this.current[num++] = high);

                if (pos != this.varTypes.length)
                    this.numberOfNodesAfterReduction += 2;
            }

            handleReduction(this);
            this.previous = this.current;
            this.current = new Node[this.previous.length * 2];
        }

        return this;
    }

    public BDD createWithBestOrder(String expression) {
        String[] variables = new HashSet<>(Arrays.asList(expression.toUpperCase().replaceAll("\\+", "").split(""))).toArray(new String[0]);
        String[] orders = generateOrderVariants(String.join("", variables));

        Map<AbstractMap.SimpleEntry<String, Float>, BDD> bdds = new HashMap<>();

        for (int i = 0; i < variables.length; i++) {
            BDD bdd;
            float time = System.nanoTime();

            bdd = new BDD().create(expression, orders[i]);

            time = System.nanoTime() - time;

            bdds.put(new AbstractMap.SimpleEntry<>(orders[i], time), bdd);
        }

        return bdds.entrySet().stream()
                .min((_simpleEntity1, _simpleEntity2) -> Float.compare(_simpleEntity1.getKey().getValue(), _simpleEntity2.getKey().getValue()))
                .get().getValue();
    }

    /**
     * @param input Input for which the BDD is used
     * @return Result of using an input vector on the BDD
     */
    public String use(String input) {
        String[] parts = input.split("");
        Node current = this.getRoot();
        int position = current.getLevel() - 1;

        if (parts.length != this.getVarTypes().length || !validateInput(input))
            return "-1";

        while (++position < this.getVarTypes().length) {
            int variable = current.getLevel();

            if (position == variable)
                current = parts[variable].equals("0")
                        ? current.getLowChild()
                        : current.getHighChild();
        }

        return current.getValue();
    }

    /**
     * @param bdd BDD for which the reduction is performed
     */
    private static void handleReduction(BDD bdd) {
        Node[] hash = new Node[bdd.current.length * 2];

        for (int i = 0; i < bdd.current.length; i++) {
            if (bdd.current[i] == null)
                continue;

            int position = Math.abs(bdd.current[i].getValue().hashCode() % hash.length);

            while (hash[position] != null) {
                if (hash[position].getValue().equals(bdd.current[i].getValue()))
                    break;

                if (++position >= hash.length)
                    position = 0;
            }

            if (hash[position] != null) {
                bdd.deleteNode(hash[position], i);

                if (hash[position].getLevel() != bdd.varTypes.length)
                    bdd.numberOfNodesAfterReduction -= 1;

            } else
                hash[position] = bdd.current[i];
        }

        for (int i = 0; i < bdd.previous.length; i++) {
            if (bdd.previous[i] == null)
                continue;

            Node low = bdd.previous[i].getLowChild();
            Node[] parents = bdd.previous[i].getParents();

            if (low.equals(bdd.previous[i].getHighChild())) {
                bdd.numberOfNodesAfterReduction -= 1;

                if (bdd.previous[i].equals(bdd.getRoot()))
                    bdd.setRoot(low);

                low.setParents(parents, bdd.previous[i]);
                bdd.previous[i] = low.deleteParent(bdd.previous[i]);
            }
        }
    }

    /**
     * @param first  Node to be changed on
     * @param second Position of the node to be deleted
     */
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

    /**
     * @param parent     Parent node
     * @param expression Expression to be reduced
     * @param varPos     Position of the variable
     * @param varTypes   Variable types
     * @param flag       Flag for the expression
     * @return Reduced expression
     */
    private static Node reduceExpression(Node parent, String expression, int varPos, String[] varTypes, boolean flag) {
        if (varPos == varTypes.length + 1)
            return new Node(
                    parent,
                    expression.equals("1") ? "1" : "0",
                    varTypes.length
            );

        return new Node(
                parent,
                handleExpression(expression, varTypes[varPos - 1], flag),
                varPos
        );
    }

    /**
     * @param expression Expression to be handled
     */
    private static String handleExpression(String expression, String upper, boolean flag) {
        String lower = upper.toLowerCase();

        if (expression.equals("1") || expression.equals("0"))
            return expression;

        String[] parts = expression.contains("+")
                ? expression.split("\\+")
                : new String[]{expression};

        String[] hash = new String[parts.length * 2];

        for (String part : parts)
            if ((flag && part.equals(lower)) || (!flag && part.equals(upper)))
                return "1";

        if ((flag && expression.equals(lower)) || (!flag && expression.equals(upper)))
            return "1";

        if ((!flag && expression.equals(lower)) || (flag && expression.equals(upper)))
            return "0";

        int temp = 0, count = 0;
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].isBlank())
                continue;
            if (parts[i].contains(flag ? upper : lower)) {
                if (parts[i].contains(lower) && parts[i].contains(upper)) {
                    if (++count == parts.length)
                        return "0";
                }
                parts[i] = "";
                temp++;
            } else if (parts[i].contains(flag ? lower : upper)) {
                parts[i] = parts[i].replaceAll(flag ? lower : upper, "");
                if (parts[i].isBlank())
                    temp++;
            }
        }
        temp = parts.length - temp - 1;

        for (int i = 0; i < parts.length; i++) {
            if (parts[i].isBlank())
                continue;

            int pos = Math.abs(parts[i].hashCode() % hash.length);
            while (hash[pos] != null) {
                if (hash[pos].equals(parts[i]))
                    break;

                if (++pos >= hash.length)
                    pos = 0;
            }
            if (hash[pos] != null) {
                parts[i] = "";
                temp--;
            } else
                hash[pos] = parts[i];
        }

        lower = expression;
        StringBuilder expressionBuilder = new StringBuilder();

        for (String part : parts) {
            if (part.isBlank())
                continue;
            else
                expressionBuilder.append(part);

            if (temp-- > 0)
                expressionBuilder.append("+");
        }

        expression = expressionBuilder.toString();
        if (expression.isBlank()) {
            if (lower.contains(upper))
                expression = flag ? "0" : "1";
            else
                expression = flag ? "1" : "0";
        }

        return expression;
    }

}
