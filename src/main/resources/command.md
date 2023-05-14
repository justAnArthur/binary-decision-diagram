# BDD_create
```shell
graph output.csv -x 3 -y 2 -l "Time on creating" --figsize 1600x1000 --fontsize 14 --chain | \
graph output.csv -x 4 -y 2 -l "Time on using" \
--xlabel "Milliseconds" --ylabel "Number of variable in expression" --title "BDD_create" \
-o BDD_create.png
```

# BDD_createWithBestOrder
```shell
graph output_best_order.csv -x 3 -y 2 -l "Time on creating" --figsize 1600x1000 --fontsize 14 --chain | \
graph output_best_order.csv -x 4 -y 2 -l "Time on using" \
--xlabel "Milliseconds" --ylabel "Number of variable in expression" --title "BDD_createWithBestOrder" \
-o BDD_createWithBestOrder.png
```

# BDD_create.timeOnCreating vs BDD_createWithBestOrder.timeOnCreating
```shell
graph output.csv -x 3 -y 2 -l "BDD_create" --figsize 1600x1000 --fontsize 14 --chain | \
graph output_best_order.csv -x 3 -y 2 -l "BDD_createWithBestOrder" \
--xlabel "Milliseconds" --ylabel "Number of variable in expression" --title "Time on creating compare" \
-o timeOnCreatingCompare.png
```

# BDD_create.timeOnUsing vs BDD_createWithBestOrder.timeOnUsing vs Evaluating
```shell
graph output.csv -x 4 -y 2 -l "BDD_create" --figsize 1600x1000 --fontsize 14 --chain | \
graph output_best_order.csv -x 4 -y 2 -l "BDD_createWithBestOrder" --chain | \
graph output.csv -x 5 -y 2 -l "Evaluating" \
--xlabel "Milliseconds" --ylabel "Number of variable in expression" --title "Time on using compare" \
-o timeOnUsingCompare.png
```

# Reduction rate
```shell
graph output.csv -x 6 -y 2 -l "Reduction rate" --figsize 1600x1000 --fontsize 14 \
--xlabel "Reduction in percentage" --ylabel "Number of variable in expression" --title "Reduction rate" \
-o reductionRate.png
```