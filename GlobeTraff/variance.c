/****************************************************************/
/* This program is to calculates the variances of aggregated    */
/* process X(m). The input of this program will be packet counts*/
/* (a given sample of N observations). We subdivides the whole  */
/* sample into N/m non-overlapping blocks with each has m obser-*/
/* vations and computes the varinaces of x(m).                  */
/****************************************************************/ 
  
 #include <stdio.h>
 #include <stdlib.h>
 #include <math.h>

  main (int argc, char *argv[])
  {
   FILE *infile, *outfile;
   double mean,  var;
   double x;
   int counter = 0;
   int m ;
   float *buffer, *p;
   float current;
   int i,j, n ;


   if (argc < 2)
   {
      fprintf(stderr,"\nUsage : %s <infile> <outfile1>  [start] [end] \n\n",argv[0]);
      exit(1);
   }

   if ((infile = fopen(argv[1],"r")) == NULL)
   {
      printf("\nUnable to open input file %s.\n\n",argv[1]);
      exit(1);
   }

   if ((outfile = fopen(argv[2],"w")) == NULL)
   {
      printf("\nUnable to open output file %s.\n\n",argv[2]);
      exit(1);
   }



   /* Find mean */

   
   mean = 0.0;
   /* Find mean */
   while (fscanf(infile,"%f",&current) != EOF)
   {
      mean += (double) current;
      counter++;
   }

   mean /= counter;


 /*  fprintf(outfile,"Mean of input data : %6.3f\n",mean);*/ 


   if ((buffer = (float *) malloc(counter*sizeof(float))) == NULL)
     {
        fprintf(stderr,"\nMemory allocation error.\n\n");
        exit (1) ;
     }

    p = buffer;

   rewind(infile);

   while (fscanf(infile,"%f",p++) != EOF);

#ifdef YING_OLD_WAY
   for (m = 1; m < counter/2; m++)
#else
   m = 1;
   while( m < counter/2 )
#endif
   {
    var = 0.0;
    n =  counter/m  ; /* number of non-overlapping intervals */
    for (i = 1; i <= n ; i++)
     { 
        x = 0.0;
        p = buffer+(i-1)*m;
        for (j = 1; j <= m; j++)  /* making a new process X(m) */
           x += (double)(*p++);
        x /= (double) m;        
        var += (x - mean)*(x - mean); 
     }
    var /= n;
    fprintf(outfile ,"%d  %8.6f\n", m, var);

#ifndef YING_OLD_WAY
    if( m < 10 )
	m += 1;
    else if( m < 20 )
	m += 2;
    else if( m < 50 )
	m += 5;
    else if( m < 100 )
	m += 10;
    else if( m < 200 )
	m += 20;
    else if( m < 500 )
	m += 50;
    else if( m < 1000 )
	m += 100;
    else if( m < 2000 )
	m += 200;
    else if( m < 5000 )
	m += 500;
    else if( m < 10000 )
	m += 1000;
    else if( m < 20000 )
	m += 2000;
    else if( m < 50000 )
	m += 5000;
    else if( m < 100000 )
	m += 10000;
    else if( m < 200000 )
	m += 20000;
    else if( m < 500000 )
	m += 50000;
    else m += 100000;
#endif
  
   }

   fclose(infile);
   fclose(outfile);

   return (0);
}
