
//gdsize.cc


#include "gdsize.h"
#include <stdio.h>


//----------------------------------------------------------------------
// GDSize::
//   
//----------------------------------------------------------------------
GDSize::GDSize(unsigned int initWarmup)
{
  //initialize the warmup values
  warmup = initWarmup;

  MAX_SIZE = 100000; //this is the maximum size of the hash table

  HEAP_SIZE = 3100000; //you might need to change this value if unique 
                       //docs is more than this value

  //allocate memory for the hash table
  if ( (hash_table = new GDNode*[MAX_SIZE]) == NULL)
   {
     ErrorMessage("Not enough memory to allocate hash table");
   }

  //allocate the heap
  if ( (gdheap = new Heap(HEAP_SIZE)) == NULL)
   {
     ErrorMessage("Not enough memory to allocate the heap");
   }
}

//----------------------------------------------------------------------
// GDSize::
//   
//----------------------------------------------------------------------
GDSize::~GDSize()
{
  delete [] hash_table;
  delete gdheap;
}


//----------------------------------------------------------------------
// GDSize::initialize
//    This method creates the hash table and initializes the entries to
//    Null. It also initializes all other variables to their respective
//    initial values
//----------------------------------------------------------------------


void 
GDSize::initialize(float cacheSize)
{
 cache_size = cacheSize;

 noof_hits = noof_request = total_noof_request = 0;
 cache_freespace = cache_size;
 
 //initialize the inflation value and then the heap
 L = 0;
 gdheap->Init();

 //initialize the hash table
 for (unsigned i=0; i< MAX_SIZE;  i++)
     hash_table[i] =  NULL;

 byte_requested = byte_hit =0.0;
}


//----------------------------------------------------------------------
// GDSize::
//   
//----------------------------------------------------------------------


void
GDSize::ErrorMessage(char* s)
{
  printf("\n **** %s ****",s);
  exit(1);
}



//----------------------------------------------------------------------
// GDSize::
//   
//----------------------------------------------------------------------

void
GDSize::update_statistics(unsigned size)
{
  ++total_noof_request;
  if (total_noof_request > warmup)
     {
	 ++noof_request;
	 byte_requested = byte_requested + size;
     } 
}

//----------------------------------------------------------------------
// GDSize::
//   
//----------------------------------------------------------------------


GDNode* 
GDSize::in_cache(unsigned int id)
{
 unsigned int index = (id % MAX_SIZE);
 unsigned int found = false;
 GDNode *next, *loc = NULL;
 
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
// GDSize::
//   
//----------------------------------------------------------------------

void 
GDSize::put_in_hash_table(GDNode *anode, unsigned int index)
{
     anode->SetRight(hash_table[index]);
     hash_table[index] = anode;
}

//----------------------------------------------------------------------
// GDSize::
//   
//----------------------------------------------------------------------

GDNode* 
GDSize::get_new_node(unsigned int id, unsigned int size)
{
 GDNode *anode;

 anode = new GDNode(id, size); 
 if (anode == NULL)
  ErrorMessage("Cannot allocate more memory for new nodes");
 
 anode->SetHValue(L + 1.0/size);
 
 //now decrement the remaining cache free space   
 cache_freespace -= size;
 
 //return the newly constructed node
 return anode;
}


//----------------------------------------------------------------------
// GDSize::
//   
//----------------------------------------------------------------------

GDNode* 
GDSize::getHorzPred(GDNode  *loc, unsigned int index)
{
 GDNode  *start = hash_table[index];
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
// GDSize::
//   
//----------------------------------------------------------------------

void 
GDSize::remove_docs(unsigned int size)
{
  GDNode *next, *HorzPred;
  unsigned int index,i;
 
  do{
    next = gdheap->FindMin();
    gdheap->DeleteMin();
    
    index = next->GetFileId() % MAX_SIZE;
    
    HorzPred = getHorzPred(next, index);

    if (HorzPred != NULL)
      HorzPred->SetRight(next->GetRight());
    else
      hash_table[index] = next->GetRight();

    cache_freespace += next->GetFileSize();
    
    L = next->GetHValue();

    delete next;

  }while (cache_freespace < size);
}



//----------------------------------------------------------------------
// GDSize::
//   
//----------------------------------------------------------------------
	
void 
GDSize::put_in_cache(unsigned int id, unsigned int size)
{
 GDNode *anode;  
 unsigned int index = id % MAX_SIZE;
 if (cache_freespace >= size)  //do we have enough free space to accomodate this file
   {
     anode = get_new_node(id, size);
     put_in_hash_table(anode, index);
     gdheap->InsertNode(anode);
   }
 else if (cache_size >= size)  //since there is not enough space, is the file bigger than the whole cache size
  {
    remove_docs(size);
    anode = get_new_node(id, size);
    put_in_hash_table(anode, index);
    gdheap->InsertNode(anode);
  }
}


//----------------------------------------------------------------------
//  GDSize::
//   
//----------------------------------------------------------------------
	
void 
GDSize::found_update(GDNode *loc)
{
  gdheap->AlterHeap(loc->GetHeapLoc(), L + 1.0/loc->GetFileSize());
}


//----------------------------------------------------------------------
//  GDSize::
//   
//----------------------------------------------------------------------
	
float
GDSize::compute_hit_ratio()
{
 return (noof_hits *100.0 / noof_request);
}


//----------------------------------------------------------------------
//  GDSize::
//   
//----------------------------------------------------------------------

float
GDSize::compute_byte_hit_ratio()
{
 return (byte_hit *100.0 / byte_requested);
}


//----------------------------------------------------------------------
//  GDSize::
//   
//----------------------------------------------------------------------

void 
GDSize::free_row(GDNode *startptr)
{
 GDNode *next;
 
 do{
   next = startptr->GetRight();
   delete startptr;
   startptr = next;
 }while(startptr != NULL);
}


//----------------------------------------------------------------------
//  GDSize::
//   
//----------------------------------------------------------------------

void 
GDSize::free_all_nodes()
{
  for (unsigned int i=0; i< MAX_SIZE; i++)
   if (hash_table[i] != NULL)
     {
       free_row(hash_table[i]);
     }
}


//----------------------------------------------------------------------
//  GDSize::
//   
//----------------------------------------------------------------------

bool
GDSize::simulate(unsigned fileid, unsigned size)
{
 GDNode *loc;
 bool flag;
 
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
	 flag = true;
    }
 else  //it is not in the cache
  {
       if (total_noof_request > warmup)
	   {
	     ++noof_request;
	     byte_requested = byte_requested + size;
	   }
       put_in_cache(fileid,size);
       flag =  false;
  }

 return flag;

}


