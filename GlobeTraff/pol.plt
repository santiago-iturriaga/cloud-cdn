set terminal postscript eps "Helvetica" 20
set output 'results/pol.eps'
set size 1.2, 1.2
set size 2.0, 2.0

set title "Hit Ratio Results for First-Level Cache (Data Hit Ratio)"

set xlabel 'Cache Size C (Bytes)'
set logscale x
set ylabel 'Hit Ratio R (%)'
set mxtics 10
set mytics 5

plot [1:] 'data/pol1.dhr' title 'LRU' with linespoints, 'data/pol2.dhr' title 'LFU' with linespoints, 'data/pol3.dhr' title 'GDS' with linespoints, 'data/pol5.dhr' title 'FIFO' with linespoints, 'data/pol4.dhr' title 'RAND' with linespoints
