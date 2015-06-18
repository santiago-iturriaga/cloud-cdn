/* Calculates requests per time interval for traces in the format: */
/*   timestamp request                                             */

/* Usage: gcc -o reqsperinterval reqsperinterval.c                 */
/*        reqsperinterval [interval size] < infile                 */

/* interval: the width of time interval desired                    */
/* infile: input file, in two-column format:  timestamp request    */

#include <stdio.h>
#include <stdlib.h>

#define INTERVAL_SIZE 60.0  /* default interval value is 60.0 seconds */

int main(argc, argv)
    int argc;
    char *argv[];
  {
    float interval_size, interval_end, current_time;
    int reqs_this_interval, request;
 
    if (argc < 2)
      interval_size = INTERVAL_SIZE;
    else sscanf(argv[1], "%f", &interval_size);

    reqs_this_interval = 0;

    /* start from time 0 */
    interval_end = interval_size;

    while (scanf("%f %d", &current_time, &request) != EOF)
      {
	while (current_time >= interval_end)
	  {
	    printf("%8.6f %d\n", interval_end, reqs_this_interval);

	    reqs_this_interval = 0;
	   
	    interval_end += interval_size;
	  }

	reqs_this_interval++;
      }
    /* print one last report at the end, if needed */
    if( reqs_this_interval > 0 )
      printf("%8.6f %d\n", current_time, reqs_this_interval);
    return(0);
  }

