set terminal postscript eps "Helvetica" 20
set output 'htr.eps'
set size 2.2, 2.2

set title "Real Time Hit Ratio Results"

set xlabel 'Elapsed Time (seconds)'
set ylabel 'Hit Ratio R (%)'
set mxtics 10
set mytics 10

plot [1:] 'hrt1' title 'Cache size 1 (bytes)' with linespoints,'hrt4' title 'Cache size 4 (bytes)' with linespoints,'hrt16' title 'Cache size 16 (bytes)' with linespoints,'hrt64' title 'Cache size 64 (bytes)' with linespoints,'hrt256' title 'Cache size 256 (bytes)' with linespoints,'hrt1024' title 'Cache size 1024 (bytes)' with linespoints,'hrt4096' title 'Cache size 4096 (bytes)' with linespoints,'hrt16384' title 'Cache size 16384 (bytes)' with linespoints,'hrt65536' title 'Cache size 65536 (bytes)' with linespoints,'hrt262144' title 'Cache size 262144 (bytes)' with linespoints,'hrt1048576' title 'Cache size 1048576 (bytes)' with linespoints,'hrt4194304' title 'Cache size 4194304 (bytes)' with linespoints,'hrt16777216' title 'Cache size 16777216 (bytes)' with linespoints,'hrt67108864' title 'Cache size 67108864 (bytes)' with linespoints,'hrt268435456' title 'Cache size 268435456 (bytes)' with linespoints