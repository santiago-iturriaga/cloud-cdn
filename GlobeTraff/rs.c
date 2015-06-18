/**************************************************************/
/* This program calculates the rescaled adjusted range        */
/* statistic (R/S statistic) for a data set. That is, it      */
/* computes R(t(i), n)/S(t(i), n) values for different n's.   */
/*                                                            */
/* The input of this program will be packet counts (a given   */
/* sample of N observations). We subdivide the whole sample   */
/* into K non-overlapping blocks of N/K observations each,    */
/* and compute the rescaled adjusted range statistic          */
/* r(t(i), n)/s(t(i), n) for each of the new block            */
/* "starting points" t(i) with (t(i) - 1) + n <= N, for       */
/* i = 1, 2, ... and (i - 1) * (N / K) + 1 < n < N.           */
/*                                                            */
/* For the given value of N observations, the block size K    */
/* can be specified by the user. Default value for K is 1.    */
/*                                                            */
/* Usage: gcc -o rs rs.c -lm                                  */
/*        rs J [K] < infile > outfile                         */
/*                                                            */
/* J - numebr of line in the input file. Needed for dynamic   */
/* allocation of memory                                       */
/* Written by Ying Chen, July 1994.                           */
/* Modified by Carey Williamson, August 18, 1994.             */
/**************************************************************/ 
  
/* Usual include files */
#include <stdio.h>
#include <stdlib.h>
#include <math.h>

/* Maximum length of time series we can analyze */
// #define MAX_OBSERVATIONS 1010010

/* Debugging flags */
/* #define DEBUG1 1  */
/* #define DEBUG2 1 */
/* #define DEBUG3 1 */
#define OLD_WAY 1
/* #define NEW_WAY 1 */
/* #define NEWEST_WAY 1 */
#define FAST 1

/* Data structures to hold the original time series */
//double X[MAX_OBSERVATIONS];
double *X;

/* Data structures to hold calculated statistics */

double *sumofX;
double *W;
double *W2;
double *R;
double *S;

/*
double sumofX[MAX_OBSERVATIONS];
double W[MAX_OBSERVATIONS];
double W2[MAX_OBSERVATIONS];
double R[MAX_OBSERVATIONS];
double S[MAX_OBSERVATIONS];
*/

int
main(int argc, char *argv[])
  {
    double sum, mean, var, sdev;
    long int N, K;
    long int i, j, k, n, t; 
    double max, min;
    long int imax, imin;
    float current, timest;
    long int blocksize;
    long int lines;
    FILE *ffp; 

    /* Set default number of intervals */
    K = 10;
//    lines = atol(argv[1]); 
    lines = 8000000; 

    /* Check for command line arguments */
    if (argc > 1)
      sscanf(argv[1], "%d", &K);

    if( K <= 0 )
      {
	printf("Illegal number of intervals K: %d\n", K);
	printf("Usage: %s [K] < infile > outfile\n", argv[0]);
	exit(1);
      }

// Allocating memory 
        if ((X = (double *) malloc(lines * sizeof(double ))) == NULL) {
                printf("Cannot allocate memory for X\n");
                exit(-1);
        }
        if ((sumofX = (double *) malloc(lines * sizeof(double ))) == NULL) {
                printf("Cannot allocate memory for sumofX\n");
                exit(-1);
        }
        if ((W = (double *) malloc(lines * sizeof(double ))) == NULL) {
                printf("Cannot allocate memory for W\n");
                exit(-1);
        }
        if ((W2 = (double *) malloc(lines * sizeof(double ))) == NULL) {
                printf("Cannot allocate memory for W2\n");
                exit(-1);
        }
        if ((R = (double *) malloc(lines * sizeof(double ))) == NULL) {
                printf("Cannot allocate memory for R\n");
                exit(-1);
        }
        if ((S = (double *) malloc(lines * sizeof(double ))) == NULL) {
                printf("Cannot allocate memory for S\n");
                exit(-1);
        }


    /* Read in and store the values in the time series. */
    /* Note: the X's are stored starting at index 1,    */
    /* not index 0. This wastes one storage space, but  */
    /* makes it easier to understand the math later.    */
    N = 1;
    while( scanf("%f %f", &timest, &current) != EOF )
      {
	if( N >= lines )
	  {
//	    printf("%d %d\n", N, lines);
	    exit(0);
	  }
	X[N] = current;
	N++;
      }

#ifdef DEBUG1
    printf("Read in %d observations...\n", N);
    printf("X values:\n");
    for( i = 1; i <= N; i++ )
      {
	printf("%f ", X[i]);
	if( i % 5 == 0 )
	  printf("\n");
      }
    printf("\n");
#endif DEBUG1

   /* Compute the mean of the observations */
    sum = 0.0;
    for( i = 1; i <= N; i++ )
      {
	sum += X[i];
      }
    mean = sum / N;

#ifdef DEBUG1
    printf("Mean is %f...\n", mean);
#endif DEBUG1

   /* Compute S, the standard deviation of the observations */
    sum = 0.0;
    for( i = 1; i <= N; i++ )
      {
	sum += (X[i] - mean) * (X[i] - mean);
      }
    var = sum / N;
    sdev = sqrt(var);

#ifdef DEBUG1
    printf("Variance is %f...\n", var);
    printf("Standard deviation is %f...\n", sdev);
#endif DEBUG1   

    /* Compute the Wi's */
    sum = 0.0;
    for( i = 1; i <= N; i++ )
      {
	sum += X[i];
	W[i] = sum - i * mean;
      }
#ifdef DEBUG2
    printf("W values:\n");
    for( i = 1; i <= N; i++ )
      {
	printf("%f ", W[i]);
	if( i % 5 == 0 )
	  printf("\n");
      }
    printf("\n");
    printf("\n");
#endif DEBUG2

    /* Determine size of each block */
    blocksize = N / K;

    /* Compute R/S statistic for each legal value of n  */
    /* from each starting point.                        */
    for( k = 1; k <= K; k++ ) /* number of blocks */
      {
#ifdef DEBUG2
	printf("Working on block %d ...\n", k);
#endif DEBUG2

	/* Determine starting point for block k */
	t = (k - 1) * blocksize + 1;

	/* Leland suggests starting with n around 10 */
	n = 1;
	n = 5;
	n = 10;
	while( n < N - t + 1 )
	  {
	    /* Compute the mean of the first n observations */
	    /* starting from the starting point t.          */
	    sum = 0.0;
	    for( i = 1; i <= n; i++ )
	      {
		sum += X[t+i-1];
	      }
	    mean = sum / n;

#ifdef DEBUG3
	    printf("Mean of first %d observations is %f...\n", n, mean);
#endif DEBUG3

	    /* Compute S, the standard deviation of these observations */
	    sum = 0.0;
	    for( i = 1; i <= n; i++ )
	      {
		sum += (X[t+i-1] - mean) * (X[t+i-1] - mean);
	      }
	    var = sum / n;
	    sdev = sqrt(var);

	    /* save it for later use */
	    S[n] = sdev;
	    
#ifdef DEBUG3
	    printf("  Variance is %f...\n", var);
	    printf("  Standard deviation is %f...\n", sdev);
#endif DEBUG3   

	    /* Compute the Wi's */
	    sum = 0.0;
	    for( i = 1; i <= n; i++ )
	      {
		sum += X[t+i-1];
#ifdef OLD_WAY
		/* use the first i values starting with X[t] */
		/* store result in W[i] */
		W[i] = sum - i * mean;
#endif OLD_WAY
#ifdef NEW_WAY
		/* use the first i values starting with X[t] */
		/* store result in W[t+i-1] */
		/* this is identical to the old way */
		W[t+i-1] = sum - i * mean;
#endif NEW_WAY
#ifdef NEWEST_WAY
		/* replace Wk by W[ti+k] - W[ti]  (Leland93) */
		/* this results in using the k values AFTER X[t]??? */
		W2[i] = W[t+i] - W[t];
#endif NEWEST_WAY
	      }
#ifdef DEBUG3
	    printf("W values:\n");
	    for( i = 0; i < n; i++ )
	      {
#ifdef OLD_WAY
		printf("%f ", W[i]);
#endif OLD_WAY
#ifdef NEW_WAY
		printf("%f ", W[t+i-1]);
#endif NEW_WAY
#ifdef NEWEST_WAY
		printf("%f ", W2[i]);
#endif NEWEST_WAY
		if( i % 5 == 0 )
		  printf("\n");
	      }
	    printf("\n");
#endif DEBUG3

	    /* find largest Wi value */
	    max = 0;
	    imax = -1;
	    for( i = 1; i <= n; i++ )
	      {
#ifdef OLD_WAY
		if( W[i] > max )
		  {
		    max = W[i];
		    imax = i;
		  }
#endif OLD_WAY
#ifdef NEW_WAY
		if( W[t+i-1] > max )
		  {
		    max = W[t+i-1];
		    imax = t+i-1;
		  }
#endif NEW_WAY
#ifdef NEWEST_WAY
		if( W2[i] > max )
		  {
		    max = W2[i];
		    imax = i;
		  }
#endif NEWEST_WAY
	      }
#ifdef DEBUG2
	    printf("  Largest W[i] is %f for i = %d\n", max, imax);
#endif DEBUG2

	    /* find smallest Wi value */
	    min = 0;
	    imin = -1;
	    for( i = 1; i <= n; i++ )
	      {
#ifdef OLD_WAY
		if( W[i] < min )
		  {
		    min = W[i];
		    imin = i;
		  }
#endif OLD_WAY
#ifdef NEW_WAY
		if( W[t+i-1] < min )
		  {
		    min = W[t+i-1];
		    imin = t+i-1;
		  }
#endif NEW_WAY
#ifdef NEWEST_WAY
		if( W2[i] < min )
		  {
		    min = W2[i];
		    imin = i;
		  }
#endif NEWEST_WAY
	      }
#ifdef DEBUG2
	    printf("  Smallest W[i] is %f for i = %d\n", min, imin);
#endif DEBUG2
	
	    /* Compute R and store it */
	    R[n] = max - min;

#ifdef DEBUG2
	    printf("  R is %f, S is %f for k = %d, n = %d\n",
		   R[n], S[n], k, n);
#endif DEBUG2

	    /* print out R/S statistic */
	    if( S[n] > 0.0 )
	      printf("%d %f\n", n, R[n]/S[n]);

	    /* get next value of n */
#ifdef FAST
	    /* go by bigger steps to get visual approximation */
	    /* this speeds up execution by a factor of 100 or more */
	    if( n < 10 )
	      n += 1;
	    else if( n < 20 )
	      n += 2;
	    else if( n < 50 )
	      n += 5;
	    else if( n < 100 )
	      n += 10;
	    else if( n < 200 )
	      n += 20;
	    else if( n < 500 )
	      n += 50;
	    else if( n < 1000 )
	      n += 100;
	    else if( n < 2000 )
	      n += 200;
	    else if( n < 5000 )
	      n += 500;
	    else if( n < 10000 )
	      n += 1000;
	    else if( n < 20000 )
	      n += 2000;
	    else if( n < 50000 )
	      n += 5000;
	    else if( n < 100000 )
	      n += 10000;
	    else if( n < 200000 )
	      n += 20000;
	    else if( n < 500000 )
	      n += 50000;
	    else n += 100000;
#else
	    n = n + 1;
#endif FAST
	  } /* end while n */
      } /* end for k */
	free(X); 
	free(sumofX); 
	free(W); 
	free(W2); 
	free(R); 
	free(S); 

	return(0);
  }

