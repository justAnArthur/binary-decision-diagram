# BDD_create
```shell
graph results.csv -x 3 -y 2 -l "Time on creating" --figsize 1600x1000 --fontsize 14 --chain | \
graph results.csv -x 4 -y 2 -l "Time on using" \
--xlabel "Milliseconds" --ylabel "Number of variable in expression" --title "BDD_create" \
-o BDD_create.png
```

# BDD_createWithBestOrder
```shell
graph results_best_order.csv -x 3 -y 2 -l "Time on creating" --figsize 1600x1000 --fontsize 14 --chain | \
graph results_best_order.csv -x 4 -y 2 -l "Time on using" \
--xlabel "Milliseconds" --ylabel "Number of variable in expression" --title "BDD_createWithBestOrder" \
-o BDD_createWithBestOrder.png
```

# BDD_create.timeOnUsing vs BDD_createWithBestOrder.timeOnUsing
```shell
graph results.csv -x 4 -y 2 -l "BDD_create" --figsize 1600x1000 --fontsize 14 --chain | \
graph results_best_order.csv -x 4 -y 2 -l "BDD_createWithBestOrder" \
--xlabel "Milliseconds" --ylabel "Number of variable in expression" --title "Time on using compare" \
-o timeOnUsingCompare.png
```


# BDD_create.timeOnCreating vs BDD_createWithBestOrder.timeOnCreating
```shell
graph results.csv -x 3 -y 2 -l "BDD_create" --figsize 1600x1000 --fontsize 14 --chain | \
graph results_best_order.csv -x 3 -y 2 -l "BDD_createWithBestOrder" \
--xlabel "Milliseconds" --ylabel "Number of variable in expression" --title "Time on creating compare" \
-o timeOnCreatingCompare.png
```
