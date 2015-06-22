echo === LOW TRAFFIC
TRAFFIC=low
for ((i=0; i<5; i++))
do
	echo Scenario $i
	echo num. videos $(wc -l ${TRAFFIC}/data.${i}/docs.video)
	awk '{for(x=1;x<=NF;x++)a[++y]=$1}END{c=asort(a);print "start time:",a[1];print "end time:",a[c]}' ${TRAFFIC}/data.${i}/workload.video
	echo num. requests $(wc -l ${TRAFFIC}/data.${i}/workload.video) 
done

echo === MED TRAFFIC
TRAFFIC=medium
for ((i=0; i<5; i++))
do
	echo Scenario $i
	echo num. videos $(wc -l ${TRAFFIC}/data.${i}/docs.video)
	awk '{for(x=1;x<=NF;x++)a[++y]=$1}END{c=asort(a);print "start time:",a[1];print "end time:",a[c]}' ${TRAFFIC}/data.${i}/workload.video
	echo num. requests $(wc -l ${TRAFFIC}/data.${i}/workload.video) 
done

echo === HI TRAFFIC
TRAFFIC=high
for ((i=0; i<5; i++))
do
	echo Scenario $i
	echo num. videos $(wc -l ${TRAFFIC}/data.${i}/docs.video)
	awk '{for(x=1;x<=NF;x++)a[++y]=$1}END{c=asort(a);print "start time:",a[1];print "end time:",a[c]}' ${TRAFFIC}/data.${i}/workload.video
	echo num. requests $(wc -l ${TRAFFIC}/data.${i}/workload.video) 
done