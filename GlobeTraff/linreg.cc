#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <iostream>

// Function to find the best linear fit. It returns the 
// intercept on yaxis (Y), the slope (M) and the coefficient of 
// determination of the distribution (R2).

int main(int argc, char* argv[]) 
{
  FILE *infile;
  double sumxy = 0.0 ;
  double sumx  = 0.0 ;
  double sumy  = 0.0 ;
  double sumyy = 0.0 ;
  double sumxx = 0.0 ;
  unsigned long int  n = 0         ;
  float x, y;
  double meanx, meany;
  double SST, SSE, slope, interceptY, Rsquare; 

 
  // open the input file to the function
  
  if((infile = fopen(argv[1],"r")) == NULL)
    {
      printf("Unable to open %s for input.\n",argv[1]);
      exit(1);
    }
  
  while(!feof(infile))
    {
          fscanf(infile,"%f %f\n",&x,&y);
	  n += 1; 
	  sumxy += (double)x * (double)y ;
	  sumxx += (double)x * (double)x;
	  sumx  += (double)x;
	  sumy  += (double)y;
	  sumyy += (double)y * (double)y; 
     
    }

  fclose(infile); // close infile
  
  meanx = sumx / n ;
  meany = sumy / n ;

  // calculating the coefficient of x

  slope = (sumxy - ((double)n * meanx * meany)) /
    (sumxx - ((double)n * (meanx * meanx))) ; //calculate b1

  
  interceptY = meany - ((slope) * meanx) ; // calculate bo
  
  SST = (sumyy - ((double)n * meany * meany))                 ;
  SSE = (sumyy - ((interceptY) * sumy) - ((slope) * sumxy)) ;
  
  Rsquare = (SST - SSE) / SST ;

  printf("Slope = %f \nY-Intercept = %f \nRsquare = %f\n", slope, interceptY, Rsquare);

}

