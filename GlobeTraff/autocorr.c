/* Calculates autocorrelation coefficients. */

/* The output will be the autocorrelation coefficient covering the */
/* entire data set, with given gap size "k". The number of output values */
/* can be controlled by setting the gap stepsize, start, and end values. */
/* The mean of the input data and the maximum and minimum coefficients */
/* are output to stderr and not to the output file.                    */


/* Usage: autocorr <infile> [stepsize] [start] [end] > outfile  */
/*
/* Parameters in [] are optional.
/*							*/
/* infile: the input file. Data should be ASCII real numbers, */
/*         one per line.				      */
/* stepsize: the amount the gap size "k" increases per iteration. Default */
/*           is 1.						*/
/* start: the value the gap size starts from. Default is 0.  */
/*        calculations).                                             */
/* end: the value the gap size ends at. Default is the length of the data. */
/* outfile: output data. The first column is the gap size, the second is */
/*          then autocorrelation coefficient for that gap size.          */

#include <stdio.h>
#include <stdlib.h>

#define STEPSIZE 1
#define START 0
#define END 0x7FFFFFFF

int main (int argc, char *argv[])
{
   FILE *infile;
   double mean = 0.0;
   int counter = 0;
   int stepsize = STEPSIZE;
   int start = START;
   int end = END;
   float current;
   float *buffer,*p;
   double numerator,denominator,t;
   int g, i;
   double max_coeff = -1.0, min_coeff = 1.0;
   double coeff;

   if (argc < 2)
   {
      fprintf(stderr,"\nUsage : %s <infile> [stepsize] [start] [end] > <outfile>\n\n",argv[0]);
      exit(1);
   }

   if ((infile = fopen(argv[1],"r")) == NULL)
   {
      printf("\nUnable to open input file %s.\n\n",argv[1]);
      exit(1);
   }

   if (argc > 2)
      sscanf(argv[2],"%d",&stepsize);

   if (argc > 3)
      sscanf(argv[3],"%d",&start);

   if (argc > 4)
      sscanf(argv[4],"%d",&end);

   /* Find mean */
   while (fscanf(infile,"%f",&current) != EOF)
   {
      mean += (double) current;
      counter++;
   }

   mean /= counter;

   /*    fprintf(stderr,"Mean of input data : %6.3f\n",mean); */

   /* Number of values known - allocate storage. */

   if ((buffer = (float *) malloc(counter*sizeof(float))) == NULL)
   {
      fprintf(stderr,"\nMemory allocation error.\n\n");
      exit (1);
   }

   p = buffer;
 
   if (end > (counter - 1))
      end = (counter - 1);

   
   /* Read values into array. */
   rewind(infile);

   while (fscanf(infile,"%f",p++) != EOF);


   /* Find autocorrelation coefficients. */ 
   for (g = start; g < end; g += stepsize)
   {

      p = buffer + g;

      numerator = denominator = 0.0;

      for (i = g; i < counter; i++,p++)
      {
         numerator += ((double) (*p) - mean)*((double) (*(p-g)) - mean);
         t = (double) (*p) - mean;
         denominator += t*t;
      }


      /* This part handles the case when numerator and denominator are */
      /* equal to zero (in which case the autocorrelation coeff. is    */
      /* zero). The "epsilon" value (.00001) is for handling floating  */
      /* point errors.                                                 */
      if ((denominator < 0.00001) && (numerator < 0.00001) && (numerator > -0.00001))
         coeff = 1.0;
      else
         coeff = numerator / denominator;

      printf("%d %10.6lf\n",g,coeff);

      max_coeff = coeff > max_coeff ? coeff : max_coeff;
      min_coeff = coeff < min_coeff ? coeff : min_coeff;

   }

   /*   fprintf(stderr,"Max. coefficient : %6.4lf\n",max_coeff); */
   /*   fprintf(stderr,"Min. coefficient : %6.4lf\n",min_coeff); */

   fclose(infile);

   return (0);

} 
