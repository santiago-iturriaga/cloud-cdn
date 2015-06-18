

#include <stdio.h>
#include <stdlib.h>
//#include "lru.h"
#include "lfu.h"
//#include "gdsize.h"

int main(int argc, char* argv[])
{
 
if (argc != 6) 
{
    printf("Usage: %s Reqfile HRfile BHRfile WarmUp InfiniteCacheSize\n", argv[0]);
    exit(1);
}


 unsigned int line;
 float cache_size;

 char command[2000];
 sprintf(command, "gunzip -dc %s", argv[1]);

 FILE *fp; //request file pointer
 FILE *fp1 = fopen(argv[2],"w"); //hit rate output file
 FILE *fp2 = fopen(argv[3],"w"); //byte hit rate output file

 float infiniteCache = atoi(argv[5]); //this is infinite cache size

 unsigned int warmup = atoi(argv[4]); //1000000; //we will use 1m request for warmup


 // Lru *myPol = new Lru(warmup);
 Lfu *myPol = new Lfu(warmup);
 //GDSize *myPol = new GDSize(warmup);

 unsigned int size, fileid;
 
 //starting cache size
 cache_size = 0.5 * 1024.0* 1024.0;  
 
 
 while (cache_size < infiniteCache*1024*1024*1024)
  {
  cache_size *= 2; //actually starts from 64M
  myPol->initialize(cache_size);
  fp = popen(command,"r");
  line=0;
  
  while (!feof(fp))
   { 
     fscanf(fp,"%d%d\n",&fileid,&size);
     line++;
     if (line % 1000000 == 0)
     printf("Request no :%d\n", line);
      
     if (myPol->simulate(fileid, size))
	 {
	   //do nothing
	 }
     else
      {
	//do this
      }
   } //endwhile not eof

  //this part computes the hit ratio
  fprintf(fp1,"%15.0f  %6.2f\n",cache_size/(float)(1024*1024),myPol->compute_hit_ratio());
  fprintf(fp2,"%15.0f  %6.2f\n",cache_size/(float)(1024*1024),myPol->compute_byte_hit_ratio());

  printf("%15.0f  %6.2f  %6.2f\n",cache_size/(float)(1024*1024),myPol->compute_hit_ratio(),myPol->compute_byte_hit_ratio());

 
  pclose(fp);

  }
 fclose(fp1);fclose(fp2);

}






