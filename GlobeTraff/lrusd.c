/*
 * Program to process a reference stream and simulate an LRU
 * cache and determine LRU stack reference frequencies.
 *
 * Usage: lrusd < datafile
 *
 * Carey Williamson, Nov 2001
 */

#include <stdio.h>
#define MAX_DOCS 10000000

/* #define DEBUG 1 */

unsigned lruFreq[MAX_DOCS];

typedef struct _DRR
  {
    long int docid;
    unsigned references;
    struct _DRR *prev;
    struct _DRR *next;
  } DocReferenceRecord;

DocReferenceRecord docReferenceRecords[MAX_DOCS];
DocReferenceRecord *docReferenceList;

DocReferenceRecord *docPresent[MAX_DOCS];

int FindPlaceOnList(doc)
    long int doc;
  {
    /* Find the referenced doc on the docReferenceList, counting as you
     * search so you know its position in the LRU stack. 1,2,...N
     * Return 0 if not there.
     */
    DocReferenceRecord *prec;
    int place = 1;
    for( prec = docReferenceList; prec != NULL; prec = prec->next, place++ )
      {
	if( prec->docid == doc )
	    return( place );
      }
    printf("Failed to find doc %d in docReferenceList\n", doc);
    return( 0 );
  }

main()
  {
    long  i, index;
    long int count; 
    long int docid, docsize;
    long int refNum, numDocs;
    float timest; 
    FILE *fp; 
    DocReferenceRecord *drr;

    /* Initialize */
    for(i = 0; i < MAX_DOCS; i++ )
      {
	docPresent[i] = NULL;
	lruFreq[i] = 0;
      }

    fp = fopen("results/lrusdts.dat", "w");

    refNum = 0;
    numDocs = 0;
    docReferenceList = NULL;

    while( scanf("%f %d %d\n", &timest, &docid, &docsize) > 0  )
      {
	/* Process each item in the reference string. */
	refNum++;
	if( docPresent[docid] == NULL )
	  {
	    /* Allocate a docReferenceRecord */
	    drr = &docReferenceRecords[numDocs];

	    /* Initialize docReferenceRecord */
	    drr->docid = docid;
	    drr->references = 0;

	    /* Put on front of docReference list */
	    drr->next = docReferenceList;
	    drr->prev = NULL;
	    if( docReferenceList )
		drr->next->prev = drr;
	    docReferenceList = drr;

	    /* Safety check */
#ifdef DEBUG
	    printf("Just added a record for doc %d\n", docid);
#endif
//	    fprintf(fp, "%f %d\n", timest, numDocs);
	    numDocs++;
	    if( docid >= MAX_DOCS )
	      {
		printf("Maximum number of docs reached, reference %d\n",
		    refNum);
		exit( 0 );
	      }
	    docPresent[docid] = drr;
	  }
	else /* already seen */
	  {
#ifdef DEBUG
	    printf("Looking for doc %d in LRU stack...", docid);
#endif
	    index = FindPlaceOnList(docid);
//#ifdef DEBUG
//	    fprintf(fp, "Answer: %d %f\n", index, timest);
//#endif
	    fprintf(fp, "%f %d\n", timest, index);
	    lruFreq[index]++;
	    
	    /* Move from current spot on list to front (if not already) */
	    drr = docPresent[docid];
	    if( drr->prev )
	      {
		drr->prev->next = drr->next;
		if( drr->next )
		    drr->next->prev = drr->prev;
		drr->next = docReferenceList;
		drr->next->prev = drr;
		drr->prev = NULL;	/* mark as front of list */
		docReferenceList = drr;
#ifdef DEBUG
		printf("Just moved doc %d to front\n", docid);
#endif
	      }
	  }

	/* Update number of references */
	drr->references++;
      }
/*
    printf("Total references: %d\n", refNum);
    printf("Number of docs seen: %d\n", numDocs);

    printf("\n");
    printf("LRU Stack Frequencies\n");
    printf("Level  Count Freq(%%)\n");
*/
//	printf("start\n");
    for( i = 0; i < numDocs; i++ )
      {
	printf("%8.6f %3d\n", 
		((float) lruFreq[i] / (refNum - numDocs)), i);
//	printf("%8.6f\n", ((float) lruFreq[i] / (refNum - numDocs)));
      }
//  printf("\n");
	return(0); 

  }


