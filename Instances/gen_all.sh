set -x
cd /home/siturria/github/cloud-cdn/Akamai

TYPE[0]=low
TYPE[1]=medium
TYPE[2]=high

for (( i=0; i<5; i++ ))
do
    for (( j=0; j<3; j++ ))
    do
        mkdir -p ../Instances/${TYPE[j]}/data.${i}
        #rm ../Instances/${TYPE[j]}/data.${i}/*
        cp ../ProWGen/${TYPE[j]}/data.${i}/* ../Instances/${TYPE[j]}/data.${i}/
    
        cp ../ProWGen/${TYPE[j]}/data.${i}/workload.video .
        ./run.sh
        mv geo_loc.workload.video ../Instances/${TYPE[j]}/data.${i}/
    done
done
