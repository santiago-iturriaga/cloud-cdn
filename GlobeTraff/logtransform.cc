#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <iostream>

//opens a file and transforms the x and y values into log10


int main(int argc, char* argv[]) 
{
  FILE *infile;
 
  //unsigned long int x, y;
  float x, y;
  // open the input file to the function
  
  if((infile = fopen(argv[1],"r")) == NULL)
    {
      printf("Unable to open %s for input.\n",argv[1]);
      exit(1);
    }
  
  while(!feof(infile))
    {
      fscanf(infile,"%f %f\n",&x,&y);
      if (x > 0 && y > 0)
          printf("%f %f\n", log10(x),log10(y));
	 
    }

  fclose(infile); // close infile
  
}

