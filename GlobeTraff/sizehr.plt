set terminal postscript eps "Helvetica" 20
set output 'results/sizehr.eps'
set size 1.2, 1.2 
set size 2.0, 2.0 

set title "Hit Ratio Results for First-Level Cache (LFU-Aging)"

set xlabel 'Cache Size C (Bytes)'
set logscale x
set ylabel 'Hit Ratio R (%)'
set mxtics 10
set mytics 5

plot [1:] 'data/size.dhr' title 'Document Hit Ratio' with linespoints, 'data/size.bhr' title 'Byte Hit Ratio' with linespoints
