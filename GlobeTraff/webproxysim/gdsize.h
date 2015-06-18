//gdsize.h

#ifndef GDSIZE_H
#define GDSIZE_H

#include <stdlib.h>
#include "heap.h"

//This class models the GD size policy


class GDSize {
 
 public:

  GDSize(unsigned warmup); //constructor

  ~GDSize(); //the destructor

  void initialize(float cacheSize);

  void ErrorMessage(char* s);

  void update_statistics(unsigned size); 

  GDNode* in_cache(unsigned int id);

  void put_in_hash_table(GDNode *anode, unsigned int index);

  GDNode* get_new_node(unsigned int id, unsigned int size);

  GDNode* getHorzPred(GDNode  *loc, unsigned int index);

  void remove_docs(unsigned int size);

  void put_in_cache(unsigned int id, unsigned int size);

  void found_update(GDNode *loc);

  float compute_hit_ratio();

  float compute_byte_hit_ratio();

  void free_row(GDNode *startptr);

  void free_all_nodes();

  bool simulate(unsigned fileid, unsigned size);

 
private:

  unsigned int MAX_SIZE;
  unsigned int HEAP_SIZE;
  GDNode** hash_table;
  unsigned total_noof_request;
  unsigned noof_hits, noof_request;
  unsigned int warmup;
  float cache_size, cache_freespace;
  float byte_requested, byte_hit;
  float hit_ratio, byte_hit_ratio;
  
  float L; //the inflation value
  Heap *gdheap;
};

#endif
