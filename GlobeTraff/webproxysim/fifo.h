//fifo.h

#ifndef FIFO_H
#define FIFO_H

#include "fifonode.h"
#include <stdlib.h>



class Fifo {
 
 public:

  Fifo(unsigned warmup); //constructor

  ~Fifo(); //the destructor

   void initialize(float cacheSize);

   void update_statistics(unsigned size); //this method will increment total request seen so far, byte  and misses

   float compute_hit_ratio();

   float compute_byte_hit_ratio();

   void free_row(FifoNode *startptr);

   void free_all_nodes();

   bool simulate(unsigned fileid, unsigned size);

private:

   void ErrorMessage(char* s);

   FifoNode* in_cache(unsigned int id);

   void put_in_hash_table(FifoNode *anode, unsigned int index);

   FifoNode* get_new_node(unsigned int id, unsigned int size);

   FifoNode* getHorzPred(FifoNode  *loc, unsigned int index);

   void remove_docs(unsigned int size);

   void update_cache(FifoNode *loc);

   void put_in_cache_FIFO(unsigned int id, unsigned int size);

   
  unsigned int MAX_SIZE;
  FifoNode *head, *tail;
  FifoNode** hash_table;
  unsigned total_noof_request;
  unsigned noof_hits, noof_request;
  unsigned int warmup;
  float cache_size, cache_freespace;
  float byte_requested, byte_hit;
  float hit_ratio, byte_hit_ratio;


};

#endif
