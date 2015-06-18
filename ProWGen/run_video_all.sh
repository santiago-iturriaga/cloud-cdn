rm data/*

for (( i = 0; i < 5; i++ ))
do
	#Default video only, big
	./Debug/ProWGen 6 ./ 0.3 70 0.75 1.19 0.0 100 4 7 9300 9357 1318 0.6 20 87.74 1.16 3807 0.66 0.51 6010.0 0.37 23910.0 0.703 2.0164 1000 0.35 0.16 0.2 0.29 0.5 0.5 0.5 5.0 0 0.3 -1.0
	
	mv data data.${i}
	mkdir data
done