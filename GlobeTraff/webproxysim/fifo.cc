
//fifo.cc


#include "fifo.h"
#include <stdio.h>


//----------------------------------------------------------------------
// Fifo::
//   
//----------------------------------------------------------------------
Fifo::Fifo(unsigned int initWarmup)
{
  //initialize the warmup values
  warmup = initWarmup;

  MAX_SIZE = 100000; //this is the maximum size of the hash table;

  //allocate memory for the hash table
  if ( (hash_table = new FifoNode*[MAX_SIZE]) == NULL)
   {
     ErrorMessage("Not enough memory to allocate hash table");
   }
}

//----------------------------------------------------------------------
// Fifo::
//   
//----------------------------------------------------------------------
Fifo::~Fifo()
{
  delete [] hash_table;
}


//----------------------------------------------------------------------
// Fifo::initialize
//    This method creates the hash table and initializes the entries to
//    Null. It also initializes all other variables to their respective
//    initial values
//----------------------------------------------------------------------


void 
Fifo::initialize(float cacheSize)
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
// Fifo::
//   
//----------------------------------------------------------------------

void
Fifo::update_statistics(unsigned size)
{
  ++total_noof_request;
  if (total_noof_request > warmup)
     {
	 ++noof_request;
	 byte_requested = byte_requested + size;
     } 
}

//----------------------------------------------------------------------
// Fifo::
//   
//----------------------------------------------------------------------


void
Fifo::ErrorMessage(char* s)
{
  printf("\n **** %s ****",s);
  exit(1);
}

//----------------------------------------------------------------------
// Fifo::
//   
//----------------------------------------------------------------------


FifoNode* 
Fifo::in_cache(unsigned int id)
{
 unsigned int index = (id % MAX_SIZE);
 unsigned int found = false;
 FifoNode *next, *loc = NULL;
 
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
// Fifo::
//   
//----------------------------------------------------------------------

void 
Fifo::put_in_hash_table(FifoNode *anode, unsigned int index)
{
     anode->SetRight(hash_table[index]);
     hash_table[index] = anode;
}

//----------------------------------------------------------------------
// Fifo::
//   
//----------------------------------------------------------------------

FifoNode* 
Fifo::get_new_node(unsigned int id, unsigned int size)
{
 FifoNode *anode;

 anode = new FifoNode(id, size); 
 if (anode == NULL)
  ErrorMessage("Cannot allocate more memory for new nodes");
 
 //now decrement the remaining cache free space   
 cache_freespace -= size;
 
 //return the newly constructed node
 return anode;
}


//----------------------------------------------------------------------
// Fifo::
//   
//----------------------------------------------------------------------

FifoNode* 
Fifo::getHorzPred(FifoNode  *loc, unsigned int index)
{
 FifoNode  *start = hash_table[index];
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
// Fifo::
//   
//----------------------------------------------------------------------

void 
Fifo::remove_docs(unsigned int size)
{
  FifoNode *next, *HorzPred;
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
// Fifo::
//   
//----------------------------------------------------------------------

void 
Fifo::update_cache(FifoNode *loc)
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
// Fifo::
//   
//----------------------------------------------------------------------
	
void 
Fifo::put_in_cache_FIFO(unsigned int id, unsigned int size)
{
 FifoNode *anode;  
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
// Fifo::
//   
//----------------------------------------------------------------------
	
float
Fifo::compute_hit_ratio()
{
 return (noof_hits *100.0 / noof_request);
}


//----------------------------------------------------------------------
// Fifo::
//   
//----------------------------------------------------------------------

float
Fifo::compute_byte_hit_ratio()
{
 return (byte_hit *100.0 / byte_requested);
}


//----------------------------------------------------------------------
// Fifo::
//   
//----------------------------------------------------------------------

void 
Fifo::free_row(FifoNode *startptr)
{
 FifoNode *next;
 
 do{
   next = startptr->GetRight();
   delete startptr;
   startptr = next;
 }while(startptr != NULL);
}


//----------------------------------------------------------------------
// Fifo::
//   
//----------------------------------------------------------------------

void 
Fifo::free_all_nodes()
{
  for (unsigned int i=0; i< MAX_SIZE; i++)
   if (hash_table[i] != NULL)
     {
       free_row(hash_table[i]);
     }
}


//----------------------------------------------------------------------
// Fifo::
//   
//----------------------------------------------------------------------

bool
Fifo::simulate(unsigned fileid, unsigned size)
{
 FifoNode *loc;

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
	 return true;
    }
 else  //it is not in the cache
  {
       if (total_noof_request > warmup)
	   {
	     ++noof_request;
	     byte_requested = byte_requested + size;
	   }
       put_in_cache_FIFO(fileid,size);
       return false;
  }

}

