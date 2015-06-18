/* 

Copyright (c) 2000 Mudashiru Busari, Department of Computer Science, 
University of Saskatchewan.

Permission to use, copy, modify, and distribute this software and its 
documentation for any purpose, without fee, and without written agreement 
is hereby granted, provided that the above copyright notice and the 
following paragraph appears in all copies of this software.

IN NO EVENTS SHALL I BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT, 
SPECIAL, INCIDENTAL, OR CONSEQUENCIAL DAMAGES ARISING OUT OF THE USE OF 
THIS SOFTWARE AND ITS DOCUMENTATION.  THE SOFTWARE PROVIDED HEREUNDER IS 
ON AN "AS IS" BASIS, AND I HAVE NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, 
UPDATES, ENHANCEMENTS, OR MODIFICATIONS.

*/



// This file contains the main program with sample configuration of parameters
// needed to synthesize a typical proxy workload. However, you can modify 
// this particular file such that a separate configuration file exists from 
// which values for these parameters can be read. This way, multiple 
// configuration files can be passed in turn to the program.

#include "stream.h"
#include <iostream>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include "distributions.h"

#define ALL 0
#define WEB 1
#define P2P 2
#define VIDEO 3
#define OTHER 4
#define ALL_BUT_WEB 6

using namespace std;

/* This modified version assumes command line parameters like this: */
/*   ./ProWGen reqfile numrefs docs onetimers zipf tail corr        */
int main(int argc, char *argv[])
{

	Distributions* distr = new Distributions();
	
	//==================================================================
	// Initializing basic parameters
	//==================================================================
	int trafficType = atoi(argv[1]);
	

	//The directory where the ouput will be placed
	char *dataDir = (char *) malloc(100 * sizeof(char)); 
	memset(dataDir, 0, 100); 
	strcpy(dataDir, argv[2]);  //1
	strcat(dataDir, "data/"); 

	// This file will contain the requests stream to generate. A three-culumn 
	// file with time stamp followed by file id and file size
	char *requestStreamFile = (char *) malloc(100 * sizeof(char)); 
	memset(requestStreamFile, 0, 100); 
	strcpy(requestStreamFile, dataDir); 
	strcat(requestStreamFile, "workload");

	//Give default name
	//strcat(requestStreamFile, argv[2]);  //1

	// the total number of requests desired in the workload
	//TODO: Remove this when done...
	int totalNoofRequests =  200; //atoi(argv[3]); //2
		
	// percentage of distinct documents of total number of requests. This is 
	// a typical value, but usually it is between 25% to 40%. If you are 
	// synthesizing a workload to mimic an empirical one, you may 
	// need to measure this value out and set this variable appropriately.
	
	//K.Katsaros: we now express this in the GUI as redundancy (r)
	//0% means all distinct documents
	//100% means one document for all requests
	//K.Katsaros (10/1/2012): better express it as "Distinct documents"
	//This has the 1-r value; 
	float web_redundancy = 1-atof(argv[3]); //3
		
	// percentage of 1-timers desired out of the distinct documents. For 
	// proxy workload it is between 50-70%. You are free to set this value 
	// at whatever value you want of course.
	float oneTimerPerc = atoi(argv[4]);  //4
	
	// this is the desired Zipf slope of the workload, usually within 0 and 1.0
	float webZipfSlope = atof(argv[5]);  //5
	
	// the heavy tail index of the workload, usually within 0 and 2.0
	float paretoTailIndex = atof(argv[6]);  //6

	// This allows correlation to be introduced between popularity and file size. 
	// A value of 0 will introduce the normal close-to-zero correlation. Set 
	// this value at -1 if you want negative correllation, or 1 if you want 
	// positive correlation. Note that the correlation introduced will not 
	// be exactly 0, -1, or 1, but will be close in each case.
	float correlation = atof(argv[7]);  //7

	// This is the size of the LRU stack used to introduce temporal locality. 
	// Larger values will be take 
	// more computational time due to movement of elements of the stack.
	unsigned int stacksize = atoi(argv[8]);  //8

	// There are two stack modes used to introduce temporal locality: Static 
	// and Dynamic. If you want static 
	// configure this variable to be 0, else use 1. Workloads with 
	// static seems to introduce more temporal 
	// locality than dynamic. The dynamic is more representative of 
	// empirical ones. So, to mimic an empirical
	// workload, use dynamic.
	unsigned int stackmode = atoi(argv[9]);  //9
	
	// this file wil and 
	// the second column is the file size. The first column of the two 
	// columns could be extracted later for generating and estimating 
	// popularity (Zipf) slope, and the 2nd column for file size distribution 
	// curves or LLCD plot for estimating tail index slope. 
	char *statisticsFile = (char *) malloc(100 * sizeof(char)); 
	memset(statisticsFile, 0, 100); 
	strcpy(statisticsFile, dataDir); 
	strcat(statisticsFile, "docs");

	// this is the percentage of documents at the tail of the file size 
	// distribution, out of the distinct  documents
	float percAtTail = atof(argv[10]); //SIGMETRICS'98 value: 7;	//WebTraff value: 20;

	//the beginning of the tail 
	float K =  atof(argv[11]);   //SIGMETRICS'98 value: 133*1024; OR 9300 (Bestavros)	//WebTraff value: 10000;

	// this is the mean and standard deviation of the lognormal values used to 
	// model the body of the file size distribution. If you are mimicking 
	// an empirical workload and the file size distribution does not match very 
	// well, you might need to adjust these values, otherwise these values seem 
	// to work okay.
	float mean =  atof(argv[12]); //SIGMETRICS'98 value: 9357;		//WebTraff value: 7000;
	float std = atof(argv[13]);  //SIGMETRICS'98 value: 1318;  		//WebTraff value: 11000;

	// these are the parameters for the bittorrent-like P2P workload
	// distributions
	float initMZSlope = atof(argv[14]);			//Default value = 0.60
	int MZplateau = atoi(argv[15]);				//Default value = 20
	float tracesTau = atof(argv[16]);			//Default value = 87.74
	float tracesLamda = atof(argv[17]);  		//Default value = 1.1625 arrivals per hour
	int torrentInterarrival =  atoi(argv[18]); 		//Default value = 8.42

	// these are the parameters for the video workload distributions
	float videoZipfSlope =  atof(argv[19]); 	//Default value = 0.668
	float weibullK = atof(argv[20]); 			//Default value = 0.513
	float weibullL = atof(argv[21]);			//Default value = 6010
	float gammaK   = atof(argv[22]);			//Default value = 0.372
	float gamma8   = atof(argv[23]); 			//Default value = 23910
	float alpha    = atof(argv[24]);			//Default value = 0.703
	float alphaBirth = atof(argv[25]); 			//Default value = 2.0164
	
	//Using user provided size	
	long workloadSize = atoi(argv[26])*pow(1024,3);	//Default value = 5GB

	float web_perc = atof(argv[27]);			//Default value = 0.35
	float p2p_perc = atof(argv[28]);			//Default value = 0.16
	float video_perc = atof(argv[29]);			//Default value = 0.20
	float other_perc = atof(argv[30]);			//Default value = 0.29

	float p2p_redundancy = 1-atof(argv[31]);			//Default value = 0.5
	float video_redundancy = 1-atof(argv[32]);		//Default value = 0.5
	float other_redundancy = 1-atof(argv[33]);		//Default value = 0.5
	float other_size = atof(argv[34])*1024;				//Default value = 5 KB
	
	int video_pop_distr = atoi(argv[35]);
	float otherZipfSlope  = atof(argv[36]);		//Default value: 0.7
	int p2p_fixed_object_size = atoi(argv[37]); //When using samples: -1, else default: 650MB
	bool fixedP2PSize = false;
	//==================================================================


	//==================================================================
	// Calculating the size of each workload...	
	//==================================================================

	// WEB
	float lognormMeanSqr = pow(mean, 2);
    float lognormVariance= pow(std, 2);
    float paramMean = log( lognormMeanSqr / sqrt(lognormVariance + lognormMeanSqr));
    float paramStd  = sqrt(log((lognormVariance + lognormMeanSqr) / lognormMeanSqr));
    float logNormalMedian = exp(paramMean);

	
	float quantile = 0.5+percAtTail/100;
	
	float web_median_object_size = -1;
	
	if (quantile >= 0.5)
		web_median_object_size = distr->non_standard_value((float)distr->standard_normal_quantile(quantile), mean, std);
    else
    {
		printf("Cannot use a Pareto tail for the majority of file size values\n");
		exit(0);
	}

	// VIDEO
	// The video file size follows a multi-modal distribution i.e., the
	// concatenation of four normal distributions. In effect, we cannot
	// calculate the median. Since we know the weight of each  
	// normal distribution in the hybrid distribution we go with the
	// weighted average file size. 
	// TODO: calculate the median from the traces (not given in the papers)
	/*
	 * TODO: Will use this once we get the values from the interface...
	 * 
	int normDist_1_Mean = 16;
	int normDist_2_Mean = 208;
	int normDist_3_Mean = 583;
	int normDist_4_Mean = 295;

	float dist_1_perc = 0.486;
	float dist_2_perc = 0.262;
	float dist_3_perc = 0.027;
	float dist_4_perc = 0.225;
	
	float expected_video_size_mean = (dist_1_perc*normDist_1_Mean + 
									 dist_2_perc*normDist_2_Mean + 
									 dist_3_perc*normDist_3_Mean + 
									 dist_4_perc*normDist_4_Mean) * 330 *1024/8;
	*
	* 
	*/

	float expected_video_size_mean = 6098950; //In Bytes, 5.81 MB

	//P2P
	float p2p_median_object_size = 650.61*1024*1024; //In Bytes, 651.77 MB, our value from the traces: 650.61MB

	if (p2p_fixed_object_size > 0)
	{
		p2p_median_object_size = p2p_fixed_object_size*1024*1024;
		fixedP2PSize = true;
	}

	//Other
	float other_median_object_size = 5*1024; //In Bytes, 5 KB

	// Terra Byte of Traffic per AS, per DAY
	// This is inaccurate. It is derived by dividing the average 2009
	// global traffic reported in http://www.dtc.umn.edu/mints/
	// by 30 and by the total number of ASs reported by CAIDA 
	// (for '10 it was 33559)
	//float TB_AS_DAY = 9.916;
	//float total_traffic_tb = numAS*TB_AS_DAY*pow(1024,2); //pow(1024,4)
	
	
	float web_traffic_size = web_perc*workloadSize;
	float p2p_traffic_size = p2p_perc*workloadSize; printf("p2p_traffic_size = %f MB\n",p2p_traffic_size/(1024*1024));
	float video_traffic_size = video_perc*workloadSize;
	float other_traffic_size = other_perc*workloadSize;
	
	//Using the meadian, as the mean value will produce less objects in 
	//heavy tail object size cases (e.g., web)
	long numWebRequests = web_traffic_size/web_median_object_size;
	long numP2PRequests = p2p_traffic_size/p2p_median_object_size;
	long numVideoRequests = video_traffic_size/expected_video_size_mean;
	long numOtherRequests = other_traffic_size/other_median_object_size;
	long numTotalRequests = numWebRequests+numP2PRequests+numVideoRequests+numOtherRequests;

	printf("===================================================\n");
	printf("Workload size:  %s GB,  Total #requests: %d\n",argv[26],numTotalRequests);
	printf("    Web:	%ld #req, %f  of total, median size = %f KBs\n",numWebRequests,( float)numWebRequests/numTotalRequests*100, web_median_object_size/1024);
	printf("    P2P:	%ld #req, %f  of total, median size = %f MBs\n",numP2PRequests,(float)numP2PRequests/numTotalRequests*100, p2p_median_object_size/(1024*1024));
	printf("    Video:	%ld #req, %f  of total, median size = %f MBs\n",numVideoRequests,(float)numVideoRequests/numTotalRequests*100, expected_video_size_mean/(1024*1024));
	printf("    Other:	%ld #req, %f  of total, median size = %f KBs\n",numOtherRequests,(float)numOtherRequests/numTotalRequests*100, other_median_object_size/1024);
	printf("\nTotal request traffic (1KB/request)= %d  MB\n",numTotalRequests/1024);
	printf("===================================================\n");
		
	float web_other_rel = (float)numWebRequests/numOtherRequests;
	//cerr<<"==================================================="<<endl;
	//cerr<<"Workload size:  "<<argv[26]<<" GB, "<<"Total#requests: "<<<<endl;
	//cerr<<"    Web % = "<<(float)numWebRequests/numTotalRequests*100<<"%, "<<numWebRequests/1024<<" MB"<<endl;
	//cerr<<"    P2P % = "<<(float)numP2PRequests/numTotalRequests*100<<"%, "<<numP2PRequests/1024<<" MB"<<endl;
	//cerr<<"    Video % = "<<(float)numVideoRequests/numTotalRequests*100<<"%, "<<numVideoRequests/1024<<" MB"<<endl;
	//cerr<<"    Other % = "<<(float)numOtherRequests/numTotalRequests*100<<"%, "<<numOtherRequests/1024<<" MB"<<endl;
	//cerr<<"\nTotal request traffic (1KB/request)= "<<numTotalRequests/(1024)<<" MB\n"<<endl;
	//cerr<<"===================================================\n"<<endl;
	//	
	//TODO: test only, remove this...
	/*
	numWebRequests =	totalNoofRequests;
	numP2PRequests =	totalNoofRequests;
	numVideoRequests =	totalNoofRequests;
	numOtherRequests = totalNoofRequests;
	numTotalRequests =	4*totalNoofRequests;
	*/
	if ((!numWebRequests) || (!numP2PRequests) || (!numVideoRequests) || (!numOtherRequests))
	{
		cerr<<"\nOne or more traffic types have zero requests. Please increase the workload size.\n\n"<<endl;
		exit(0);
	}
	
	//==================================================================

	int nextId = 0;
	float lastP2PReqTime = 0;

	RequestWebStream*  webWorkload;
	RequestVideoStream*  videoWorkload;
	RequestP2PStream*  p2pWorkload;
	RequestOtherStream* otherWorkload;
		
	if (( trafficType == WEB) || ( trafficType == ALL))
	{
		webWorkload = new RequestWebStream(requestStreamFile,
										   statisticsFile,
										   webZipfSlope,
										   paretoTailIndex,
										   numWebRequests,
										   oneTimerPerc,
										   web_redundancy, 
										   correlation, 
										   stacksize, 
										   stackmode, 
										   percAtTail,
										   K, 
										   paramMean, 
										   paramStd, 
										   distr);

		nextId = webWorkload->LastObjectId() + 1;

		webWorkload->GenerateRequestStream();

		delete webWorkload;
	}
 
	if (( trafficType == P2P) || ( trafficType == ALL) || ( trafficType == ALL_BUT_WEB))
	{
		p2pWorkload = new RequestP2PStream(requestStreamFile,
										   statisticsFile,
										   initMZSlope, 
										   numP2PRequests, 
										   p2p_redundancy, 
										   MZplateau,
										   tracesTau,
										   tracesLamda,
										   torrentInterarrival,
										   p2p_median_object_size,
										   nextId, 
										   distr,
										   fixedP2PSize);
		
		nextId = nextId+p2pWorkload->LastObjectId() + 1;
		
		p2pWorkload->GenerateRequestStream();

		lastP2PReqTime = p2pWorkload->LastObjectReqTime();
		printf("lastP2PReqTime = %d \n",lastP2PReqTime);

		delete p2pWorkload;
	}
	
	if (( trafficType == VIDEO) || ( trafficType == ALL) || ( trafficType == ALL_BUT_WEB) )
	{
		videoWorkload = new RequestVideoStream(requestStreamFile,
											   statisticsFile,
											   videoZipfSlope, 
											   numVideoRequests, 
											   video_redundancy, 
											   weibullK,
											   weibullL,
											   gammaK,
											   gamma8,
											   alpha,
											   alphaBirth,
											   nextId, 
											   distr, 
											   lastP2PReqTime,
											   video_pop_distr);
											   
		nextId = nextId+videoWorkload->LastObjectId() + 1;

		videoWorkload->GenerateRequestStream();
		
		delete videoWorkload;
	}
		
	if (( trafficType == OTHER) || ( trafficType == ALL) || ( trafficType == ALL_BUT_WEB) )
	{
		otherWorkload = new RequestOtherStream(requestStreamFile,
											   statisticsFile,
											   numOtherRequests, 
											   other_redundancy,
											   otherZipfSlope, 
											   nextId, 
											   distr, 
											   lastP2PReqTime,
											   other_size,
											   web_other_rel);
											   
		nextId = nextId+videoWorkload->LastObjectId() + 1;

		otherWorkload->GenerateRequestStream();
		
		delete otherWorkload;
	}
	
	delete distr;

}

