cd /home/siturria/github/cloud-cdn/Akamai

for (( i=0; i<5; i++ ))
do
	mkdir -p ../Instances/low/data.${i}
	rm ../Instances/low/data.${i}/*
        cp ../ProWGen/low/data.${i}/* ../Instances/low/data.${i}/

        cp ../ProWGen/low/data.${i}/workload.video .
	./run.sh
	mv geo_loc.workload.video ../Instances/low/data.${i}/
done
