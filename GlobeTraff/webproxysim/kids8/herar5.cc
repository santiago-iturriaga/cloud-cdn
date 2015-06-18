
//This simulator is for a 2-level hierarchy where the policies employed at each level is
//  Parent   : LFU
//  Children : LFU

//The dispatch mechanism is complete sharing of all requests


#include <stdio.h>
#include <stdlib.h>
#include "lru.h"
#include "lfu.h"
#include "gdsize.h"



int main(int argc, char* argv[])
{


  if (argc != 11)
   {
   printf("Usage is : \n%s P-hr-file P-bhr-file Req-file C1-hr-file C1-bhr-file C2-hr-file C2-bhr-file Warmup Infinite-cache\n", argv[0]);

   printf("\n P stands for parent, C stands for child\n");
   exit(1);
 }
  printf("\n Complete overlap scenario with 8 child caches\n");

 unsigned int line1, line2;
 float cache_size;

 //result of hit rate and byte hit rate for the parent cache
 FILE *Phrfp = fopen(argv[1], "w");
 FILE *Pbhrfp = fopen(argv[2], "w");
 

 //initialize request file which is a zipped file
 char command[2000];
 sprintf(command, "gunzip -dc %s", argv[3]); 

 //initialize file pointers for output files for child 1 
 FILE *C1hrfp = fopen(argv[4],"w"); //hit rate output file for child 1
 FILE *C1bhrfp = fopen(argv[5],"w"); //byte hit rate output file for child 1
 
 //now for child 2
 FILE *C2hrfp = fopen(argv[6],"w"); //hit rate output file for child 2
 FILE *C2bhrfp = fopen(argv[7],"w"); //byte hit rate output file for child 2

 unsigned int warmup = atoi(argv[8]); //now get the warmup
 float infiniteCache = atoi(argv[9]); //this is infinite cache size

 FILE *out = fopen(argv[10],"w"); //  

 //now create the cache objects for the parent & the children
 //in this case the parent is using the lru policy while the children 
 //are using the lru policy

 //determine which policy to use for the parent 
 Lfu *parent = new Lfu(warmup);
  
 //determine which policy to use for the children
 Lfu *child1 = new Lfu(warmup);
 Lfu *child2 = new Lfu(warmup);
 Lfu *child3 = new Lfu(warmup);
 Lfu *child4 = new Lfu(warmup);
 Lfu *child5 = new Lfu(warmup);
 Lfu *child6 = new Lfu(warmup);
 Lfu *child7 = new Lfu(warmup);
 Lfu *child8 = new Lfu(warmup);

 unsigned int size, fileid;
 
 //starting cache size
 cache_size = 2.0 * 1024.0 * 1024.0 ;  
 unsigned short seed[] = {1, 5, 8}; //we will dispatch requests randomly
 double randnum;
 while (cache_size < infiniteCache*1024*1024)
  {
  cache_size *= 2; //actually starts from 1M
  parent->initialize(cache_size);
  child1->initialize(cache_size);
  child2->initialize(cache_size);
  child3->initialize(cache_size);
  child4->initialize(cache_size);
  child5->initialize(cache_size);
  child6->initialize(cache_size);
  child7->initialize(cache_size);
  child8->initialize(cache_size);

  FILE *Reqfp = popen(command,"r");
   
  line1 = line2 = 0;
  
  //This loop simulates a completely shared request stream where any requests
  //could go to any of the child proxies depending on the generated random no

  while (!feof(Reqfp))
   { 
     //read the next request
     fscanf(Reqfp,"%d%d\n",&fileid,&size);

     randnum = erand48(seed);
     if (randnum <=0.125)
	{
     		//line1++;
     		//if ((line1 % 1000000) == 0)
		//  fprintf(out,"Request Count For Child 1 :%d\n", line1);
      
     		if (!child1->simulate(fileid, size))
	 	 {
fprintf(out,"%d %d\n",fileid,size);
	           parent->simulate(fileid, size);
	 	 }
	}
     else if (randnum <=0.25) //give it to child 2
      {	
		//line2++;
     		//if ((line2 % 1000000) == 0)
		//  printf("Request Count For Child 2 :%d\n", line2);
      
     		if (!child2->simulate(fileid, size))
	 	 {
fprintf(out,"%d %d\n",fileid,size);
	           parent->simulate(fileid, size);
	 	 }
       }
     else if (randnum <=0.375) //give it to child 3
      {	
		//line2++;
     		//if ((line2 % 1000000) == 0)
		//  printf("Request Count For Child 2 :%d\n", line2);
      
     		if (!child3->simulate(fileid, size))
	 	 {
fprintf(out,"%d %d\n",fileid,size);
	           parent->simulate(fileid, size);
	 	 }
       }
     else if (randnum <=0.5) //give it to child 4
      {	
		//line2++;
     		//if ((line2 % 1000000) == 0)
		//  printf("Request Count For Child 2 :%d\n", line2);
      
     		if (!child4->simulate(fileid, size))
	 	 {
fprintf(out,"%d %d\n",fileid,size);
	           parent->simulate(fileid, size);
	 	 }
       }
     else if (randnum <=0.625) //give it to child 5
      {	
		//line2++;
     		//if ((line2 % 1000000) == 0)
		//  printf("Request Count For Child 2 :%d\n", line2);
      
     		if (!child5->simulate(fileid, size))
	 	 {
fprintf(out,"%d %d\n",fileid,size);
	           parent->simulate(fileid, size);
	 	 }
       }
     else if (randnum <=0.75) //give it to child 6
      {	
		//line2++;
     		//if ((line2 % 1000000) == 0)
		//  printf("Request Count For Child 2 :%d\n", line2);
      
     		if (!child6->simulate(fileid, size))
	 	 {
fprintf(out,"%d %d\n",fileid,size);
	           parent->simulate(fileid, size);
	 	 }
       }
     else if (randnum <=0.875) //give it to child 7
      {	
		//line2++;
     		//if ((line2 % 1000000) == 0)
		//  printf("Request Count For Child 2 :%d\n", line2);
      
     		if (!child7->simulate(fileid, size))
	 	 {
fprintf(out,"%d %d\n",fileid,size);
	           parent->simulate(fileid, size);
	 	 }
       }
     else if (randnum <=1.0) //give it to child 8
      {	
		//line2++;
     		//if ((line2 % 1000000) == 0)
		//  printf("Request Count For Child 2 :%d\n", line2);
      
     		if (!child8->simulate(fileid, size))
	 	 {
fprintf(out,"%d %d\n",fileid,size);
	           parent->simulate(fileid, size);
	 	 }
       }
   } //endwhile not eof

  //when all request have been dispatched, compute hit ratio and byte hit ratio for the 
  //the children and the parent
  fprintf(Phrfp,"%15.0f  %6.2f\n",cache_size/(float)(1024*1024),parent->compute_hit_ratio());
  fprintf(Pbhrfp,"%15.0f  %6.2f\n",cache_size/(float)(1024*1024),parent->compute_byte_hit_ratio());
  fprintf(C1hrfp,"%15.0f  %6.2f\n",cache_size/(float)(1024*1024),child1->compute_hit_ratio());
  fprintf(C1bhrfp,"%15.0f  %6.2f\n",cache_size/(float)(1024*1024),child1->compute_byte_hit_ratio());
  fprintf(C2hrfp,"%15.0f  %6.2f\n",cache_size/(float)(1024*1024),child2->compute_hit_ratio());
  fprintf(C2bhrfp,"%15.0f  %6.2f\n",cache_size/(float)(1024*1024),child2->compute_byte_hit_ratio());
 
  //Let's see the current result printed on the screen
 // printf("%15.0f  %6.2f  %6.2f  %6.2f  %6.2f  %6.2f  %6.2f\n",cache_size/(float)(1024*1024),
  		//parent->compute_hit_ratio(),parent->compute_byte_hit_ratio(),
		//child1->compute_hit_ratio(),child1->compute_byte_hit_ratio(),
		//child2->compute_hit_ratio(),child2->compute_byte_hit_ratio());

 //now rewind to the beginning of the file for the next round with another cache size
 // fseek(Reqfp, 0, 0);
 pclose(Reqfp); 
}

//all cache sizes have been simulated, so close the output files for all
fclose(Phrfp); fclose(Pbhrfp);
fclose(C1hrfp); fclose(C1bhrfp);
fclose(C2hrfp); fclose(C2bhrfp);
fclose(out);

}
