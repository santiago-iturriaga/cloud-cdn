//rand.h

#ifndef RAND_H
#define RAND_H

#include "randnode.h"
#include <stdlib.h>



class Rand {
 
 public:

  Rand(unsigned warmup); //constructor

  ~Rand(); //the destructor

   void initialize(float cacheSize);

   void update_statistics(unsigned size); //this method will increment total request seen so far, byte  and misses

   float compute_hit_ratio();

   float compute_byte_hit_ratio();

   void free_row(RandNode *startptr);

   void free_all_nodes();

   bool simulate(unsigned fileid, unsigned size);

private:

   void ErrorMessage(char* s);

   RandNode* in_cache(unsigned int id);

   void put_in_hash_table(RandNode *anode, unsigned int index);

   void put_in_filelist(RandNode *anode);

   RandNode* get_new_node(unsigned int id, unsigned int size);

   RandNode* getHorzPred(RandNode  *loc, unsigned int index);

   void remove_docs(unsigned int size);

   void update_cache(RandNode *loc);

   void put_in_cache_RANDOM(unsigned int id, unsigned int size);

   void found_update(RandNode *loc);

   
  unsigned int MAX_SIZE;
  unsigned int MAX_SIZE2; //the total noof unique files in the trace;
  unsigned int nelements; //no of elements currently on filelist
  RandNode *head, *tail;
  RandNode** hash_table;
  RandNode** filelist;  //to hold the list of files in the cache
  unsigned total_noof_request;
  unsigned noof_hits, noof_request;
  unsigned int warmup;
  float cache_size, cache_freespace;
  float byte_requested, byte_hit;
  float hit_ratio, byte_hit_ratio;
  unsigned short int seed[3];

};

#endif
