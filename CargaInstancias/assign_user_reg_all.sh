set -x
cd /home/siturria/github/cloud-cdn/Instances

TYPE[0]=low
TYPE[1]=medium
TYPE[2]=high

for (( i=0; i<5; i++ ))
do
    for (( j=0; j<3; j++ ))
    do
        mv ${TYPE[j]}/data.${i}/workload.video ${TYPE[j]}/data.${i}/workload.video.reg
        ./assign_user_reg.py ${TYPE[j]}/data.${i}/workload.video.reg reg_users.0 > ${TYPE[j]}/data.${i}/workload.video

        #rm ${TYPE[j]}/data.${i}/workload.video ${TYPE[j]}/data.${i}/workload.video.*
    done
done
