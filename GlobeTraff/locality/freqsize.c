/* Processes a 2-column proxy trace format and prints  */
/* out the docid, frequency, and size info             */

/* Usage: cc -o freqsize freqsize                      */
/*        freqsize < infile > outfile                  */

#include <stdio.h>
#include <stdlib.h>

#define BIGARRAY 300000

int main(argc, argv)
    int argc;
    char *argv[];
  {
    int docid, size, i, j, docs, ref;
    int docids[BIGARRAY];
    int docsizes[BIGARRAY];
    int counts[BIGARRAY];
 
    for( i = 0; i < BIGARRAY; i++ )
      counts[i] = 0;

    docs = 0;
    while (scanf("%d %d", &docid, &size) != EOF)
      {
	if( counts[docid] == 0 )
	  {
	    docids[docid] = docid;
	    docsizes[docid] = size;
	    docs++;
	  }
	counts[docid]++;
      }

    for( i = 0; i < docs; i++ )
      {
		printf("%d\t%d\t%d\t%d\n", docids[i], counts[i], docsizes[i],1);
      }

	return(0); 
  }
