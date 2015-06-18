set terminal postscript eps "Helvetica" 20
set output 'results/pol1.eps'
set size 1.2, 1.2
set size 2.0, 2.0

set title "Hit Ratio Results for First-Level Cache (Byte Hit Ratio)"

set xlabel 'Cache Size C (Bytes)'
set logscale x
set ylabel 'Hit Ratio R (%)'
set mxtics 10
set mytics 5

plot [1:] 'data/pol1.bhr' title 'LRU' with linespoints, 'data/pol2.bhr' title 'LFU' with linespoints, 'data/pol3.bhr' title 'GDS' with linespoints, 'data/pol5.bhr' title 'FIFO' with linespoints, 'data/pol4.bhr' title 'RAND' with linespoints
