```shell
graph create.csv -x "Score" -y "Param: size" -l "insert_sorted" `
--xlabel "Milliseconds" --ylabel "Number of operation" `
--title "delete" --fontsize 14 --marker " " `
--figsize 1600x1000 -o create.png
```

```shell
mlr -F "," ' { if toupper($2) == "3") PRINT }' results.csv | `
graph create.csv -x "Score" -y "Param: size" -l "insert_sorted" `
--xlabel "Milliseconds" --ylabel "Number of operation" `
--title "delete" --fontsize 14 --marker " " `
--figsize 1600x1000 -o create.png
```

```shell
Import-Csv -Path "results.csv" | Where-Object { $_.variables -eq "3" } | Export-Csv -Path "output.csv" -NoTypeInformation; `
graph output.csv -x 3 -y 1 -l "insert_sorted" `
--figsize 1600x1000 -o output.png
#--xlabel "Milliseconds" --ylabel "Number of operation" `
#--title "delete" --fontsize 14 --marker " " `
```

Import-Csv -Path "results.csv" | Where-Object { $_.variables -eq "3" } | Export-Csv -Path "output.csv"
-NoTypeInformation
