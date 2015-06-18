// CacheDriver.cc - Driver for webproxy simulator. 
//
// 
// Al Fedoruk
// CMPT 855 Project 
// March 2001
// 
// Modified from version written by Muda
//

#include <stdio.h>
#include <stdlib.h>
#include "lru.h"
#include "lfu.h"
#include "gdsize.h"
#include "rand.h"
#include "fifo.h"

int main(int argc, char* argv[]) {

   unsigned int line;
   float cache_size;
   char command[2000];
   //sprintf(command, "gunzip -dc %s", argv[1]);

   if ( argc == 1 ) {
     fprintf(stderr, "Usage: CacheDriver infile dhrfile bhrfile warm strtcache infcache pol outfile\n");
     exit(1);
   };
    
   FILE *fp;                       // request file pointer
   FILE *fp1 = fopen(argv[2],"w"); // hit rate output file
   FILE *fp2 = fopen(argv[3],"w"); // byte hit rate output file

   FILE *fp3;                      // output file pointer
   FILE *fp4 = fopen("misses.dat", "w");            // output file pointer
   FILE *fp5 = fopen("hit_time_ratio.dat", "w");    // hit time ratio file
   char *outpref = argv[8];

   char outfile[100];
   
   int flag = 0;
   unsigned int warmup = atoi(argv[4]); // 1000000; 
   float startcache    = atoi(argv[5]); 
   float infiniteCache = atoi(argv[6]); // this is infinite cache size
   int policy          = atoi(argv[7]); // cache policy to use
   float timest;                        // time stamp
   float interval = 0.0;
   int runtime = atoi(argv[9]); 
   float increment = (float) ((float) runtime/150);		// hit ratio interval

   Lru *myLru   = new Lru(warmup);
   Lfu *myLfu   = new Lfu(warmup);
   GDSize *myGDS = new GDSize(warmup);
   Rand *myRAND = new Rand(warmup);
   Fifo *myFIFO = new Fifo(warmup);

   unsigned int size, fileid;

   cache_size = startcache * 1024.0 * 1024.0;  
 
   while ( cache_size <= infiniteCache*1024*1024 ) {
      if ( policy==1) myLru->initialize(cache_size);
      if ( policy==2) myLfu->initialize(cache_size);
      if ( policy==3) myGDS->initialize(cache_size);
      if ( policy==4) myRAND->initialize(cache_size);
      if ( policy==5) myFIFO->initialize(cache_size);

      sprintf(outfile, "%s%1.0f.out", outpref, (cache_size/(float)(1024*1024)));

      fp3 = fopen(outfile, "w");
      fp = fopen(argv[1],"r");

      line=0;
  
      while (!feof(fp)) { 

         fscanf(fp,"%f %d %d\n", &timest, &fileid,&size);
         line++;
         if (line % 1000000 == 0)
            printf("Request no :%d\n", line);

         int found = 0;
         if ( policy == 1) found = myLru->simulate(fileid, size);
         if ( policy == 2) found = myLfu->simulate(fileid, size);
         if ( policy == 3) found = myGDS->simulate(fileid, size);
         if ( policy == 4) found = myRAND->simulate(fileid, size);
         if ( policy == 5) found = myFIFO->simulate(fileid, size);
      
         if (found) {
	    // if file was in cache, do nothing
	 } else {
            // if file was not in cache, print it out
            fprintf(fp3,"%d %d\n", fileid, size);
            fprintf(fp4,"%f %d %d\n", timest, fileid, size);
         }
	if (policy ==1) {
		if (runtime > 0) {  
		if (timest > interval) { 
			fprintf(fp5, "%d %f %f\n",(int)  (cache_size/(float)(1024*1024)), timest, myLru->compute_hit_ratio());
			interval+= increment;
		}
		}
	} 
      } //endwhile not eof

      //this part computes the hit ratio
      interval = 0; 
      float dhr; 
      float bhr; 
      if ( policy == 1) {
         dhr = myLru->compute_hit_ratio(); 
         bhr = myLru->compute_byte_hit_ratio();
      };
      if ( policy == 2) {
         dhr = myLfu->compute_hit_ratio(); 
         bhr = myLfu->compute_byte_hit_ratio();
      };
      if ( policy == 3) {
         dhr = myGDS->compute_hit_ratio(); 
         bhr = myGDS->compute_byte_hit_ratio();
      };
      if ( policy == 4) {
         dhr = myRAND->compute_hit_ratio(); 
         bhr = myRAND->compute_byte_hit_ratio();
      };
      if ( policy == 5) {
         dhr = myFIFO->compute_hit_ratio(); 
         bhr = myFIFO->compute_byte_hit_ratio();
      };

      printf("%15.0f  %6.2f  %6.2f\n",cache_size/(float)(1024*1024), dhr, bhr);
      fprintf(fp1,"%15.0f  %6.2f\n",cache_size/(float)(1024*1024),dhr);
      fprintf(fp2,"%15.0f  %6.2f\n",cache_size/(float)(1024*1024),bhr);

      cache_size *= 4;
      fclose(fp);
      fclose(fp3);

   }
   fclose(fp1);
   fclose(fp2);
   fclose(fp4);
   fclose(fp5);
}
