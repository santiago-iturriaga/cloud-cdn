//lru.h

#ifndef LRU_H
#define LRU_H

#include "lrunode.h"
#include <stdlib.h>



class Lru {
 
 public:

  Lru(unsigned warmup); //constructor

  ~Lru(); //the destructor

   void initialize(float cacheSize);

   void update_statistics(unsigned size); //this method will increment total request seen so far, byte  and misses

   float compute_hit_ratio();

   float compute_byte_hit_ratio();

   void free_row(LruNode *startptr);

   void free_all_nodes();

   bool simulate(unsigned fileid, unsigned size);

private:

   void ErrorMessage(char* s);

   LruNode* in_cache(unsigned int id);

   void put_in_hash_table(LruNode *anode, unsigned int index);

   LruNode* get_new_node(unsigned int id, unsigned int size);

   LruNode* getHorzPred(LruNode  *loc, unsigned int index);

   void remove_docs(unsigned int size);

   void update_cache(LruNode *loc);

   void put_in_cache_LRU(unsigned int id, unsigned int size);

   void found_update(LruNode *loc);

   
  unsigned int MAX_SIZE;
  LruNode *head, *tail;
  LruNode** hash_table;
  unsigned total_noof_request;
  unsigned noof_hits, noof_request;
  unsigned int warmup;
  float cache_size, cache_freespace;
  float byte_requested, byte_hit;
  float hit_ratio, byte_hit_ratio;


};

#endif
