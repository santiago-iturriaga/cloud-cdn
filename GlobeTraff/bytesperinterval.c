/* Calculates bytes per time interval for traces in the format: */
/*   timestamp bytes                                            */

/* Usage: gcc -o bytesperinterval bytesperinterval.c                 */
/*        bytesperinterval [interval size] < infile                 */

/* interval: the width of time interval desired                    */
/* infile: input file, in two-column format:  timestamp bytes    */

#include <stdio.h>
#include <stdlib.h>

#define INTERVAL_SIZE 60.0  /* default interval value is 60.0 seconds */

int main(argc, argv)
    int argc;
    char *argv[];
  {
    float interval_size, interval_end, current_time;
    int bytes_this_interval, request;
 
    if (argc < 2)
      interval_size = INTERVAL_SIZE;
    else sscanf(argv[1], "%f", &interval_size);

    bytes_this_interval = 0;

    /* start from time 0 */
    interval_end = interval_size;

    while (scanf("%f %d", &current_time, &request) != EOF)
      {
	while (current_time >= interval_end)
	  {
	    printf("%8.6f %d\n", interval_end, bytes_this_interval);

	    bytes_this_interval = 0;
	   
	    interval_end += interval_size;
	  }

	bytes_this_interval += request;
      }
    /* print one last report at the end, if needed */
    if( bytes_this_interval > 0 )
      printf("%8.6f %d\n", current_time, bytes_this_interval);
    return(0); 
  }

