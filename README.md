# sync-bfs
implement sync bfs algo. 
message complexity : O(|E|)   
time complexity : O(diam)

### input.dat

row 1 -> number of nodes  
row 2 -> id of nodes  
row 3 -> root node  
remaining rows indicate the adjacency matrix

### compile and run

kotlinc *.kt -include-runtime -d sync.jar   
kotlin -cp sync.jar Master input.dat output
