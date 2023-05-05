package com.fiit.dsa;

import com.fiit.dsa.bdd.BDD;
import org.openjdk.jmh.annotations.*;

import java.util.Arrays;

import static com.fiit.dsa.bdd.Exception.generateExpression;

@State(Scope.Thread)
public class Test {

    @Param({"100"})
    public int size;
    @Param({"3"})
    public int varCount;

    String varOrder;
    String[] expressions;

    @Setup(Level.Invocation)
    public void setUp() {
        setUpExpressions();
    }

    private void setUpExpressions() {
        this.expressions = new String[size];
        Arrays.fill(this.expressions, "");

        String[] _generated = generateExpression(varCount, 5);
        this.varOrder = _generated[0];
        this.expressions[0] = _generated[1];

        for (int i = 1; i < size; i++) {
            String[] generated = generateExpression(varCount, 5);
            expressions[i] = generated[1];
        }
    }

    @Benchmark
    public void create() {
        for (int i = 0; i < size; i++) {
            System.out.print("Creating BDD for expression: " + expressions[i] + "...");
            new BDD().create(varOrder, expressions[i]);
            System.out.println("Done");
        }
    }
}
