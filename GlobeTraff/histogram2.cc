/*------------------------Histogram.c-------------------------------*/
// This program reads data from <infile> and generates the histogram 
// of the reading data with cell size  -b <binsize>. The output is 
// in <outfile>. If the cell size is not given, the default is 10.
//
// Compile: g++ -o histogram histogram.cc
//
// Usage: histogram [-b <binsize>] <infile> <outfile>
// binsize -- the size of the statistical bin
/*------------------------------------------------------------------*/
#include <stdio.h>
#include <iostream>
#include <fstream>
#include <cstdlib>
#include <cstring>
#include <cmath>
#include <unistd.h>

using namespace std;

#define DEFAULTBIN 10  //default bin size
#define MAXBIN 100000
#define MAX -214748300
#define MIN 2147483000

long int get_bin(char *); 

class histogram {
public:
  histogram(long int binsize, char *infile, char *outfile);
  ~histogram();
  void calculate();
  void output();

private:
  float max_num, min_num; //the maximum number from the input file
  long int bin;   //the size of the bin
  int numofbin; //the total number of bins for the file
  int histoplus[MAXBIN];  //store the count for positive bin
  int histoneg[MAXBIN];    //store the count for negative bin
  ifstream in;   //the stream for input
  ofstream out;  //the stream for output
};


histogram::~histogram() {   
  in.close();
  out.close();
}


histogram::histogram(long int binsize, char *infile, char *outfile) {
  int i;
  
  bin = binsize;
  //Open two files and attach them to in and out streams
  in.open(infile);
  if(!in) {  
    cout << "Error open file:" << infile << endl;
    exit (1);
  }

   out.open(outfile);
  if(!out) {
    cout << "Error open file:" << outfile << endl;
    exit (1);
  }

  for(i = 0; i< MAXBIN; i++) {
    histoplus[i] = 0;
    histoneg[i] = 0;
  }

  max_num = MAX;
  min_num = MIN;
}



/*--------------------------------------------------------------*/
// Get the count of each bin
/*--------------------------------------------------------------*/
void histogram::calculate() {

  char str[100]; //temporay variable to store read in string
  float data;  //the read in data
  float last;
  int index, total = 0;

  while(!in.eof()) {
    in.getline(str, 100);
    if(in.eof()) break;
    data = atof(str);
    //data = log10(data);
    total++;

    if(data >= 0) {
      index = (int) (data / bin + 0.5);
      histoplus[index]++;
    } else {
      index = (int) ((-data) / bin + 0.5);
      histoneg[index]++;
    }
      
    if(data > max_num)
      max_num = data;    //keep the maximum number
    if(data < min_num)
      min_num = data;   //keep the minimum number
   
  }
  
}


/*--------------------------------------------------------------*/
// Get the count of each bin
/*--------------------------------------------------------------*/
void histogram::output() {
  int low, high;
  
  if(min_num >= 0) {
    low = (int) (min_num / bin + 0.5);
    high = (int) (max_num / bin + 0.5);
    for(int i=low; i<= high; i++)
      out << i*bin  << " " <<  histoplus[i] << endl;
  } else {
    low = (int) (- min_num / bin + 0.5);
    high = (int) (max_num / bin + 0.5);
    for (int i = low; i> 0; i--)
      out << -i*bin << " " << histoneg[i] << endl;
    for (int j = 0; j <= high; j++)
      out << j*bin << " " << histoplus[j] << endl;
  }
}


int main(int argc, char **argv) {

  long int bin;   //the size of the bin
  char *infile = NULL;
  char *outfile = NULL;

  if(argc != 3) { //incorrect number of parameters
//      cout << "Usage: histogram -s <binsize> <maxnum> <infile> <outfile>" << endl;
      cout << "Usage: histogram <infile> <outfile>" << endl;

      exit(1);
  }else { 
        infile = argv[1];
	printf("%s -infile \n", infile); 
//      bin = atof(argv[2]);
	bin = get_bin(infile); 
	printf("binsize = %d\n", bin); 
	fflush(NULL);
        outfile = argv[2];
  }
	 
  
  histogram histo(bin, infile, outfile), *pt;
 
  pt = &histo;
  pt->calculate();  //get the count of each bin
  pt->output();  //output the results
}


long int get_bin(char *fname) { 
	FILE *fp; 
	long int all; 
	long int fid, size; 
	float timest; 

	all = 0; 
	if ( (fp = fopen(fname, "r")) == NULL ) { 
		printf("cannot open file %s\n", fname);
		exit(-1); 
	} 
	while (fscanf(fp, "%f %d %d\n", &timest, &fid, &size) > 0) { 
		all += size; 
	}

	size = 40 ; //all /323400; 
	printf("all - %d, size - %d\n", all, size); 
	fflush(NULL); 
	fclose(fp); 
	return(size); 
} 	
