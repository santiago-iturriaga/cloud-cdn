
/*

Copyright (c) 2000 Mudashiru Busari, Department of Computer Science, University
of Saskatchewan.

Permission to use, copy, modify, and distribute this software and its documentation
for any purpose, without fee, and without written agreement is hereby granted,
provided that the above copyright notice and the following paragraph appears in all
copies of this software.

IN NO EVENTS SHALL I BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL,
OR CONSEQUENCIAL DAMAGES ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION.
THE SOFTWARE PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND I HAVE NO OBLIGATION TO
PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.

*/



// stream.cc
//
//  This file defines the class RequestWebStream representing a stream of
//  Web requests to be generated.

#include <stdlib.h>
#include <stdio.h>
#include <iostream>
#include "stream.h"
#include <fstream>
#include <sstream>
#include <string.h>
#include "stack.h"
#include <time.h>
#include <algorithm>
#include <vector>

using namespace std;

int compare(int *x, int *y);
int compare2(Request **x, Request **y);
int get_correlation(float, int);
double current_time(double);
double  currentT = 0.0;

/*
 * RequestWebStream::RequestWebStream
 *
 * This is the constructor for the class. It initializes all the file
 * names necessary for keeping the results.
 */
RequestWebStream::RequestWebStream(char* initRequestStreamFile,
								   char* initStatisticsFile,
								   float initzipfSlope,
								   float initparetoTailIndex,
								   int initTotalNoofRequests,
								   float onetimerperc,
								   float redundancy,
								   float corr,
								   unsigned int initStacksize,
								   unsigned int initStackmode,
								   float initPercAtTail,
								   float initK,
								   float initLogNormMean,
								   float initLogNormStd,
								   Distributions* distrib)
{
	requestStreamFile = (char *) malloc(100 * sizeof(char));
	memset(requestStreamFile, 0, 100);
	strcpy (requestStreamFile,initRequestStreamFile);
	strcat(requestStreamFile,".web");

	statisticsFile = (char *) malloc(100 * sizeof(char));
	memset(statisticsFile, 0, 100);
	strcpy (statisticsFile,initStatisticsFile);
	strcat(statisticsFile,".web");

	zipfSlope	 		= initzipfSlope;
	paretoTailIndex     = initparetoTailIndex;
	totalNoofRequests 	= initTotalNoofRequests;
	noofDistinctDocs 	= max((int) ((1-redundancy) * totalNoofRequests),1);
	printf("Web #objects = %d, redundancy = %f, totalNoofRequests = %d \n",noofDistinctDocs,redundancy, totalNoofRequests);
	noofonetimers 		= (int)(onetimerperc*noofDistinctDocs/100.0);
	correlation 		= corr;
	stacksize 			= initStacksize;
	stackmode 			= initStackmode;
	percAtTail 			= initPercAtTail;
	K 					= initK;
	paramMean 			=  initLogNormMean;
	paramStd 			= initLogNormStd;
	distributions 		= distrib;
}

/*
 * Returns the object ID of the last object created.
 * Used to allow continueity of ID across traffic types.
 */ 
int RequestWebStream::LastObjectId()
{
	return noofDistinctDocs-1;
}

/*
 * RequestP2PStream::RequestP2PStream
 * 
 * This is the constructor for the class. It initializes all the file
 * names necessary for keeping the results.
 */
RequestP2PStream::RequestP2PStream(char* initRequestStreamFile,
								   char* initStatisticsFile, 
								   float initMZSlope, 
								   int initTotalNoofRequests, 
								   float redundancy,
								   float MZplateau_val,
								   float tracesTau_val,
								   float tracesLamda_val,
								   int torrentInterarrival_val,
								   float p2p_size_median_val,
								   unsigned int first_ID,
								   Distributions* distrib,
								   bool fixedP2PSize_val)
{
	

	requestStreamFile 	= (char *) malloc(100 * sizeof(char));
	memset(requestStreamFile, 0, 100);
	strcpy (requestStreamFile,initRequestStreamFile);
	strcat(requestStreamFile,".p2p");

	statisticsFile 		= (char *) malloc(100 * sizeof(char));
	memset(statisticsFile, 0, 100);
	strcpy (statisticsFile,initStatisticsFile);
	strcat(statisticsFile,".p2p");

	firstId 			= first_ID;
	MZSlope	 			= initMZSlope;
	totalNoofRequests 	= initTotalNoofRequests;
	noofDistinctDocs 	= max((int) ((1-redundancy) * totalNoofRequests),1);
	printf("P2P #objects = %d, redundancy = %f, totalNoofRequests = %d \n",noofDistinctDocs,redundancy, totalNoofRequests);
	maxNoofRequestsPerObject = 0;
	MZplateau 			= MZplateau_val;
	//tracesTau 		= 313314;
	//tracesLamda 		= 0.0003259;
	tracesTau 			= tracesTau_val;
	tracesLamda 		= tracesLamda_val;  //arrivals per hour
	tracesSeeding 		=  8.42; //1/gamma, hours
	p2p_size_median 	= p2p_size_median_val;
	interTorrentInterval= torrentInterarrival_val;//(3600 / 0.9454); //seconds between torrent arrival
	distributions 		= distrib;
	lastObjectReqTime 	= 0;
	fixedP2PSize		= fixedP2PSize_val;

}

/*
 * Returns the object ID of the last object created.
 * Used to allow continueity of ID across traffic types.
 */ 
int RequestP2PStream::LastObjectId()
{
	return noofDistinctDocs-1;
}

/*
 * Returns the time of the last request made in this traffic type.
 * Used to assist timing in the "video" and "other" traffic types where
 * there is no information about the timing of the requests.
 */ 
float RequestP2PStream::LastObjectReqTime()
{
	return lastObjectReqTime;
}

/*
 * RequestVideoStream::RequestVideoStream
 * 
 * This is the constructor for the class. It initializes all the file
 * names necessary for keeping the results.
 */
RequestVideoStream::RequestVideoStream(char* initRequestStreamFile,
									   char* initStatisticsFile,
									   float initzipfSlope,
									   int initTotalNoofRequests,
									   float redundancy, 
									   float weibullK_val,
									   float weibullL_val,
									   float gammaK_val,
									   float gamma8_val,
									   float alpha_val,
									   float alphaBirth_val,
									   unsigned int first_ID, 
									   Distributions* distrib,
									   float lastP2PReq,
									   int video_pop_distr_val,
									   int numRegions_val)
{
	
	requestStreamFile = (char *) malloc(100 * sizeof(char));
	memset(requestStreamFile, 0, 100);
	strcpy (requestStreamFile,initRequestStreamFile);
	strcat(requestStreamFile,".video");

	statisticsFile = (char *) malloc(100 * sizeof(char));
	memset(statisticsFile, 0, 100);
	strcpy (statisticsFile,initStatisticsFile);
	strcat(statisticsFile,".video");
	//statisticsFile    = initStatisticsFile;

	firstId 			= first_ID;
	zipfSlope	 		= initzipfSlope;
	totalNoofRequests 	= initTotalNoofRequests;
	noofDistinctDocs 	= max((int) ((1-redundancy) * totalNoofRequests),1);
	printf("Video #objects = %d, redundancy = %f, totalNoofRequests = %d \n",noofDistinctDocs,redundancy, totalNoofRequests);
	weibullK 			= weibullK_val;
	weibullL 			= weibullL_val;
	gammaK   			= gammaK_val;
	gamma8  			= gamma8_val;
	alpha   			= alpha_val;
	alphaBirth 			= alphaBirth_val;
	distributions		= distrib;
	videoInterArrivalTime 		 = lastP2PReq/noofDistinctDocs;
	videoRequestInterArrivalTime = lastP2PReq/totalNoofRequests;
	printf("videoRequestInterArrivalTime = %f, lastP2PReq = %f, totalNoofRequests = %d \n",videoRequestInterArrivalTime,lastP2PReq, totalNoofRequests);
	video_pop_distr		= video_pop_distr_val;

	numRegions = numRegions_val;
}

/*
 * Returns the object ID of the last object created.
 * Used to allow continueity of ID across traffic types.
 */ 
int RequestVideoStream::LastObjectId()
{
	return noofDistinctDocs-1;
}

/*
 * RequestOtherStream::RequestOtherStream
 * 
 * This is the constructor for the class. It initializes all the file
 * names necessary for keeping the results.
 */
RequestOtherStream::RequestOtherStream(char* initRequestStreamFile,
								   char* initStatisticsFile, 
								   int initTotalNoofRequests, 
								   float redundancy,
								   float initZipfSlope,
								   unsigned int first_ID,
								   Distributions* distrib,
								   float lastP2PReq,
								   float other_size,
								   float web_other_rel_val)
{
	zipfSlope = initZipfSlope;
	requestStreamFile = (char *) malloc(100 * sizeof(char));
	memset(requestStreamFile, 0, 100);
	strcpy (requestStreamFile,initRequestStreamFile);
	strcat(requestStreamFile,".other");

	statisticsFile = (char *) malloc(100 * sizeof(char));
	memset(statisticsFile, 0, 100);
	strcpy (statisticsFile,initStatisticsFile);
	strcat(statisticsFile,".other");

	totalNoofRequests = initTotalNoofRequests;

	firstId = first_ID;

	//TODO: Check this out again when considering the workload size...
	noofDistinctDocs = max((int) ((1-redundancy) * totalNoofRequests),1);
	printf("Other #objects = %d, redundancy = %f, totalNoofRequests = %d \n",noofDistinctDocs,redundancy, totalNoofRequests);

	distributions = distrib;
	otherInterArrivalTime = lastP2PReq/noofDistinctDocs;
	otherRequestInterArrivalTime = lastP2PReq/totalNoofRequests;
	
	//TODO: Take the value from GUI...
	other_size_median = other_size; //1 KB
	
	web_other_rel = web_other_rel_val;
}

/*
 * RequestWebStream::~RequestWebStream
 * 
 * This is the destructor for the class.
 */ 
RequestWebStream::~RequestWebStream()
{
   delete [] uniqueDoc;
}

/*
 * RequestP2PStream::~RequestP2PStream
 *
 * This is the destructor for the class.
 */
RequestP2PStream::~RequestP2PStream()
{
   delete [] uniqueDoc;
}

/*
 * RequestVideoStream::~RequestVideoStream
 *
 * This is the destructor for the class.
 */
RequestVideoStream::~RequestVideoStream()
{
   delete [] uniqueDoc;
}

/*
 * RequestOtherStream::~RequestOtherStream
 *
 * This is the destructor for the class.
 */
RequestOtherStream::~RequestOtherStream()
{
   delete [] uniqueDoc;
}

/*
 * RequestWebStream::GeneratePopularities
 * 
 * Generates a list of file  popularities to follow Zipf distribution.
 * the popularities will be stored in ascending order in a list.
 */
unsigned int* RequestWebStream::GeneratePopularities()
{
	unsigned int *popularity;  //pointer to a list of integers  representing the pops
	srand((unsigned)time(0));
	int rand1 = distributions->UniformInt(1000);
	int rand2 = distributions->UniformInt(1000);
	int rand3 = distributions->UniformInt(1000);
	
	short unsigned int seed[] 	= {rand1, rand2, rand3}; //{5, 7, 9};


	if ((popularity = new unsigned int[noofDistinctDocs]) ==  NULL)
	{
		printf("Error allocating memory in GeneratePopularities\n");
		exit(1);
	}

	// Estimate k in the zipf formula. The middle one timer has a 
	// popularity of 1, hence it can be used to estimate k
	float k = pow(noofDistinctDocs - noofonetimers/2, zipfSlope);

	// First deal with the non 1-timers
	int popularitySum = 0; //sum of the total popularity

	for (int rank =1; rank<=noofDistinctDocs - noofonetimers; rank++)
	{
		int freq = (int)(k / pow(rank, zipfSlope));
		if (freq < 2)
		freq = 2; //do not allow any 1-timer here
		popularity[rank-1] = freq;
		popularitySum += freq;
	}

	// Now assign the popularities of the 1-timers at the end of the array
	for (int i =  noofDistinctDocs - noofonetimers; i < noofDistinctDocs; i++)
	popularity[i] = 1;


	// If there is a difference between sum of popularities and the 
	// desired noof requests then we need to adjust the popularities by
	// scaling either upward or downward
	if ((totalNoofRequests - popularitySum - noofonetimers) != 0)
	{
		float scalingFactor = (float)(totalNoofRequests - noofonetimers)/popularitySum;

		for (int i=0; i < noofDistinctDocs - noofonetimers; i++)
		{
			popularity[i] = (int)(popularity[i] * scalingFactor);
			if (popularity[i] < 2)  popularity[i] = 2; //do not allow 1-timers
		}
	}

	// Sort before returning the array into ascending order
	qsort(popularity, noofDistinctDocs, sizeof(int), (int(*)(const void *, const void *))compare);

	return popularity;
}


/*
 * RequestP2PStream::GeneratePopularities
 * 
 * Generating popularities based on the Mandelbrot-Zipf distribution.
 * proposed by Heffeda et al.
 */
unsigned int* RequestP2PStream::GeneratePopularities(unsigned int q)
{

	float *popularity;  			//pointer to a list of integers  representing the pops
	unsigned int *intPopularity;  	//pointer to a list of integers  representing the pops
	srand((unsigned)time(0));
	int rand1 = distributions->UniformInt(1000);
	int rand2 = distributions->UniformInt(1000);
	int rand3 = distributions->UniformInt(1000);
	
	short unsigned int seed[] 	= {rand1, rand2, rand3}; //{5, 7, 9};


	if ((popularity = new float[noofDistinctDocs]) ==  NULL)
	{
		printf("Error allocating memory in GeneratePopularities\n");
		exit(1);
	}

	if ((intPopularity = new unsigned int[noofDistinctDocs]) ==  NULL)
	{
		printf("Error allocating memory in GeneratePopularities\n");
		exit(1);
	}

	float k = 0;
	float kSum = 0;
	for (int i = 1; i <= noofDistinctDocs; i++)
	{
		kSum += 1 / pow( (i + q), MZSlope);
	}

	k = 1 / kSum;

	// First deal with the non 1-timers
	float popularitySum = 0; //sum of the total popularity
	for (int rank =1; rank<=noofDistinctDocs; rank++)
	{
		float freq = (float)(k / pow(rank + q , MZSlope));

		popularity[rank-1] = freq;
		popularitySum += freq;
	}

	// If there is a difference between sum of popularities and  the desired noof requests
	// then we need to adjust the popularities by scaling either upward or downward
	if ((totalNoofRequests - popularitySum) != 0)
	{

		float scalingFactor = (float)(totalNoofRequests)/popularitySum;

		for (int i=0; i < noofDistinctDocs; i++)
		{
			intPopularity[i] = (int)(popularity[i] * scalingFactor);
		}
	}

	// Sort b4 returning the array into ascending order
	qsort(intPopularity, noofDistinctDocs, sizeof(int), (int(*)(const void *, const void *))compare);

	return intPopularity;
}


/*
 * RequestVideoStream::GeneratePopularities
 * 
 * Generates a list of file  popularities to follow the Weibull
 * (distr = 0),the Gamma distribution (distr = 1) or the Zipf
 * (distr = 2) the popularities will be stored in ascending order
 * in a list.
 */
unsigned int* RequestVideoStream::GeneratePopularities(unsigned int distr)
{
	float *popularity;  //pointer to a list of integers  representing the pops
	unsigned int *popularities;
	srand((unsigned)time(0));
	int rand1 = distributions->UniformInt(1000);
	int rand2 = distributions->UniformInt(1000);
	int rand3 = distributions->UniformInt(1000);
	
	short unsigned int seed[] 	= {rand1, rand2, rand3}; //{5, 7, 9};


	if ((popularity = new float[noofDistinctDocs]) ==  NULL)
	{
		printf("Error allocating memory in GeneratePopularities\n");
		exit(1);
	}

	if ((popularities = new unsigned int[noofDistinctDocs]) ==  NULL)
	{
		printf("Error allocating memory in GeneratePopularities\n");
		exit(1);
	}

	//first deal with the non 1-timers
	float popularitySum = 0; //sum of the total popularity

	switch (distr)
	{
		case 1: //Gamma
				//TODO
				cerr<<"Gamma distribution not supported yet...\n";
				exit(0);
				break;
		case 2: //Zipf
		{
				// Estimate k in the zipf formula. The middle one timer 
				// has a popularity of 1, hence it can be used to 
				// estimate k.
				float k = pow(noofDistinctDocs, zipfSlope);

				for (int rank =1; rank<=noofDistinctDocs; rank++)
				{
					float freq = (float)(k / pow(rank, zipfSlope));
					if (freq < 2)
					freq = 2; //do not allow any 1-timer here
					popularity[rank-1] = freq;
					popularitySum += freq;
				}

				break;
		}
		default:
		{
			//Weibull
			for (int rank =1; rank<=noofDistinctDocs; rank++)
			{
				float freq = distributions->Weibull(rank, weibullK, weibullL);
				popularity[rank-1] = freq;
				popularitySum += freq;
			}

		break;
		}
	}

	// If there is a difference between sum of popularities and the 
	// desired noof requests then we need to adjust the popularities by 
	// scaling either upward or downward
	if ((totalNoofRequests - popularitySum ) != 0)
	{
		float scalingFactor = (float)(totalNoofRequests)/popularitySum;

		for (int i=0; i < noofDistinctDocs; i++)
		{
			popularities[i] = (int)(popularity[i] * scalingFactor);
		}
	}

	// Sort before returning the array into ascending order
	qsort(popularities, noofDistinctDocs, sizeof(int), (int(*)(const void *, const void *))compare);

	delete [] popularity;

	return popularities;
}

/*
 * RequestOtherStream::GeneratePopularities
 * 
 */
unsigned int* RequestOtherStream::GeneratePopularities()
{
	/*
	unsigned int *popularities;

	if ((popularities = new unsigned int[noofDistinctDocs]) ==  NULL)
	{
	  printf("Error allocating memory in GeneratePopularities\n");
	  exit(1);
	}

	for (int rank =1; rank<=noofDistinctDocs; rank++)
	{
		popularities[rank-1] = 1;
	}

	// Sort before returning the array into ascending order
	qsort(popularities, noofDistinctDocs, sizeof(int), (int(*)(const void *, const void *))compare);

	return popularities;
	
	*/
	
	//----
	unsigned int *popularity;  //pointer to a list of integers  representing the pops
	srand((unsigned)time(0));
	int rand1 = distributions->UniformInt(1000);
	int rand2 = distributions->UniformInt(1000);
	int rand3 = distributions->UniformInt(1000);
	
	short unsigned int seed[] 	= {rand1, rand2, rand3}; //{5, 7, 9};


	if ((popularity = new unsigned int[noofDistinctDocs]) ==  NULL)
	{
		printf("Error allocating memory in GeneratePopularities\n");
		exit(1);
	}

	// Estimate k in the zipf formula. The middle one timer has a 
	// popularity of 1, hence it can be used to estimate k
	//float k = pow(noofDistinctDocs, zipfSlope);

	float floatSum=0;
	for (int rank =1; rank<=noofDistinctDocs; rank++)
	{
		floatSum = 1 / pow(rank, zipfSlope);
	}
	
	float k = 1/floatSum;
	
	// First deal with the non 1-timers
	int popularitySum = 0; //sum of the total popularity

	for (int rank =1; rank<=noofDistinctDocs; rank++)
	{
		int freq = (int)(k / pow(rank, zipfSlope));
		if (freq < 2)
			freq = 1; 
		popularity[rank-1] = freq;
		popularitySum += freq;
		
	}

	// If there is a difference between sum of popularities and the 
	// desired noof requests then we need to adjust the popularities by
	// scaling either upward or downward
	if ((totalNoofRequests - popularitySum ) != 0)
	{
		float scalingFactor = (float)(totalNoofRequests)/popularitySum;

		for (int i=0; i < noofDistinctDocs; i++)
		{
			popularity[i] = (int)(popularity[i] * scalingFactor);
			if (popularity[i] < 2)  popularity[i] = 1; 
		}
	}
/*
cerr<<"popularity["<<1<<"] = "<<popularity[1]<<endl;
cerr<<"popularity["<<10<<"] = "<<popularity[10]<<endl;
cerr<<"popularity["<<100<<"] = "<<popularity[100]<<endl;
cerr<<"popularity["<<1000<<"] = "<<popularity[1000]<<endl;

cerr<<"totalNoofRequests = "<<totalNoofRequests<<endl;
cerr<<"popularitySum = "<<popularitySum<<endl;
cerr<<"noofDistinctDocs = "<<noofDistinctDocs<<endl;
cerr<<"zipfSlope = "<<zipfSlope<<endl;
*/
	// Sort before returning the array into ascending order
	qsort(popularity, noofDistinctDocs, sizeof(int), (int(*)(const void *, const void *))compare);

	return popularity;
}

/*
 * RequestWebStream::GenerateFileSizes
 * 
 * Generates file sizes for the tail of the distribution using
 * Pareto distribution while lognormal distribution is used for the
 * body of the distribution.
 */ 
unsigned int* RequestWebStream::GenerateFileSizes()
{
	long all 			= 0;
	long lognormal_only	=0;
	int lll				=0;

	unsigned int *filesizes = new unsigned int[noofDistinctDocs];
	unsigned int noAtTail 	= (unsigned int)(percAtTail*noofDistinctDocs/100); //to be calculated

	bool usedFlag = true;

	double u1, u2, v1, v2, w, y, x1, x2; //params of the normal variate

	double normalVariate; //the lognormal variate
	srand((unsigned)time(0));
	int rand1 = distributions->UniformInt(1000);
	int rand2 = distributions->UniformInt(1000);
	int rand3 = distributions->UniformInt(1000);
	
	unsigned short seed[3] 	= {rand1, rand2, rand3}; //{5, 7, 9}; //passed to the random number function

	unsigned int size; //filesize for a request

	// First generate the body using lognormal distribution
	unsigned int counter=0;
	while (counter < (noofDistinctDocs - noAtTail))
	{
		if (usedFlag) //are the 2 previous normal variates used? must enter this loop first time!
		{
			// Then generate 2 new normal variates
			usedFlag = false;
			w = 2.0;
			while (w > 1)
			{
				u1 = erand48(seed); //u1 & u2 are IID U(0,1)
				u2 = erand48(seed);
				v1 = 2*u1 - 1;
				v2 = 2*u2 - 1;
				w = v1*v1 + v2*v2;
			}
			y  = pow((-2*log(w)/w), 0.5);
			x1 = v1*y;  //x1 & x2 are IID N(0,1) random variables
			x2 = v2*y;

			normalVariate = x1;
		}
		else //second normal variate of the previous loop has not been used
		{
			normalVariate = x2;
			usedFlag = true;
		}

		// Now generate a file size from the log-normal distribution and
		// do not allow sizes that is as big as K, where the tail should
		// start from.
		size = (int)(exp(paramMean + paramStd * normalVariate));
		if (size < K)
		{
			filesizes[counter] = size;
			counter++;
			all +=size;
			lognormal_only += size;
			lll++;
		}
	}

	// Now generate the file sizes at the tail using Pareto disallowing files
	// larger than 50K.
	counter = noofDistinctDocs-noAtTail;
	double ParetoMean=0;
	int kkk=0;

	while  (counter< noofDistinctDocs)
	{
		size = (int)(K / pow(erand48(seed), (1/paretoTailIndex)));
		if (size < 50*1024*1024)  //disallow files more than 50K
		{
			filesizes[counter] = size;
			counter++;
			all +=size;
			kkk++;
			ParetoMean += size;
		}
	}

	//sort in ascending order before returning
	qsort(filesizes, noofDistinctDocs, sizeof(int), (int(*)(const void *, const void *))compare);

	/*
	* 	double median = 0;
	* 
	* 	if (noofDistinctDocs%2 == 0)
	* 	median = (filesizes[noofDistinctDocs/2]+filesizes[(noofDistinctDocs/2) + 1])/2;
	* 	else
	* 	median = filesizes[(noofDistinctDocs/2) + 1];
	* 
	* 	cerr<<"Median = "<<median<<endl;
	* 	cerr<<"Pareto Mean = "<<ParetoMean/kkk<<endl;
	* 	cerr<<"Lognormal Mean = "<<lognormal_only/lll<<endl;
	* 	cerr<<"All Mean = "<<all/noofDistinctDocs<<"\nExpected mean = "<<efzm<<endl;
	*/

	return filesizes;
}

/*
 * RequestP2PStream::GenerateFileSizes
 */ 
unsigned int* RequestP2PStream::GenerateFileSizes()
{
    unsigned int *filesizes = new unsigned int[noofDistinctDocs];

	for (unsigned int i =0; i<noofDistinctDocs; i++)
	{
		if (!fixedP2PSize) {
			filesizes[i] = SampleFileSize()*1024*1024;//p2p_size_median; //In Bytes
		}
		else {
			filesizes[i] = p2p_size_median;
		}
	}

  return filesizes;
}

/*
 * RequestVideoStream::GenerateFileSizes
 */ 
unsigned int* RequestVideoStream::GenerateFileSizes()
{
    unsigned int *filesizes = new unsigned int[noofDistinctDocs];

	int normDist_1_Mean = 16;
	int normDist_2_Mean = 208;
	int normDist_3_Mean = 583;
	int normDist_4_Mean = 295;

	int normDist_1_Std = 62;
	int normDist_2_Std = 58;
	int normDist_3_Std = 16;
	int normDist_4_Std = 172;

	float dist_1_perc = 0.486;
	float dist_2_perc = 0.262;
	float dist_3_perc = 0.027;
	float dist_4_perc = 0.225;

	float point_1 = dist_1_perc;
	float point_2 = point_1 + dist_2_perc;
	float point_3 = point_2 + dist_3_perc;

	int mean, std;

    //TBD...
    //Yield a single value for the moment
    //i.e. the median video file size 8.215MB
  	for (int i=0; i<noofDistinctDocs; i++)
	{
		double length = -1;
		
		float randNum = distributions->Uniform01();

		if (randNum <= point_1)
		{
			mean = normDist_1_Mean;
			std  = normDist_1_Std;
		}
		else if (point_1 < randNum <= point_2)
		{
			mean = normDist_2_Mean;
			std  = normDist_2_Std;
		}	
		else if (point_2 < randNum <= point_3)
		{
			mean = normDist_3_Mean;
			std  = normDist_3_Std;
		}	
		else
		{
			mean = normDist_4_Mean;
			std  = normDist_4_Std;
		}	

		while (length <= 0)
			length = distributions->Normal(mean, std);


		filesizes[i] = (int)(length*330*1024)/8; // length*Kbps -> bytes; //In Bytes
	}

  return filesizes;
}

/*
 * RequestOtherStream::GenerateFileSizes
 */ 
unsigned int* RequestOtherStream::GenerateFileSizes()
{
    unsigned int *filesizes = new unsigned int[noofDistinctDocs];

	//TBD..Fixed file size for the time being
	for (unsigned int i =0; i<noofDistinctDocs; i++)
	{
		filesizes[i] = other_size_median; //In Bytes
	}

  return filesizes;
}

/*
 * RequestWebStream::GenerateAllRequests
 * 
 * This generates the requests stream using either static/dynamic finite
 * LRU stack models. The request is stored in the file specified as
 * input to the workload generator. The file has 2 columns: the first
 * column is the fileId and the second column is the file size.
 */ 
void RequestWebStream::GenerateAllRequests()
{
	//This part generate references based on static or dynamic models
	Stack *stack;

	if (stackmode == 0)  //static model desired
	{
		double *cummProb = new double[stacksize];  //do not delete this array, it is passed to the stack class
		double sum = 0;

		// The stack size cannot be more than the unique docs we have
		if (stacksize > noofDistinctDocs)
			stacksize = noofDistinctDocs;

		for (int i=0; i<stacksize; i++)
		{
			sum += uniqueDoc[i]->GetProb();
			cummProb[i] = sum;
		}

		stack = new Stack(stacksize, cummProb);
	}
	else   //dynamic model desired
		stack = new Stack(stacksize);

	unsigned int totalReqGenerated = 0;
	srand((unsigned)time(0));
	int rand1 = distributions->UniformInt(1000);
	int rand2 = distributions->UniformInt(1000);
	int rand3 = distributions->UniformInt(1000);
	
	unsigned short seed[3] 	= {rand1, rand2, rand3}; //{5, 7, 9}; //passed to the random number function

	double randNum;
	unsigned int index;

	//open the request stream file for writing
	FILE *fp = fopen(requestStreamFile, "w");

	while (totalReqGenerated < totalNoofRequests)
	{
		randNum = erand48(seed);

		if (!stack->LocateDoc(currentT, randNum, noofDistinctDocs, fp))
		{
			if (noofDistinctDocs > 0)
			{
				index = (unsigned int)(noofDistinctDocs*erand48(seed));

				Request *req = uniqueDoc[index];
				fprintf(fp,"%u %u\n", req->GetFileId(), req->GetFileSize());

				// This increment should be substituted with a procedure. See below.
				totalReqGenerated++;
				req->DecFreq();
				uniqueDoc[index] = uniqueDoc[noofDistinctDocs-1];
				noofDistinctDocs--;

				if (req->GetFreq() == 0)
					delete req;
				else
				{
					Request *temp = stack->Put(req);
					if (temp != NULL)
					{
						uniqueDoc[noofDistinctDocs] = temp;
						noofDistinctDocs++;
					}
				}
			}
		}
		else
		{
			totalReqGenerated++;
		}

		//ShowProgress(totalReqGenerated, totalNoofRequests);
	}

	fclose(fp);
	delete stack;

	//all requests have been generated into the request stream file. Note the total request
	//generated may not be the same as desired but should be close enough to within 10% difference
}


/*
 * RequestP2PStream::GenerateAllRequests
 */ 
void RequestP2PStream::GenerateAllRequests()
{
	srand((unsigned)time(0));
	unsigned int sumFreq = 0;
	float medianFreq = (uniqueDoc[0]->GetFreq() + uniqueDoc[noofDistinctDocs-1]->GetFreq()) / 2;

	for (int i = 0 ; i < noofDistinctDocs ; i++)
	{
	     Request *req = uniqueDoc[i];
		 sumFreq += req->GetFreq();
	}

	float avgFreq = sumFreq / noofDistinctDocs ;

	FILE *fp = fopen(requestStreamFile, "w");

	// This vector will be used to draw order indexes for distinct torrents.
	// The target is to avoid creating torrents in the order of popularity
	vector<int> torrentArrivalOrder;

	for (int i = 0; i <	noofDistinctDocs; i++)
	{
		torrentArrivalOrder.push_back(i);
	}

	for (int i = 0 ; i < noofDistinctDocs ; i++)
	{
	     Request *req = uniqueDoc[i];

		// tracesTau describes the average torrent.
		// Unfortunately we do not know the median value ...
		// ... so we go with the average value, but normalize the value
		// between all torrents in the workload.
		float tau 		=   ((float)( (req->GetFreq())/ avgFreq )) * tracesTau;

		int indexIndex 	= distributions->UniformInt(torrentArrivalOrder.size());
		int itemIndex 	= torrentArrivalOrder.at(indexIndex);
		float time 		= itemIndex*interTorrentInterval;
		torrentArrivalOrder.erase(torrentArrivalOrder.begin() + indexIndex);

		double *times = bitTorrentInterarrivalTimes(tracesLamda,tau,tracesSeeding,req->GetFreq());

		for (int k = 0; k < req->GetFreq() ; k++)
		{
			time = time + times[k];

			if (time > lastObjectReqTime)
				lastObjectReqTime = time;



			fprintf(fp,"%f\t%d\t%d\n", time, req->GetFileId(),req->GetFileSize());
		}
	}

	fclose(fp);
}

/*
 * RequestVideoStream::GenerateAllRequests
 */ 
void RequestVideoStream::GenerateAllRequests()
{
	srand((unsigned)time(0));
	FILE *fp = fopen(requestStreamFile, "w");

	// This vector will be used to draw order indexes for distinct videos.
	// The target is to avoid creating videos in the order of popularity
	// This is usefull in case we choose to use a fixed session inter-arrival
	// time value.
	
	vector<int> videoArrivalOrder;
	for (int i = 0; i <	noofDistinctDocs; i++)
	{
		videoArrivalOrder.push_back(i);
	}
	
	for (int i = 0 ; i < noofDistinctDocs ; i++)
	{
		Request *req = uniqueDoc[i];

		int indexIndex = distributions->UniformInt(videoArrivalOrder.size());
		int itemIndex = videoArrivalOrder.at(indexIndex);
		float time = itemIndex*videoInterArrivalTime;
		videoArrivalOrder.erase(videoArrivalOrder.begin() + indexIndex);
		
		/*	ALternatively...
		 * 
		 * float time = distributions->ParetoCDF(alphaBirth)*24*3600;
		 *
		 *  */

		for (int k = 0; k < req->GetFreq() ; k++)
		{
			time = time + videoRequestInterArrivalTime;//distributions->ParetoCDF(alpha)*24*3600;

			int geoloc;
			geoloc = distributions->UniformInt(numRegions);

			fprintf(fp,"%f\t%d\t%d\t%d\n", time, req->GetFileId(),req->GetFileSize(),geoloc);
		}
	}

	fclose(fp);
}

/*
 * RequestOtherStream::GenerateAllRequests
 */ 
void RequestOtherStream::GenerateAllRequests()
{
	FILE *fp = fopen(requestStreamFile, "w");

	vector<int> allOtherObjectRequests;
	int	totalNumRequestsCounter = 0;
	
	for (int i = 0; i <	noofDistinctDocs; i++)
	{
		Request *req = uniqueDoc[i];
		int freq = req->GetFreq();
		totalNumRequestsCounter = totalNumRequestsCounter+freq;
		
		if (freq == 0) { cerr<<"ZERO!"<<endl; printf("ZERO"); exit(1);}
		
		for (int l=0; l < freq; l++)
		{
			allOtherObjectRequests.push_back(i);
		}
	}
	
	cerr<<"totalNumRequestsCounter = "<<totalNumRequestsCounter<< " allOtherObjectRequests size = "<<allOtherObjectRequests.size()<<" arrival rate = "<<ARRIVAL_RATE*web_other_rel<<endl;
	
	float time = 0;
	while (totalNumRequestsCounter > 0)
	{
		int reqIndex = distributions->UniformInt(totalNumRequestsCounter);
		int itemIndex = allOtherObjectRequests.at(reqIndex);
		allOtherObjectRequests.erase(allOtherObjectRequests.begin()+reqIndex);
		Request *req = uniqueDoc[itemIndex];
		
		time = time + distributions->Exponential(1.0/(ARRIVAL_RATE/web_other_rel));
		//time += otherRequestInterArrivalTime;
		fprintf(fp,"%f\t%d\t%d\n", time, req->GetFileId(),req->GetFileSize());
		//printf("generating request for item %d, totalNumRequestsCounter %d",itemIndex,totalNumRequestsCounter);
		totalNumRequestsCounter--;
	}
	
	//------------------------------------------------------------------
	/*
	// This vector will be used to draw order indexes for distinct videos.
	// The target is to avoid creating videos in the order of popularity
	// This is usefull in case we choose to use a fixed session inter-arrival
	// time value.
	
	vector<int> otherArrivalOrder;
	for (int i = 0; i <	noofDistinctDocs; i++)
	{
		otherArrivalOrder.push_back(i);
	}
	
	for (int i = 0 ; i < noofDistinctDocs ; i++)
	{
		Request *req = uniqueDoc[i];

		int indexIndex = distributions->UniformInt(otherArrivalOrder.size());
		int itemIndex = otherArrivalOrder.at(indexIndex);
		float time = itemIndex*otherInterArrivalTime;
		otherArrivalOrder.erase(otherArrivalOrder.begin() + indexIndex);
		

		//Considering only one timers...
		
		for (int k = 0; k < req->GetFreq() ; k++)
		{
			time = time + 0; // Considering single reference documents...
			fprintf(fp,"%f\t%d\t%d\n", time, req->GetFileId(),req->GetFileSize());
		}
	}
	//------------------------------------------------------------------
	*/
	fclose(fp);
}

/*
 * RequestWebStream::GenerateUniqueDocs
 * 
 * this function will introduce either +ve, -ve, or zero correlation
 * between popularity and file size. It takes 2 lists--a list storing
 * a set of popularities and and their cummulative probabilities and
 * the second one stores the filesizes and their cummulative probs. For
 * each unique request, a popularity is generated as well as the corres-
 * ponding filesize. A uniform random number is generated which is used
 * to search the popularity list for the corresponding popularity(applying
 * interpolation if necessary). For the file size, the same random no
 * is used to search the filesize list if +ve correlation is desired, or
 * for negative correlation the random no is subtracted from 1 before
 * being used to search the filesize list. For zero correlation, another
 * random no is generated for the filesize search. Note that the new
 * list being created must be in descending order of popularity for our
 * temporal locality proc to work correctly
 */

void RequestWebStream::GenerateUniqueDocs(Node* popCDF, int noofItems1,
 Node* sizeCDF, int noofItems2)
{

	uniqueDoc = new Request*[noofDistinctDocs];
	if (uniqueDoc == NULL)
	{
		printf("Error allocating memory in function GenerateUniqueDocs \n");
		exit(1);
	}
	
	srand((unsigned)time(0));
	int rand1 = distributions->UniformInt(1000);
	int rand2 = distributions->UniformInt(1000);
	int rand3 = distributions->UniformInt(1000);
	
	unsigned int filesize, filepop;
	short unsigned int seed[] 	= {rand1, rand2, rand3}; //{5, 7, 9};
	unsigned int total 			= 0;
	int corr 					= get_correlation(correlation, noofDistinctDocs);

	if (corr < 0) 
	{
		// First generate the popularities
		for (unsigned int count=0; count < noofDistinctDocs; count++)
		{
			filepop = max((unsigned int)1,FindValue(popCDF, noofItems1, erand48(seed)));
			total += filepop;
			Request *request = new Request(filepop, 0, WEB); //sets the file size to 0 here
			uniqueDoc[count] = request;
		}

		// Then generate the file sizes without correlation but 
		// disallowing files bigger than 10K to have popularity >=100
		unsigned int count =0;
		while (count < noofDistinctDocs)
		{
			filesize = FindValue(sizeCDF, noofItems2, erand48(seed));
			if ((filesize < 10*1024) || (uniqueDoc[count]->GetFreq() < 100))
			{
				uniqueDoc[count]->SetFileSize(filesize);
				count++;
			}
		}
	}
	else
	{
		for (unsigned int count=0; count < corr; count++)
		{
			double randnum 	= erand48(seed);
			filepop 		= FindValue(popCDF, noofItems1, randnum);
			total 			+= filepop;
			if (correlation > 0)     //+ve correlation, so use the same random no
				filesize = FindValue(sizeCDF, noofItems2, randnum);
			else if(correlation < 0) //-ve correlation, so use 1-random no
				filesize = FindValue(sizeCDF, noofItems2, 1-randnum);

			// Create a unique request object and then insert it into the unique doc list
			Request *request = new Request(filepop, filesize, WEB);
			uniqueDoc[count] = request;
		}

		// First generate the popularities
		for (unsigned int count=corr; count < noofDistinctDocs; count++)
		{
			filepop = FindValue(popCDF, noofItems1, erand48(seed));
			total += filepop;
			Request *request = new Request(filepop, 0, WEB); //sets the file size to 0 here
			uniqueDoc[count] = request;
		}

		// Then generate the file sizes without correlation but disallowing files
		// bigger than 10K to have popularity >=100
		unsigned int count =corr;
		while (count < noofDistinctDocs)
		{
			filesize = FindValue(sizeCDF, noofItems2, erand48(seed));
			if ((filesize < 10*1024) || (uniqueDoc[count]->GetFreq() < 100))
			{
				uniqueDoc[count]->SetFileSize(filesize);
				count++;
			}
		}
	}

	// We have to sort them first in descending order of popularity
	qsort(uniqueDoc, noofDistinctDocs, sizeof(Request*), (int(*)(const void *, const void *))compare2);

	if ((totalNoofRequests - total) != 0)
	{
		float scalingFactor = (float)(totalNoofRequests - noofonetimers)/(total - noofonetimers);
		total 				= noofonetimers; //recalculate total noof requests

		for (int i=0; i < noofDistinctDocs - noofonetimers; i++)
		{
			//int freq  = (int)(uniqueDoc[i]->GetFreq() * scalingFactor);
			int freq = floorf(uniqueDoc[i]->GetFreq() * scalingFactor + 0.5);

			if (freq < 2)
				uniqueDoc[i]->SetFreq(2); //do not allow more 1-timers
			else
				uniqueDoc[i]->SetFreq(freq);

			total += freq;
		}
	}

	// Assign the file ids now starting from 0
	for (int i=0; i< noofDistinctDocs; i++)
		uniqueDoc[i]->SetFileId(i);


	// Now set the cummulative probability of each request based on its popularity
	for (unsigned int j= 0; j < noofDistinctDocs; j++)
	{
		uniqueDoc[j]->SetProb((double)uniqueDoc[j]->GetFreq()/ total);
	}

	totalNoofRequests = total; //this is the new total noof requests that will be generated
}


/*
 * RequestP2PStream::GenerateUniqueDocs
 */ 
void RequestP2PStream::GenerateUniqueDocs(Node* popCDF, int noofItems1, Node* sizeCDF, int noofItems2)
{
	uniqueDoc = new Request*[noofDistinctDocs];
	if (uniqueDoc == NULL)
	{
		printf("Error allocating memory in function GenerateUniqueDocs_P2P \n");
		exit(1);
	}
	srand((unsigned)time(0));
	int rand1 = distributions->UniformInt(1000);
	int rand2 = distributions->UniformInt(1000);
	int rand3 = distributions->UniformInt(1000);
	
	short unsigned int seed[] 	= {rand1, rand2, rand3}; //{5, 7, 9};

	unsigned int total = 0;
	unsigned int filesize, filepop;
	unsigned int fileSizeRounded;

	// First generate the popularities
	for (unsigned int count=0; count < noofDistinctDocs; count++)
	{
		filepop = FindValue(popCDF, noofItems1, erand48(seed));
		total += filepop;
		Request *request = new Request(filepop, 0, P2P); //sets the file size to 0 here
		uniqueDoc[count] = request;
	}

	// Then generate the file sizes without correlation but disallowing files
	// bigger than 10K to have popularity >=100
	unsigned int count = 0;
	int sum = 0;

	while (count < noofDistinctDocs)
	{
		filesize = FindValue(sizeCDF, noofItems2, erand48(seed));
		uniqueDoc[count]->SetFileSize(filesize);
		count++;
		sum += filesize;
	}

	// We have to sort them first in descending order of popularity
	qsort(uniqueDoc, noofDistinctDocs, sizeof(Request*), (int(*)(const void *, const void *))compare2);

	if ((totalNoofRequests - total) != 0)
	{
		float scalingFactor = (float)(totalNoofRequests)/(total);
		total = 0;

		for (int i=0; i < noofDistinctDocs ; i++)
		{
			//int freq  = (int)(uniqueDoc[i]->GetFreq() * scalingFactor);
			int freq = floorf(uniqueDoc[i]->GetFreq() * scalingFactor + 0.5);
			uniqueDoc[i]->SetFreq(freq);
			total += freq;
		}
	}

	// Assign the file ids now starting from 0
	for (int i=0; i< noofDistinctDocs; i++)
	uniqueDoc[i]->SetFileId(firstId+i);

	// Now set the cummulative probability of each request based on its popularity
	for (unsigned int j= 0; j < noofDistinctDocs; j++)
	{
		uniqueDoc[j]->SetProb((double)uniqueDoc[j]->GetFreq()/ total);
	}

	totalNoofRequests = total; //this is the new total noof requests that will be generated
}

double* RequestP2PStream::bitTorrentInterarrivalTimes(float lamda, float tau, float tracesSeeding, unsigned int noofRequests)
{
	double *val = new double[noofRequests];

	for (int i=0; i < noofRequests; i++)
	{
		val[i] =  3600/distributions->exponentialDecayArrivalRate(lamda,tau, tracesSeeding);
	}

    sort(val, val + noofRequests);

	return val;
}

/*
 * RequestVideoStream::GenerateUniqueDocs
 */ 
void RequestVideoStream::GenerateUniqueDocs(Node* popCDF, int noofItems1, Node* sizeCDF, int noofItems2)
{
	uniqueDoc = new Request*[noofDistinctDocs];

	if (uniqueDoc == NULL)
	{
		printf("Error allocating memory in function GenerateUniqueDocs_P2P \n");
		exit(1);
	}
	
	srand((unsigned)time(0));
	int rand1 = distributions->UniformInt(1000);
	int rand2 = distributions->UniformInt(1000);
	int rand3 = distributions->UniformInt(1000);
	
	short unsigned int seed[] 	= {rand1, rand2, rand3}; //{5, 7, 9};

	unsigned int total = 0;
	unsigned int filesize, filepop;
	unsigned int fileSizeRounded;

	// First generate the popularities
	for (unsigned int count=0; count < noofDistinctDocs; count++)
	{
		filepop = max((unsigned int)1,FindValue(popCDF, noofItems1, erand48(seed)));
		total += filepop;
		Request *request = new Request(filepop, 0, VIDEO); //sets the file size to 0 here
		uniqueDoc[count] = request;
	}

	// Then generate the file sizes without correlation but disallowing files
	// bigger than 10K to have popularity >=100
	unsigned int count = 0;
	int sum = 0;

	while (count < noofDistinctDocs)
	{
		filesize = FindValue(sizeCDF, noofItems2, erand48(seed));

		uniqueDoc[count]->SetFileSize(filesize);
		count++;
		sum += filesize;
	}

	// We have to sort them first in descending order of popularity
	qsort(uniqueDoc, noofDistinctDocs, sizeof(Request*), (int(*)(const void *, const void *))compare2);

	if ((totalNoofRequests - total) != 0)
	{
		float scalingFactor = (float)(totalNoofRequests)/(total);
		total = 0;

		for (int i=0; i < noofDistinctDocs ; i++)
		{
			//int freq  = (int)(uniqueDoc[i]->GetFreq() * scalingFactor);		
			int freq = floorf(uniqueDoc[i]->GetFreq() * scalingFactor + 0.5);	
			uniqueDoc[i]->SetFreq(freq);
			total += freq;
		}
	}

	// Assign the file ids now starting from 0
	for (int i=0; i< noofDistinctDocs; i++)
		uniqueDoc[i]->SetFileId(firstId+i);

	// Now set the cummulative probability of each request based on its popularity
	for (unsigned int j= 0; j < noofDistinctDocs; j++)
	{
		uniqueDoc[j]->SetProb((double)uniqueDoc[j]->GetFreq()/ total);
	}

	totalNoofRequests = total; //this is the new total noof requests that will be generated
}


/*
 * RequestOtherStream::GenerateUniqueDocs
 */ 
void
RequestOtherStream::GenerateUniqueDocs(Node* popCDF, int noofItems1, Node* sizeCDF, int noofItems2)
{
	uniqueDoc = new Request*[noofDistinctDocs];

	if (uniqueDoc == NULL)
	{
		printf("Error allocating memory in function RequestOtherStream::GenerateUniqueDocs \n");
		exit(1);
	}

	srand((unsigned)time(0));
	int rand1 = distributions->UniformInt(1000);
	int rand2 = distributions->UniformInt(1000);
	int rand3 = distributions->UniformInt(1000);
	
	short unsigned int seed[] 	= {rand1, rand2, rand3}; //{5, 7, 9};

	unsigned int total = 0;
	unsigned int filesize, filepop;
	unsigned int fileSizeRounded;

	// First generate the popularities
	for (unsigned int count=0; count < noofDistinctDocs; count++)
	{
		filepop = max((unsigned int)1,FindValue(popCDF, noofItems1, erand48(seed)));
		total += filepop;
		Request *request = new Request(filepop, 0, OTHER); //sets the file size to 0 here
		uniqueDoc[count] = request;
	}

	// Then generate the file sizes without correlation
	unsigned int count = 0;
	int sum = 0;

	while (count < noofDistinctDocs)
	{
		filesize = FindValue(sizeCDF, noofItems2, erand48(seed));
		uniqueDoc[count]->SetFileSize(filesize);
		count++;
		sum += filesize;
	}

	// We have to sort them first in descending order of popularity
	qsort(uniqueDoc, noofDistinctDocs, sizeof(Request*), (int(*)(const void *, const void *))compare2);


	if ((totalNoofRequests - total) != 0)
	{
		
		float scalingFactor = (float)(totalNoofRequests)/(total);
		total = 0;

		for (int i=0; i < noofDistinctDocs ; i++)
		{
			//int freq  = (int)(uniqueDoc[i]->GetFreq() * scalingFactor);
			int freq = floorf(uniqueDoc[i]->GetFreq() * scalingFactor + 0.5);
			uniqueDoc[i]->SetFreq(freq);
			total += freq;
		}
		//cerr<<"new total = "<<total<<endl;
		
		
	}

	// Assign the file ids now starting from 0
	for (int i=0; i< noofDistinctDocs; i++)
		uniqueDoc[i]->SetFileId(firstId+i);

	// Now set the cummulative probability of each request based on its popularity
	for (unsigned int j= 0; j < noofDistinctDocs; j++)
	{
		uniqueDoc[j]->SetProb((double)uniqueDoc[j]->GetFreq()/ total);
	}

	totalNoofRequests = total; //this is the new total noof requests that will be generated
}


/*
 * ShowProgress
 * 
 * display a progress information about the current level of the stream 
 * generation
 */

void ShowProgress(int reqGeneratedSoFar, int totalNoofRequests)
{
	printf("\r%d %%  Complete",reqGeneratedSoFar*100/totalNoofRequests);
}

/*
 * CDF
 * 
 * Produce a cummulative distribution values from the input
 * list of integers, which is already in ascending order. inList is
 * input array, outIndex is the number of items in the resulting CDF
 * values
 */

Node* CDF(unsigned int *inList, int& outIndex, int noofDistinctDocs)
{
	if (inList == NULL)
	{
		printf("The list passed to CDF is null");
		exit(1);
	}

	Node *outList = new Node[noofDistinctDocs]; //each node in this new list will
	//contain each unique element and its cummulative probability

	if (outList == NULL)
	{
		printf("Error allocation memory in CDF\n");
		exit(1);
	}

	int currVal, nextVal;

	currVal 	= inList[0];	//current value is the first value in the list
	outIndex 	= 0;	       //index of where unique values will go in outList
	int inIndex;

	for (inIndex = 1; inIndex < noofDistinctDocs; inIndex++)
	{
		nextVal = inList[inIndex];

		if (nextVal != currVal) //we have come to the end of prev unique item
		{
			outList[outIndex].value = currVal;
			outList[outIndex].cummProb = (double)(inIndex)/noofDistinctDocs;
			outIndex++;
			currVal = nextVal; //start a new set of values
		}
	}

	// The last item in the list must be
	outList[outIndex].value 	= currVal;
	outList[outIndex].cummProb 	= (double)(inIndex)/noofDistinctDocs;
	outIndex++;   //this is the total no items in outList;

	// Now copy the exact no of items into a new list and return it
	Node* newOutList = new Node[outIndex];

	if (newOutList == NULL)
	{
		printf("Error allocating memory in CDF\n");
		exit(1);
	}

	// Copy outList to newOutList before returning the pointer to it
	for (int i=0; i < outIndex; i++)
		newOutList[i] = outList[i];

	delete [] outList;

	return newOutList;
}



/*
 * FindValue
 * 
 * Search the input list for where the input randnum lies and
 * interpolate if necessary
 */

unsigned int FindValue(Node* a, int n, double item)
{
	//n is the no of items in this list

	int l 		= 0;
	int r 		= n-1;
	bool done 	= false;
	int loc;

	while (!done)
	{
		int mid = (l+r)/2;

		if ((item <= a[mid].cummProb && mid == 0) ||
			(item <= a[mid].cummProb && item > a[mid-1].cummProb))
		{
			loc  = mid;
			done = true;
		}
		else if (item > a[mid].cummProb && ((mid +1) == r))
		{
			loc  = mid+1;
			done = true;
		}
		else if (item  > a[mid].cummProb)
			l = mid;
		else
			r = mid;
	}

	if ((item == a[loc].cummProb) ||  (loc == 0) )
		return a[loc].value;

	//else interpolate
	int value = (int) ceil((((item - a[loc-1].cummProb)*(a[loc].value -
	a[loc-1].value)) / (a[loc].cummProb - a[loc-1].cummProb)) + a[loc-1].value);

	return value;
}



/*
 * OutputPopAndFileSize
 * 
 * This outputs the info about the distinct files in the workload into
 * a file. The info has only 2 column. The first column is the popularity
 * of each distinct file arranged in descending order. This could easily
 * be extracted and used to plot the popularity ranking graph. The second
 * column is the file size of each distinct file in the workload, but is
 * not sorted. You may have to sort it when you extract that column to
 * plot CDF or LLCD stuffs.
 */
void OutputPopAndFileSize(char* statisticsFile, int noofDistinctDocs, Request **uniqueDoc,bool append)
{
	const char* fmode = append ? "a" : "w";

	FILE *fp = fopen(statisticsFile, fmode);
	for (int i=0; i<noofDistinctDocs; i++)
		fprintf(fp, "%u\t%u\t%u\t%u\n",uniqueDoc[i]->GetFileId(),uniqueDoc[i]->GetFreq(), uniqueDoc[i]->GetFileSize(),uniqueDoc[i]->GetFileType());

	fclose(fp);
}


/*
 * This function is passed to the qsort routine to sort in ascending order
 */
int compare(int *x, int *y)
{
	if ((*x) < (*y))
		return -1;
	else if ((*x) > (*y))
		return 1;
	else
		return 0;
}

/*
 * This function sorts an array of requests object in descending order based on frequency
 */ 
int compare2(Request **x, Request **y) //reverse sorting
{
	if ( (*x)->GetFreq() < (*y)->GetFreq() )
		return 1;
	else if ( (*x)->GetFreq() > (*y)->GetFreq() )
		return -1;
	else
		return 0;
}


/*
 * RequestWebStream::GenerateRequestStream
 * 
 * This is the main routine called after creating an object of this
 * class. This method calls all other methods ar necessary to
 * generate the request stream. Generating a requests stream is done
 * in several stages. Refer to the thesis to understand how
 */ 
void RequestWebStream::GenerateRequestStream()
{

	// First generate a set of popularities for the number of distinct files in the
	// workload and store it in an array. Note that there is just a pointer to this array
	printf("Generating Web workload:\n");
	printf("\tGenerating starting popularities...\n");
	unsigned int *popularities = GeneratePopularities();

	// Generate the Cumulative distribution values from the popularities. Each value
	// in the popularityCDF array has two values: a unique value and the cumm. freq.
	// All the values are stored in the popularityCDF array as shown below which just
	// one pointer to the whole array. The array has noofElement1 elements.
	printf("\tGenerating CDF for popularities...\n");
	int noofElement1; //the total no of elements in CDF values
	Node *popularityCDF = CDF(popularities, noofElement1, noofDistinctDocs);
	delete [] popularities;  //we don't need this array again

	// Do the same above for file sizes.
	printf("\tGenerating starting file sizes...\n");
	unsigned int *filesizes = GenerateFileSizes();

	printf("\tGenerating CDF for file sizes...\n");
	int noofElement2; //the total no of elements in CDF values
	Node *filesizeCDF = CDF(filesizes, noofElement2, noofDistinctDocs);
	delete [] filesizes; //we don't need this array again


	// Now, use the CDFs to generate unique file sizes and popularities and introduce the
	// desired correlation. The generated info about the unique docs in the workload will be
	// stored in an array called uniquedocs, which is an instance variable in this class.

	printf("\tGenerating popularities & file sizes for the distinct requests (possibly with correlation)...\n");
	GenerateUniqueDocs(popularityCDF, noofElement1, filesizeCDF, noofElement2);

	// We don't need these arrays again. We have used them to generate the info about uniqued
	// documents in the workload.
	delete [] popularityCDF;
	delete [] filesizeCDF;

	// Writes the info about each unique docs into a file. The info has only 2 column. The first
	// column is the popularity of each distinct file arranged in descending order. This could
	// easily be extracted and used to plot the popularity ranking graph. The second column is the
	// file size of each distinct file in the workload, but is not sorted. You may have to sort it
	// when you extract that column to plot CDF or LLCD stuffs.
	OutputPopAndFileSize(statisticsFile,noofDistinctDocs, uniqueDoc, false);

	// Now, call the routine that will now generate the workload from the info already computed
	// about each distinct file using the LRU stack approach. The request is stored in the file
	// specified as input to the workload generator. The file has 2 columns: the first column is
	// the fileId and the second column is the file size.
	printf("\tGenerating the requests...\n");
	GenerateAllRequests();

	// Now all requests have been generated
	printf("Done with Web Traffic!\n\n");
}


/*
 * RequestP2PStream::GenerateRequestStream
 * 
 * This is the main routine called after creating an object of this
 * class. This method calls all other methods ar necessary to
 * generate the request stream. Generating a requests stream is done
 * in several stages. 
 */ 
void RequestP2PStream::GenerateRequestStream()
{
	// First generate a set of popularities for the number of distinct files in the
	// workload and store it in an array. Note that there is just a pointer to this array
	// K.Katsaros, 29/10/2009: calling Mandelbrot-Zipf instead...
	printf("Generating P2P workload:\n");
	printf("\tGenerating starting popularities...\n");
	unsigned int *popularities = GeneratePopularities(MZplateau);

	// Generate the Cumulative distribution values from the popularities. Each value
	// in the popularityCDF array has two values: a unique value and the cumm. freq.
	// All the values are stored in the popularityCDF array as shown below which just
	// one pointer to the whole array. The array has noofElement1 elements.
	printf("\tGenerating CDF for popularities...\n");
	int noofElement1; //the total no of elements in CDF values
	Node *popularityCDF = CDF(popularities, noofElement1, noofDistinctDocs);
	delete [] popularities;  //we don't need this array again

	// Do the same above for file sizes.
	// K.Katsaros, 29/10/2009: sampling BitTorrent traces instead...
	// K.Katsaros, 28/04/2011: shall consider using a distribution...
	printf("\tGenerating starting file sizes...\n");
	unsigned int *filesizes = GenerateFileSizes();

	printf("\tGenerating CDF for file sizes...\n");
	int noofElement2; //the total no of elements in CDF values
	Node *filesizeCDF = CDF(filesizes, noofElement2, noofDistinctDocs);
	delete [] filesizes; //we don't need this array again

	// Now, use the CDFs to generate unique file sizes and popularities and introduce the
	// desired correlation. The generated info about the unique docs in the workload will be
	// stored in an array called uniquedocs, which is an instance variable in this class.
	printf("\tGenerating popularities & file sizes for the distinct requests\n");
	GenerateUniqueDocs(popularityCDF, noofElement1, filesizeCDF, noofElement2);

	// We don't need these arrays again. We have used them to generate the info about uniqued
	// documents in the workload.
	delete [] popularityCDF;
	delete [] filesizeCDF;

	//writes the info about each unique docs into a file. The info has only 2 column. The first
	//column is the popularity of each distinct file arranged in descending order. This could
	//easily be extracted and used to plot the popularity ranking graph. The second column is the
	//file size of each distinct file in the workload, but is not sorted. You may have to sort it
	//when you extract that column to plot CDF or LLCD stuffs.
	OutputPopAndFileSize(statisticsFile,noofDistinctDocs, uniqueDoc, true);

	//Now, call the routine that will now generate the workload from the info already computed
	//about each distinct file using the LRU stack approach. The request is stored in the file
	//specified as input to the workload generator. The file has 2 columns: the first column is
	//the fileId and the second column is the file size.
	printf("\tGenerating the requests...\n");
	GenerateAllRequests();

	//Now all requests have been generated
	printf("Done with P2P Traffic!\n\n");
}

/*
 * RequestVideoStream::GenerateRequestStream
 * 
 * This is the main routine called after creating an object of this
 * class. This method calls all other methods ar necessary to
 * generate the request stream. Generating a requests stream is done
 * in several stages. 
 */ 
void
RequestVideoStream::GenerateRequestStream()
{
	// First generate a set of popularities for the number of distinct files in the
	// workload and store it in an array. Note that there is just a pointer to this array
	printf("Generating Video workload:\n");
	printf("\tGenerating starting popularities...\n");
	unsigned int *popularities = GeneratePopularities(video_pop_distr);

	// Generate the Cumulative distribution values from the popularities. Each value
	// in the popularityCDF array has two values: a unique value and the cumm. freq.
	// All the values are stored in the popularityCDF array as shown below which just
	// one pointer to the whole array. The array has noofElement1 elements.
	printf("\tGenerating CDF for popularities...\n");
	int noofElement1; //the total no of elements in CDF values
	Node *popularityCDF = CDF(popularities, noofElement1, noofDistinctDocs);
	delete [] popularities;  //we don't need this array again

	//Do the same above for file sizes.
	printf("\tGenerating starting file sizes...\n");
	srand((unsigned)time(0));
	unsigned int *filesizes = GenerateFileSizes();

	printf("\tGenerating CDF for file sizes...\n");
	int noofElement2; //the total no of elements in CDF values
	Node *filesizeCDF = CDF(filesizes, noofElement2, noofDistinctDocs);
	delete [] filesizes; //we don't need this array again

	// Now, use the CDFs to generate unique file sizes and popularities and introduce the
	// desired correlation. The generated info about the unique docs in the workload will be
	// stored in an array called uniquedocs, which is an instance variable in this class.
	printf("\tGenerating popularities & file sizes for the distinct requests...\n");
	GenerateUniqueDocs(popularityCDF, noofElement1, filesizeCDF, noofElement2);

	// We don't need these arrays again. We have used them to generate the info about uniqued
	// documents in the workload.
	delete [] popularityCDF;
	delete [] filesizeCDF;

	//writes the info about each unique docs into a file. The info has only 2 column. The first
	//column is the popularity of each distinct file arranged in descending order. This could
	//easily be extracted and used to plot the popularity ranking graph. The second column is the
	//file size of each distinct file in the workload, but is not sorted. You may have to sort it
	//when you extract that column to plot CDF or LLCD stuffs.
	OutputPopAndFileSize(statisticsFile,noofDistinctDocs, uniqueDoc, true);

	//Now, call the routine that will now generate the workload from the info already computed
	//about each distinct file using the LRU stack approach. The request is stored in the file
	//specified as input to the workload generator. The file has 2 columns: the first column is
	//the fileId and the second column is the file size.
	printf("\tGenerating the requests...\n");
	GenerateAllRequests();

	//Now all requests have been generated
	printf("Done with Video Traffic!\n\n");
}

/*
 * RequestOtherStream::GenerateRequestStream
 * 
 * This is the main routine called after creating an object of this
 * class. This method calls all other methods ar necessary to
 * generate the request stream. Generating a requests stream is done
 * in several stages. 
 */ 
void
RequestOtherStream::GenerateRequestStream()
{
	// First generate a set of popularities for the number of distinct files in the
	// workload and store it in an array. Note that there is just a pointer to this array
	printf("Generating Other workload:\n");
	printf("\tGenerating starting popularities...\n");
	unsigned int *popularities = GeneratePopularities();

	// Generate the Cumulative distribution values from the popularities. Each value
	// in the popularityCDF array has two values: a unique value and the cumm. freq.
	// All the values are stored in the popularityCDF array as shown below which just
	// one pointer to the whole array. The array has noofElement1 elements.
	printf("\tGenerating CDF for popularities...\n");
	int noofElement1; //the total no of elements in CDF values
	Node *popularityCDF = CDF(popularities, noofElement1, noofDistinctDocs);
	delete [] popularities;  //we don't need this array again

	//Do the same above for file sizes.
	printf("\tGenerating starting file sizes...\n");
	unsigned int *filesizes = GenerateFileSizes();

	printf("\tGenerating CDF for file sizes...\n");
	int noofElement2; //the total no of elements in CDF values
	Node *filesizeCDF = CDF(filesizes, noofElement2, noofDistinctDocs);
	delete [] filesizes; //we don't need this array again

	// Now, use the CDFs to generate unique file sizes and popularities and introduce the
	// desired correlation. The generated info about the unique docs in the workload will be
	// stored in an array called uniquedocs, which is an instance variable in this class.
	printf("\tGenerating popularities & file sizes for the distinct requests...\n");
	GenerateUniqueDocs(popularityCDF, noofElement1, filesizeCDF, noofElement2);

	// We don't need these arrays again. We have used them to generate the info about uniqued
	// documents in the workload.
	delete [] popularityCDF;
	delete [] filesizeCDF;

	// Wwrites the info about each unique docs into a file. The info has only 2 column. The first
	// column is the popularity of each distinct file arranged in descending order. This could
	// easily be extracted and used to plot the popularity ranking graph. The second column is the
	// file size of each distinct file in the workload, but is not sorted. You may have to sort it
	// when you extract that column to plot CDF or LLCD stuffs.
	OutputPopAndFileSize(statisticsFile,noofDistinctDocs, uniqueDoc, true);

	//Now, call the routine that will now generate the workload from the info already computed
	//about each distinct file using the LRU stack approach. The request is stored in the file
	//specified as input to the workload generator. The file has 2 columns: the first column is
	//the fileId and the second column is the file size.
	printf("\tGenerating the requests...\n");
	GenerateAllRequests();

	//Now all requests have been generated
	printf("Done with Other Traffic!\n\n");
}

int get_correlation(float corr, int size) {
	int value;

	if (corr == 0) 
	{
		return(-1);
	} 
	else 
	{
		if (corr > 0) 
		{
			value = (int) (corr * size);
		}
		else 
		{
			value = (int) (corr * (-1) * size);
		}
	}

	return(value);
}


unsigned int RequestP2PStream::SampleFileSize()
{
	unsigned int filesize = 0;
	string tmp_cmd_buff = "pwd";
	system(tmp_cmd_buff.c_str());
	
	tmp_cmd_buff = "pwd";
	system(tmp_cmd_buff.c_str());
	
	
	ostringstream o (ostringstream::out);
	o<<"./sampleBTFileSizes/sampleTorrentFileSize";
	system(o.str().c_str());

	string line;
	ifstream myfile ( "sizeSamples"  , ifstream::in );

	if (myfile.is_open())
	{
		while( getline( myfile, line ) )
		{
			filesize = (unsigned int) atoi(line.c_str());
			//printf("\tRead size = %d\n",filesize);
		}

		myfile.close();
	}
	else 
	{
		printf("Unable to open file size samples file!\n");
		printf(o.str().c_str());
	}

	//tmp_cmd_buff = "ls sampleBTFileSizes";
	//system(tmp_cmd_buff.c_str());
	
	//string cmd_buff = "rm sampleBTFileSizes/sizeSamples";
	//system(cmd_buff.c_str());

	return filesize;
}

