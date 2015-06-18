/* Bucketize the output from a cell delay    */
/* stream and prepare for histogram plot.    */

/* This version reads from stdin and writes to outfile */

/* Usage: cc -o buckets buckets.c                   */
/*        buckets num start end outfile  */
/* where:                                           */
/*        num is the number of buckets desired      */
/*        start is the value for the low bucket     */
/*        end is the value for the high bucket      */

#include <stdio.h>

#define MAX_BUCKETS 1000

/* #define VERBOSE 1 */

main(argc, argv)
int argc;
char *argv[];
  {
    int i, index, counter, cumcount;
    int counthist[MAX_BUCKETS];
    int total;
    float obs, num;
    int numbuckets;
    float startvalue, endvalue, stepsize;
    FILE *fp;

    if( argc < 5 )
      {
	printf("Usage: buckets num start end outfile\n");
	exit(0);
      }

    sscanf(argv[1],"%d",&numbuckets);
    if( (numbuckets > MAX_BUCKETS) || (numbuckets < 1) )
      {
	printf("Sorry, but I can't do that many buckets!!!\n");
	exit(0);
      }

    sscanf(argv[2],"%f",&startvalue);
    sscanf(argv[3],"%f",&endvalue);

    fp = fopen(argv[4], "w");
    if( fp == NULL )
      {
	fprintf(stderr, "Unable to open outfile <%s>!!\n", argv[1]);
	exit(0);
      }
    if( startvalue > endvalue )
      {
	/* switch them, I guess */
	num = startvalue;
	startvalue = endvalue;
	endvalue = num;
      }

    stepsize = (endvalue - startvalue) / numbuckets;

    for( i = 0; i < numbuckets; i++ )
      counthist[i] = 0;

    total = 0;
    counter = 0;
    while( (i = scanf("%f\n", &obs)) == 1 )
      {
	counter += 1;

	if( obs > endvalue )
	  {
#ifdef VERBOSE
	    fprintf(stderr, "Value on line %d is too big to fit! %f > %f\n",
		   counter, obs, endvalue);
#endif VERBOSE
	    counthist[numbuckets-1]++;
	    total++;
	    continue;
	  }

	if( obs < startvalue )
	  {
#ifdef VERBOSE
	    fprintf(stderr, "Value on line %d is too small to fit! %f < %f\n",
		   counter, obs, startvalue);
#endif VERBOSE
	    counthist[0]++;
	    total++;
	    continue;
	  }

	/* figure out bucket number for this observation */
	num = startvalue;
	index = 0;
	while( num < obs )
	  {
	    num += stepsize;
	    if( num < obs )
	      index++;
	  }
	counthist[index]++;
	total++;
      }

    /* Summary info */
#ifdef VERBOSE
    printf("Interval  count  freq    cumfreq    1-cumfreq\n");
#endif VERBOSE
    cumcount = 0;
    for( i = 0; i < numbuckets; i++ )
      {
	int count;
	count = counthist[i];
	cumcount += count;
	fprintf(fp, "%4d   %6.3f   %6d   %8.5f  %8.5f   %8.5f\n",
	       i, startvalue + i * stepsize,
	       count, 
	       (float) 100.0 * count/total,
	       (float) 1.0 * cumcount/total,
	       (float) 1.0 - 1.0 * cumcount/total);
      }
    fclose(fp);
return(0);
  }

