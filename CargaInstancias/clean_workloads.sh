set -x
cd /home/siturria/github/cloud-cdn/Instances

TYPE[0]=low
TYPE[1]=medium
TYPE[2]=high

for (( i=0; i<5; i++ ))
do
    for (( j=0; j<3; j++ ))
    do
        rm ${TYPE[j]}/data.${i}/workload.video.reg
        rm ${TYPE[j]}/data.${i}/workload.video.uniform
        rm ${TYPE[j]}/data.${i}/geo_loc.workload.video
    done
done
