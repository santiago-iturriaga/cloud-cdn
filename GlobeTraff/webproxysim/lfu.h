//lfu.h

#ifndef LFU_H
#define LFU_H

#include "lfunode.h"
#include <stdlib.h>


class Lfu {
 
 public:

  Lfu(unsigned warmup); //constructor

  ~Lfu(); //the destructor

  void initialize(float cacheSize);

  void ErrorMessage(char* s);

  LfuNode* in_cache(unsigned int id);

  void put_in_hash_table(LfuNode *anode, unsigned int index);

  LfuNode* get_new_node(unsigned int id, unsigned int size);

  LfuNode* getHorzPred(LfuNode  *loc, unsigned int index);

  void half_all_freqs();

  void update_statistics(unsigned size);

  void remove_docs(unsigned int size);

  void update_cache(LfuNode *loc);

  void put_in_cache(unsigned int id, unsigned int size);

  void found_update(LfuNode *loc);

  float compute_hit_ratio();

  float compute_byte_hit_ratio();

  void free_row(LfuNode *startptr);

  void free_all_nodes();

  bool simulate(unsigned fileid, unsigned size);

  LfuNode* get_where_to_insert(LfuNode *loc);

 
private:

  unsigned int MAX_SIZE;
  unsigned FREQ_TABLE_SIZE; 
  LfuNode *head, *tail;
  LfuNode** hash_table;
  LfuNode** freqtable;
  unsigned total_noof_request;
  unsigned noof_hits, noof_request;
  unsigned int warmup;
  unsigned docs_count;
  unsigned sumof_freqs;
  unsigned average_age;
  float cache_size, cache_freespace;
  float byte_requested, byte_hit;
  float hit_ratio, byte_hit_ratio;

};

#endif
