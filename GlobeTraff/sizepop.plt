reset
set terminal postscript eps "Helvetica" 20
set output 'results/sizepop.eps'
set size 1.2, 1.2
set size 2.0, 2.0

set title "Document Popularity Profile Results for Different Cache Sizes (First-Level Cache: LFU-Aging)"

set xlabel 'Log10(Rank)'
set ylabel 'Log10(Popularity)'
set mxtics 5 
set mytics 2
set xtics nomirror
set ytics nomirror
set xrange [0:6]
set yrange [0:5]

plot [0:7] [0:5] f(x) = a*x + b, a = -0.808, b = 4.838, f(x) title 'f(x)=-0.808x+4.838' with lines, 'data/SZ1.dat' title '1 MB' with points ps 0.9, 'data/SZ2.dat' title '4 MB' with points ps 0.9, 'data/SZ3.dat' title '16 MB' with points ps 0.9, 'data/dataSZ4.dat' title '64 MB' with points ps 0.9, 'data/SZ5.dat' title '256 MB' with points ps 0.9, 'data/SZ6.dat' title '1 GB' with points ps 0.9
