all: autocorr avg buckets bytesperinterval col2 col3 get_time hist histogram \
iat linreg logtrans lrusd number reqsperinterval rnd rs rs_x rs_y strip  \
strip_col12 strip_col2 strip_col3 variance avgpop body lrg iat2
autocorr: autocorr.c
	gcc autocorr.c -o autocorr
avg: avg.c
	gcc avg.c -o avg
avgpop: avgpop.c
	gcc avgpop.c -o avgpop
body: body.c
	gcc body.c -o body
buckets: buckets.c
	gcc buckets.c -o buckets
bytesperinterval: bytesperinterval.c
	gcc bytesperinterval.c -o bytesperinterval
col2: col2.c
	gcc col2.c -o col2
col3: col3.c
	gcc col3.c -o col3
get_time: get_time.c
	gcc get_time.c -o get_time
hist: hist.c
	gcc hist.c -o hist
histogram: histogram.cc
	g++ histogram.cc -o histogram
iat: iat.c
	gcc iat.c -o iat
iat2: iat2.c
	gcc iat2.c -o iat2
linreg: linreg.cc
	g++ linreg.cc -lm -o linreg 
logtrans: logtransform.cc
	g++ logtransform.cc -o logtrans
lrg: lrg.c
	gcc lrg.c -o lrg
lrusd: lrusd.c
	gcc lrusd.c -o lrusd
number: number.c
	gcc number.c -o number
reqsperinterval: reqsperinterval.c
	gcc reqsperinterval.c -o reqsperinterval
rs: rs.c
	gcc rs.c -lm -o rs
rs_x: rs_x.c
	gcc rs_x.c -lm -o rs_x
rs_y: rs_y.c
	gcc rs_y.c -lm -o rs_y 
rnd: rnd.c
	gcc rnd.c -o rnd 
strip: strip.c
	gcc strip.c -o strip
strip_col12: strip_col12.c
	gcc strip_col12.c -o strip_col12
strip_col2: strip_col2.c
	gcc strip_col2.c -o strip_col2
strip_col3: strip_col3.c
	gcc strip_col3.c -o strip_col3
variance: variance.c
	gcc variance.c -o variance

clean: 
	rm -rf autocorr avg buckets bytesperinterval col2 col3 get_time hist \
histogram iat linreg logtrans lrusd number reqsperinterval rnd rs rs_x rs_y \
strip strip_col12  strip_col2 strip_col3 variance avgpop body lrg iat2


