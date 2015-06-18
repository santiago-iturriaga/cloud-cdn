
//lru.cc


#include "lru.h"
#include <stdio.h>


//----------------------------------------------------------------------
// Lru::
//   
//----------------------------------------------------------------------
Lru::Lru(unsigned int initWarmup)
{
  //initialize the warmup values
  warmup = initWarmup;

  MAX_SIZE = 100000; //this is the maximum size of the hash table;

  //allocate memory for the hash table
  if ( (hash_table = new LruNode*[MAX_SIZE]) == NULL)
   {
     ErrorMessage("Not enough memory to allocate hash table");
   }
}

//----------------------------------------------------------------------
// Lru::
//   
//----------------------------------------------------------------------
Lru::~Lru()
{
  delete [] hash_table;
}


//----------------------------------------------------------------------
// Lru::initialize
//    This method creates the hash table and initializes the entries to
//    Null. It also initializes all other variables to their respective
//    initial values
//----------------------------------------------------------------------


void 
Lru::initialize(float cacheSize)
{
 cache_size = cacheSize;

 noof_hits = noof_request = total_noof_request = 0;
 cache_freespace = cache_size;
 
 //initialize the hash table
 for (unsigned i=0; i< MAX_SIZE;  i++)
     hash_table[i] =  NULL;
 
 head = tail = NULL;
 byte_requested = byte_hit =0.0;
}


//----------------------------------------------------------------------
// Lru::
//   
//----------------------------------------------------------------------

void
Lru::update_statistics(unsigned size)
{
  ++total_noof_request;
  if (total_noof_request > warmup)
     {
	 ++noof_request;
	 byte_requested = byte_requested + size;
     } 
}

//----------------------------------------------------------------------
// Lru::
//   
//----------------------------------------------------------------------


void
Lru::ErrorMessage(char* s)
{
  printf("\n **** %s ****",s);
  exit(1);
}

//----------------------------------------------------------------------
// Lru::
//   
//----------------------------------------------------------------------


LruNode* 
Lru::in_cache(unsigned int id)
{
 unsigned int index = (id % MAX_SIZE);
 unsigned int found = false;
 LruNode *next, *loc = NULL;
 
 next = hash_table[index];
 while ((next != NULL) && (!found))
   {
    if (next->GetFileId() == id)
    {
     loc=next;
     found = true;
    }
   else
     next = next->GetRight();
   }
 return loc;
}


//----------------------------------------------------------------------
// Lru::
//   
//----------------------------------------------------------------------

void 
Lru::put_in_hash_table(LruNode *anode, unsigned int index)
{
     anode->SetRight(hash_table[index]);
     hash_table[index] = anode;
}

//----------------------------------------------------------------------
// Lru::
//   
//----------------------------------------------------------------------

LruNode* 
Lru::get_new_node(unsigned int id, unsigned int size)
{
 LruNode *anode;

 anode = new LruNode(id, size); 
 if (anode == NULL)
  ErrorMessage("Cannot allocate more memory for new nodes");
 
 //now decrement the remaining cache free space   
 cache_freespace -= size;
 
 //return the newly constructed node
 return anode;
}


//----------------------------------------------------------------------
// Lru::
//   
//----------------------------------------------------------------------

LruNode* 
Lru::getHorzPred(LruNode  *loc, unsigned int index)
{
 LruNode  *start = hash_table[index];
 if (start == loc) 
    start = NULL;
 else
  {
    while (start->GetRight() != loc) 
      start = start->GetRight();
  }
return start;
}

//----------------------------------------------------------------------
// Lru::
//   
//----------------------------------------------------------------------

void 
Lru::remove_docs(unsigned int size)
{
  LruNode *next, *HorzPred;
  unsigned int index,i;
 
  do{
    next = tail;
    tail = tail->GetDown();
    if (tail != NULL)
      tail->SetUp(NULL);

    index = next->GetFileId() % MAX_SIZE;
    
    HorzPred = getHorzPred(next, index);

    if (HorzPred != NULL)
      HorzPred->SetRight(next->GetRight());
    else
      hash_table[index] = next->GetRight();

    if (head == next)  //removing the last node
       head=NULL;

    cache_freespace += next->GetFileSize();
   
    delete next;

  }while((cache_freespace < size)&&(tail != NULL));
}


//----------------------------------------------------------------------
// Lru::
//   
//----------------------------------------------------------------------

void 
Lru::update_cache(LruNode *loc)
{
 
 if (head == NULL)
   {
     head = loc; 
     tail = loc;
   } 
 else 
  { 
    loc->SetDown(NULL);
    loc->SetUp(head);
    head->SetDown(loc);
    head = loc;
  }
}

//----------------------------------------------------------------------
// Lru::
//   
//----------------------------------------------------------------------
	
void 
Lru::put_in_cache_LRU(unsigned int id, unsigned int size)
{
 LruNode *anode;  
 unsigned int index = id % MAX_SIZE;
 if (cache_freespace >= size)  //do we have enough free space to accomodate this file
   {
     anode = get_new_node(id, size);
     put_in_hash_table(anode, index);
     update_cache(anode);
   }
 else if (cache_size >= size)  //since there is not enough space, is the file bigger than the whole cache size
  {
    remove_docs(size);
    anode = get_new_node(id, size);
    put_in_hash_table(anode, index);
    update_cache(anode);
  }
}


//----------------------------------------------------------------------
// Lru::
//   
//----------------------------------------------------------------------
	
void 
Lru::found_update(LruNode *loc)
{
  //get the preceeding and the succeeding nodes
 LruNode *predloc = loc->GetUp();
 LruNode *succloc = loc->GetDown();

 if((loc != head)&&(succloc == NULL)) ErrorMessage("\nright here");

 if (loc != head)     //needs to detach loc since it is not at the head
 {
  //connects the nodes be4 and after loc together
  if (predloc!= NULL)
    predloc->SetDown(succloc);
  if (succloc != NULL)
    succloc->SetUp(predloc);
  if (tail == loc)
      tail = succloc;

  loc->SetDown(NULL);
  loc->SetUp(NULL);
  
  update_cache(loc);
 }
}


//----------------------------------------------------------------------
// Lru::
//   
//----------------------------------------------------------------------
	
float
Lru::compute_hit_ratio()
{
 return (noof_hits *100.0 / noof_request);
}


//----------------------------------------------------------------------
// Lru::
//   
//----------------------------------------------------------------------

float
Lru::compute_byte_hit_ratio()
{
 return (byte_hit *100.0 / byte_requested);
}


//----------------------------------------------------------------------
// Lru::
//   
//----------------------------------------------------------------------

void 
Lru::free_row(LruNode *startptr)
{
 LruNode *next;
 
 do{
   next = startptr->GetRight();
   delete startptr;
   startptr = next;
 }while(startptr != NULL);
}


//----------------------------------------------------------------------
// Lru::
//   
//----------------------------------------------------------------------

void 
Lru::free_all_nodes()
{
  for (unsigned int i=0; i< MAX_SIZE; i++)
   if (hash_table[i] != NULL)
     {
       free_row(hash_table[i]);
     }
}


//----------------------------------------------------------------------
// Lru::
//   
//----------------------------------------------------------------------

bool
Lru::simulate(unsigned fileid, unsigned size)
{
 LruNode *loc;

 //this is another request, so increment the request counter
 ++total_noof_request;

 //check whether it is in the cache
 if (loc = in_cache(fileid))
   {
      if (total_noof_request > warmup)
	 {
	    ++noof_hits;
	    ++noof_request;
	    byte_hit = byte_hit + size;
	    byte_requested = byte_requested + size;
	 }
	 found_update(loc);
	 return true;
    }
 else  //it is not in the cache
  {
       if (total_noof_request > warmup)
	   {
	     ++noof_request;
	     byte_requested = byte_requested + size;
	   }
       put_in_cache_LRU(fileid,size);
       return false;
  }

}

