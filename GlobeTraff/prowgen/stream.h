
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



// stream.h
//
//  This class defines a class RequestStream representing a stream of 
//  requests to be generated. 


#include "request.h"
#include "distributions.h"

#ifndef STREAM_H
#define STREAM_H

//This class (Node) is just for miscelaneous use
class Node{  //store a value and its cummulative probability
 public:
     int value;
     double cummProb;
};


//TODO: Create a single RequestStream class and use inheritance...

class RequestWebStream {
 public:
   
    RequestWebStream(char* initRequestStreamFile,
					 char* initStatisticsFile, 
					 float initZipfSlope,
					 float initParetoTailIndex, 
					 int initTotalNoofRequests,
					 float  oneTimersPerc, 
					 float  distinctDocsPerc, 
					 float corr,
					 unsigned int initStacksize, 
					 unsigned int initStackmode,
					 float initPercAtTail, 
					 float initK, 
					 float initLognormMeanSqr, 
					 float initLognormVariance,
					 Distributions* distrib); 
                    

    ~RequestWebStream(); //Destructor

    void GenerateRequestStream(); //This is the main method called from main.cc

	int LastObjectId();

 private:
    
    unsigned int* GeneratePopularities(); //generates a list of popularities
                                  //following the zipf distribution. 

    unsigned int* GenerateFileSizes(); //generate file sizes from pareto & lognormal
                               //distributions
    
    //Node* CDF(unsigned int *inList, int& outIndex); //produce a cummulative probability from the 
                             //input list and returns the result as another list

    void GenerateUniqueDocs(Node* popCDF, int noofItems1, Node* sizeCDF, int noofItems2);
                                   //this function will introduce either +ve,
                                   //-ve, or zero correlation between popularity 
                                   //and file sizes of documents

    //unsigned int FindValue(Node *a, int n, double item);//locates where  the random number                         
                                              //is located in the list and interpolate if necessary

    void GenerateAllRequests(); //this will generate the requests into a file specified in main.cc

    
    char* requestStreamFile; //the final output file containing the requests
    char* zipfFile;          //this file will contain the zipf ranking and freq
    char* statisticsFile;    //some statistics about the generated stream(pop and file size of unique docs)
    float zipfSlope;         //zipf parameter 
    float paretoTailIndex;   //pareto distribution parameter
    int   maxPopularity;     //frequency of the most popular file
    int   noofDistinctDocs;  //number of distinct documents requested
    int   totalNoofRequests; //the total no of requests generated
    int noofonetimers; //the number timers of the distinct docs in the requests stream
    float correlation; //a +ve, -ve, or zero value to indicate correlation
                       //between popularity and file size
    unsigned int stacksize;
    unsigned int stackmode;
 
    float percAtTail; //percentage of the disitinct docs at the tail of the distribution
    float K; //this indicates the beginning of the tail of the distribution
    float paramMean, paramStd;
    //float mean, std;  //mean and standard dev of the lognormal variate used
                      //to model the body of the file size distribution
	
    Request **uniqueDoc;  // the unique files in the workload
    
    Distributions* distributions;
};

    unsigned int FindValue(Node *a, int n, double item);//locates where  the random number                         
                                              //is located in the list and interpolate if necessary
    Node* CDF(unsigned int *inList, int& outIndex, int noofDistinctDocs); //produce a cummulative probability from the 
                             //input list and returns the result as another
                             
class RequestP2PStream {
 public:
   
	RequestP2PStream(char* initRequestStreamFile,
					 char* initStatisticsFile, 
					 float initMZSlope, 
					 int initTotalNoofRequests, 
					 float distinctDocsPerc,
						float MZplateau,
						float tracesTau,
						float tracesLamda,
						int torrentInterarrival,
						float p2p_size_median,
					 unsigned int firstId, 
					 Distributions* distrib,
					 bool fixedP2PSize);
	/*	  
    RequestP2PStream(char* initRequestStreamFile,
		  char* initStatisticsFile, float initZipfSlope,
		  float initParetoTailIndex, int initTotalNoofRequests,
		  float  oneTimersPerc, float  distinctDocsPerc, float corr,
		  unsigned int initStacksize, unsigned int initStackmode,
		  float initPercAtTail, float initK, float initMean, float initStd); 
      */              

    ~RequestP2PStream(); //Destructor

   

    void GenerateRequestStream(); //This is the main method called from main.cc

	int LastObjectId();
	float LastObjectReqTime();
	
 private:
    unsigned int* GeneratePopularities(unsigned int q); //generates a list of popularities
                                  //following the zipf distribution. 

    unsigned int* GenerateFileSizes(); //generate file sizes from pareto & lognormal
                               //distributions
    
    //Node* CDF(unsigned int *inList, int& outIndex); //produce a cummulative probability from the 
                             //input list and returns the result as another list

	void  GenerateUniqueDocs(Node* popCDF, int noofItems1, Node* sizeCDF, int noofItems2);

    //unsigned int FindValue(Node *a, int n, double item);//locates where  the random number                         
                                              //is located in the list and interpolate if necessary

    void GenerateAllRequests(); //this will generate the requests into a file specified in main.cc

	double*  bitTorrentInterarrivalTimes(float lamda, float tau, float tracesSeeding, unsigned int noofRequests);
     
	unsigned int SampleFileSize();

    char* requestStreamFile; 	//the final output file containing the requests
    char* zipfFile;          	//this file will contain the zipf ranking and freq
    char* statisticsFile;    	//some statistics about the generated stream(pop and file size of unique docs)
    unsigned int firstId;
    float MZSlope;         	//zipf parameter 
    float p2p_size_median;
    int   noofDistinctDocs;  	//number of distinct documents requested
    int   totalNoofRequests; 	//the total no of requests generated
    int   maxNoofRequestsPerObject; //the max no of requests generated for a single object
 
	unsigned int MZplateau; //The plateau factor of Mandelbrot-Zipf distribution
   	float tracesTau;			//Parameters for the exponential decay function
	float tracesLamda;
	float tracesSeeding;
	float interTorrentInterval;

	float lastObjectReqTime;

    Request **uniqueDoc;  // the unique files in the workload
    Distributions* distributions;
    bool fixedP2PSize;
};

class RequestVideoStream {
 public:

	RequestVideoStream(char* initRequestStreamFile,
					   char* initStatisticsFile, 
					   float initzipfSlope,
					   int initTotalNoofRequests,
					   float distinctDocsPerc, 
							float weibullK,
							float weibullL,
							float gammaK,
							float gamma8,
							float alpha,
							float alphaBirth,
							//float m,
							//float s,
					   unsigned int firstId,
					   Distributions* distrib,
					   float lastWebReqTime,
					   int video_pop_distr);
	/*	     
    RequestVideoStream(char* initRequestStreamFile,
		  char* initStatisticsFile, float initZipfSlope,
		  float initParetoTailIndex, int initTotalNoofRequests,
		  float  oneTimersPerc, float  distinctDocsPerc, float corr,
		  unsigned int initStacksize, unsigned int initStackmode,
		  float initPercAtTail, float initK, float initMean, float initStd); 
     */               

    ~RequestVideoStream(); //Destructor

   

    void GenerateRequestStream(); //This is the main method called from main.cc

	int LastObjectId();

 private:
    unsigned int* GeneratePopularities(unsigned int distr); //generates a list of popularities
                                  //following the zipf distribution. 

    unsigned int* GenerateFileSizes(); //generate file sizes from pareto & lognormal
                               //distributions
    
    //Node* CDF(unsigned int *inList, int& outIndex); //produce a cummulative probability from the 
                             //input list and returns the result as another list

	void  GenerateUniqueDocs(Node* popCDF, int noofItems1, Node* sizeCDF, int noofItems2);

    //unsigned int FindValue(Node *a, int n, double item);//locates where  the random number                         
                                              //is located in the list and interpolate if necessary

    void GenerateAllRequests(); //this will generate the requests into a file specified in main.cc
    
    char* requestStreamFile; 	//the final output file containing the requests
    char* zipfFile;          	//this file will contain the zipf ranking and freq
    char* statisticsFile;    	//some statistics about the generated stream(pop and file size of unique docs)
    unsigned int firstId;
    float zipfSlope;         	//zipf parameter 
    float weibullK;
    float weibullL;
    float gammaK;
    float gamma8;
    float alpha;
    float alphaBirth;
    //float m,s;
    int   maxPopularity;     	//frequency of the most popular file
    int   noofDistinctDocs;  	//number of distinct documents requested
    int   totalNoofRequests; 	//the total no of requests generated
    int   maxNoofRequestsPerObject; //the max no of requests generated for a single object
    //int noofonetimers; //the number timers of the distinct docs in the requests stream
    //float correlation; //a +ve, -ve, or zero value to indicate correlation
                       //between popularity and file size
    

    Request **uniqueDoc;  // the unique files in the workload
    
    float videoInterArrivalTime;
    float videoRequestInterArrivalTime;
    Distributions* distributions;
    int video_pop_distr;
};

class RequestOtherStream {
 public:
   
	RequestOtherStream(char* initRequestStreamFile,
					   char* initStatisticsFile, 
					   int initTotalNoofRequests, 
					   float distinctDocsPerc,
					   float initZipfSlope,
					   unsigned int first_ID,
					   Distributions* distrib,
					   float lastP2PReq,
					   float other_size,
					   float web_other);
        

    ~RequestOtherStream(); //Destructor

   

    void GenerateRequestStream(); //This is the main method called from main.cc
	
 private:
    unsigned int* GeneratePopularities(); //generates a list of popularities
                                  //following the zipf distribution. 

    unsigned int* GenerateFileSizes(); //generate file sizes from pareto & lognormal
                               //distributions
    
	void  GenerateUniqueDocs(Node* popCDF, int noofItems1, Node* sizeCDF, int noofItems2);

    void GenerateAllRequests(); //this will generate the requests into a file specified in main.cc

    unsigned int firstId;
    char* requestStreamFile; 	//the final output file containing the requests
    char* statisticsFile;    	//some statistics about the generated stream(pop and file size of unique docs)
    float zipfSlope;         	//zipf parameter 
    float other_size_median;
    int   noofDistinctDocs;  	//number of distinct documents requested
    int   totalNoofRequests; 	//the total no of requests generated
	float otherInterArrivalTime;
	float otherRequestInterArrivalTime;
    Request **uniqueDoc;  // the unique files in the workload
    Distributions* distributions;
    float web_other_rel; //Used to adjust the arrival rate of the requests
};


void ShowProgress(int reqGeneratedSoFar, int totalNoofRequests); //display a progress information
                          // about the current level of the stream generation
void OutputPopAndFileSize(char* statisticsFile, int noofDistinctDocs, Request **uniqueDoc, bool append);
//int UniformInt(int range);
//double exponentialDecayArrivalRate(float lamda, float tau, float tracesSeeding);
//float Weibull(int rank, float weibullK, float weibullL);
//float ParetoCDF(float alpha);
//float LognormalCDF(float m, float s);
#endif  // STREAM_H
