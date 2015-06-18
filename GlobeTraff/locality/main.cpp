/* main.cpp
 * Aug 15,2001
 * Yujian (Peter) Li
 * Temporal Locality Generator
 *
 * Design idea:
 * Two classes are defined. availDocs, lrustack.
 * 1. the availDocs object represents:
 * available documents with number of references desired,
 * which are currently available to reference but not in stack.
 * 2. the lrustack object represents:
 * LRU stack which stores the docs which are referenced recently.
 * these two objects exchange info via common doc name.
 * 
 * The source files:
 * lrustack.h, availdocs.h,
 * main.cpp,lrustack.cpp,availdocs.cpp
 *
 * Pre-condition parameters are given as macro defination:
 * N,M,L,Mode.
 * The given data source files:
 *  docs.dat --- file with docID, num of references, and size info
 *  stack.dat -- file with LRU stack referencing probabilities
 *
 * Reference stream is printed, also saved in result.dat
 * 
 * running environment Visual C++ 5.0 IDE.
 * 
 */
#include "lrustack.h"
#include "availdocs.h"
#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <iostream>
#include <string.h>
#include <math.h>

#include "../prowgen/stream.h"
#include "../prowgen/distributions.h"

double log(); 

using namespace std;

/*
#define M 100 //num of references,integer,1-10,000,000
#define N 10 //num of docNames,integer, 1-10,000,000
*/

//#define L 1000 //LRU stack depth,integer, 1-10,000,000

int Mode = 1; 
//#define Mode 1 //0,1,2 are independent,static,dynamic.
               // 3 is static but with preloading of stack   - CLW
               // 4 is dynamic but with preloading of stack   - CLW

/* #define ALPHABETIC 1 */

/* arrays to store given doc data:
 * name of each doc
 * number of references desired for each doc */
/*
int docNames[N];
int docFreqs[N];
int docSizes[N];
*/

int *docNames;
int *docFreqs;
int *docSizes;
float *docTimest;

float timest = 0.0;
int L;
long long M;
long long N;

/* available documents with num of references */
availdocs* avaDocs;

/* LRU Stack, for static/dynamic model,
 * the initial stack prob provided externally */
lrustack* stack1;

//float stackDepthProb[L];
float *stackDepthProb; 

/* generated reference stream for printing */
//int refStream[M];   
int *refStream; 

//int *docRefPtr = refStream; 
int *docRefPtr;

//Function to read given docs.dat file
//the data file is assumed as pairs of (doc name,num of references)
void readDocs(void);

//Function to read stack prob from stack.dat
//used for static/dynamic model
void readStackProb(void);

//Function to generate and store reference of doc accessed
void generateRef(int doc);

//Function to output the generated reference stream
void outputRef(void);

//Function to generate a random float number within 0 - 1 
float generateRand01(void);

//Important function to process the reference requests 
void process(void);


//Timestamp functions.
//double Uniform01();
//double Exponential(double mu);
Distributions* distributions;
double current_time(double time);
char *inpfile; 
double currentT = 0.0;
float fudge_factor;   // aka probability bias. 
char *dataDir;
char *webDocsFile;
	
//begin main()
int main(int argc, char **argv) {
   
        if (argc != 8) {
                printf("Usage: lrustack probabilities_file fudge_factor L M N > output_file\n");
                exit(-1);
        }

	fudge_factor = atof(argv[2]);        //Probability bias
	L = atoi(argv[3]);
	M = atoll(argv[4]);
	N = atoll(argv[5]);

	dataDir = (char *) malloc(100 * sizeof(char)); 
	memset(dataDir, 0, 100); 
	strcpy(dataDir, argv[1]);  //1
	
	inpfile = (char *) malloc(100 * sizeof(char)); 
	memset(inpfile, 0, 100); 
	strcpy(inpfile, dataDir);  //1
	strcat(inpfile, argv[7]); 	

	strcat(dataDir, "data/"); 	

	webDocsFile = (char *) malloc(100 * sizeof(char)); 
	memset(webDocsFile, 0, 100); 
	strcpy(webDocsFile, dataDir);  //1
	strcat(webDocsFile, "docs.web"); 	

	if ((stackDepthProb = (float *) malloc(L * sizeof(float))) == NULL) { 
                printf("Cannot allocate memory for stackDepthProb\n");
                exit(-1);
	}	
        if ((docNames = (int *) malloc(N * sizeof(int ))) == NULL) {
                printf("Cannot allocate memory for docNames\n");
                exit(-1);
        }
        if ((docFreqs = (int *) malloc(N * sizeof(int ))) == NULL) {
                printf("Cannot allocate memory for docFreqs\n");
                exit(-1);
        }
        if ((docSizes = (int *) malloc(N * sizeof(int ))) == NULL) {
                printf("Cannot allocate memory for docSizes\n");
                exit(-1);
        }
        if ((refStream = (int *) malloc(M * sizeof(int ))) == NULL) {
                printf("Cannot allocate memory for refStream\n");
                exit(-1);
        }
        if ((docTimest = (float *) malloc(N * sizeof(int ))) == NULL) {
                printf("Cannot allocate memory for docTimest\n");
                exit(-1);
        }

        docRefPtr = refStream;


	// read given data file into global array:
	// docNames[],docFreqs[]
	readDocs();

	//create and initialize availible docments object
	avaDocs = new availdocs(N,M);
	avaDocs->init(docNames,docFreqs);

    // create and initialize LRU Stack based on Model
    // for independent model
	if (Mode==0) {
		stack1 = new lrustack(N,Mode);
		stack1->init(M,docNames,docFreqs);
	}
    // for static model
	else if (Mode==1){
		stack1 = new lrustack(L,Mode);
		readStackProb();
		stack1->init(stackDepthProb);
	}
	// for dynamic model
	else if (Mode==2) {
		stack1 = new lrustack(L,Mode);
		stack1->init();
		readStackProb();
		avaDocs->bind(stackDepthProb);
	}
	// for Carey's new models
	else {
		stack1 = new lrustack(L,Mode);
		readStackProb();
		stack1->init(docNames,stackDepthProb);
		for( int i = 0; i < L; i++ )
		  avaDocs->mark(i);
	}

	distributions = new Distributions();
	
   //process the reference requests 
   process();

   //print the generated reference series
   outputRef();
        free(docNames);
        free(docTimest);
        free(docSizes);
        free(docFreqs);
        free(refStream);
	free(stackDepthProb); 

	delete dataDir;
	delete webDocsFile;
	return(0); 
} // end main() 



/* Function to return a random float number within 0 - 1
 * uniformally. RAND_MAX is max random number defined in stdlib.h
 */
float generateRand01() {
	float randnum;
	randnum = (float)rand();
	randnum = (randnum / RAND_MAX); 
	return(randnum);       
}

/* Function to read given file named docs.dat 
 * the data file is assumed with two columns.
 * 1st col: doc name
 * 2nd col: num of references desired for each doc
 * The given data for each doc are stored in global var:
 * docNames[],docFreqs[] */
void readDocs() {
	FILE *doc_file;
	doc_file=fopen(webDocsFile, "r"); 
	if( doc_file == NULL )
	  fprintf(stderr, "Could not find file docs.web\n");
	int id,refnum,filesize,filetype;
	// sequentially read each line until end of file
	for (int i=0;i<N;i++) {
		fscanf(doc_file, "%d\t%d\t%d\t%d",&id, &refnum, &filesize,&filetype);
		//cerr<<"ID: "<<id<<" Ref Num: "<<refnum<<" File Size: "<<filesize<<" File Type: "<<filetype<<endl;
		docNames[i] = id;
		docFreqs[i] = refnum;
		docSizes[i] = filesize;
	} 
   fclose(doc_file);
}

//Function to read stack prob from stack.dat
//used only for static model
void readStackProb(void) {
	FILE *stackProb_file;
	stackProb_file=fopen(inpfile, "r"); 
	if( stackProb_file == NULL )
	  fprintf(stderr, "Could not find file %s \n", inpfile);
	float prob;
	// sequentially read each line until end of file
	for (int i=0;i<L;i++) {
		fscanf(stackProb_file, "%f ",&prob);
		stackDepthProb[i] = prob;
	}
   fclose(stackProb_file);
}


//Function to process the reference requests 
void process(void) {

   /* Seed the random-number generator with current time so that
    * the numbers will be different every time we run. */
    srand( (unsigned)time( NULL ) );

   int refCounter = M; //count from M to 1
   int doc; //document to be referenced
   bool inStack; //does the reference go to stack ?
   int reflevel; //the stack level which is referenced
   float randnum;

   /* for each refrence request, do: */
   while (refCounter>0) {
	   randnum = generateRand01();

	   //	   stack1->output();

	   inStack = stack1->inStack(randnum);
	   
	   /* if the reference goes to the stack, */
	   if ( inStack ==true) {
	   	   //find the reference level and
		   //the  document to be referenced
		   reflevel = stack1->reflevel();
		   doc = stack1->docAt(reflevel);
		   //   cout<<"Referencing doc "<<doc<<" on level "<<reflevel<<endl;
	   }
 	   /* if the reference does not go to the stack, */
	   else {
		   // for independent model, all docs stored in stack
		   if (Mode==0) continue; 

		   randnum = generateRand01();
		   //choose a doc from availible docs(not stack) randomly
                   // this is the fudge factor part    Carey
		   if( randnum < fudge_factor )
		     {
		       randnum = generateRand01();
		       doc = avaDocs->docChosen(randnum);
		     }
		   else
		     {
		       randnum = generateRand01();
		       doc = avaDocs->docChosen2(randnum,refCounter);
		     }
		   //		   cout<<"Referencing doc "<<doc<<" at random"<<endl;
		   //if no avail doc,pick a doc from stack randomly
		   if (doc < 0) { 
			   doc = stack1->docChosen(randnum);
			   reflevel = stack1->reflevel();
			   inStack=true;
			   //			   cout<<"Referencing random doc "<<doc<<" on level "<<reflevel<<endl;
		   }
	   }
  	
	   //generate reference based on doc accessed
	   generateRef(doc);

	   //decrease number of references of doc accessed
	   avaDocs->decreaseCountOf(doc);
	   refCounter--;

	   //check if more reference of this document required
	   //if yes, add to stack or update the stack
	   if ( avaDocs->quotaOf(doc)!= 0 ) {
		   if (Mode==0) ; //do nothing for independent model
		   else  {
			   if (inStack==false) {
				   int victim;
				   if (Mode==1 || Mode==3)
					   victim = stack1->add(doc);
				   if (Mode==2 || Mode==3) {
					   float docprob = avaDocs->prob(doc);
					   victim = stack1->add(doc,docprob);
				   }
				   avaDocs->mark(doc);
				   // does the stack overflow ?
				   if (victim > -1) avaDocs->mark(victim);
			   }
			   else 
				   stack1->update(reflevel);
		   }
	   }
	   //if no, delete the doc from consideration
	   else {
	     //	     cout<<"Done with that doc "<<doc<<endl;
		   avaDocs->del(doc);
		   if (inStack==true) stack1->del(doc);
	   }
   }
}


//Function to generate reference and store into refStream[]
void generateRef(int doc){
	*docRefPtr = doc;
	docRefPtr++;
}

//Function to output the generated reference series
void outputRef(void) {

        srand(12357);
	docRefPtr = refStream;

//	cout<<"N:"<<N<<"  M:"<<M
//		<<"  L:"<<L<<"  Model:"<<Mode<<endl<<endl;
	srand((unsigned)time(0));
	for (int i=0;i<M;i++) {

#ifdef ALPHABETIC
//		fprintf(stdout, "%c ",docRefPtr[i]+'A'-1);
//		cout<<endl;
#else

		currentT = current_time(currentT); 
		printf("%f\t%d\t%d\n", currentT, docRefPtr[i], docSizes[docRefPtr[i]]); 
//		cout<<docRefPtr[i]<<" "<<docSizes[docRefPtr[i]]<<endl;
#endif 
	}

/*	FILE *refStream;
	refStream=fopen("result.dat", "w"); 
	if( refStream == NULL )
	  fprintf(stderr, "Could not create file result.dat\n");
	for (int i=0;i<M;i++) {
#ifdef ALPHABETIC
		fprintf(refStream, "%c\n",docRefPtr[i]+'A'-1);
#else
		fprintf(refStream, "%d %d\n",
			docRefPtr[i], docSizes[docRefPtr[i]]);
#endif ALPHABETIC
	} 
   fclose(refStream);
*/
}


double
current_time(double time)
  {
    double now;

//    srand(12357);
    now = time + distributions->Exponential(1.0/ARRIVAL_RATE);
    //float m = 1.5;
    //float s = 0.80;
    //now = time + distributions->LognormalCDF(m,s);
    return(now);
  }


