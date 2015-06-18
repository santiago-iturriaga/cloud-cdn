set reqFl "default"
set NumRefs 1000
set Docs 30
set OneTimers 20
set Zipf 0.75
set Tail 1.20
set Corr 0
set LruSd 100
set PopBias 0.2
set LruFn "stack.dat"
set Model 0

exec ./ProWGen $reqFl $NumRefs $Docs $OneTimers $Zipf $Tail $Corr $LruSd 1 
exec ./freqsize < data/$reqFl > data/docs.dat
set ifv [exec ./lc.pl data/$reqFl]
set dfv [exec ./lc.pl data/docs.dat]
exec ./lrustack $LruFn $PopBias $LruSd $ifv $dfv $Model > data/$reqFl.tmp
exec mv data/$reqFl.tmp data/$reqFl
exit 0 
