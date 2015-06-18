
//rand.cc


#include "rand.h"
#include <stdio.h>


//----------------------------------------------------------------------
// Rand::
//   
//----------------------------------------------------------------------
Rand::Rand(unsigned int initWarmup)
{
  for (unsigned int i=0; i<3; i++)
      seed[i] = i+2;

  //initialize the warmup values
  warmup = initWarmup;

  nelements = 0;
  MAX_SIZE = 100000; //this is the maximum size of the hash table;
  MAX_SIZE2 = 1500000;
  //allocate memory for the hash table
  if ( (hash_table = new RandNode*[MAX_SIZE]) == NULL)
   {
     ErrorMessage("Not enough memory to allocate hash table");
   }
 
  if ( (filelist = new RandNode*[MAX_SIZE2]) == NULL)
   {
     ErrorMessage("Not enough memory to allocate file list");
   }
  
}

//----------------------------------------------------------------------
// Rand::
//   
//----------------------------------------------------------------------
Rand::~Rand()
{
  delete [] hash_table;
  delete [] filelist;
}


//----------------------------------------------------------------------
// Rand::initialize
//    This method creates the hash table and initializes the entries to
//    Null. It also initializes all other variables to their respective
//    initial values
//----------------------------------------------------------------------


void 
Rand::initialize(float cacheSize)
{
 cache_size = cacheSize;

 noof_hits = noof_request = total_noof_request = 0;
 cache_freespace = cache_size;
 
 //initialize the hash table
 for (unsigned i=0; i< MAX_SIZE;  i++)
     hash_table[i] =  NULL;
 
 for (unsigned i=0; i< MAX_SIZE2;  i++)
     filelist[i] =  NULL;
 nelements = 0;

 head = tail = NULL;
 byte_requested = byte_hit =0.0;
}


//----------------------------------------------------------------------
// Rand::
//   
//----------------------------------------------------------------------

void
Rand::update_statistics(unsigned size)
{
  ++total_noof_request;
  if (total_noof_request > warmup)
     {
	 ++noof_request;
	 byte_requested = byte_requested + size;
     } 
}

//----------------------------------------------------------------------
// Rand::
//   
//----------------------------------------------------------------------


void
Rand::ErrorMessage(char* s)
{
  printf("\n **** %s ****",s);
  exit(1);
}

//----------------------------------------------------------------------
// Rand::
//   
//----------------------------------------------------------------------


RandNode* 
Rand::in_cache(unsigned int id)
{
 unsigned int index = (id % MAX_SIZE);
 unsigned int found = false;
 RandNode *next, *loc = NULL;
 
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
// Rand::
//   
//----------------------------------------------------------------------

void 
Rand::put_in_hash_table(RandNode *anode, unsigned int index)
{
     anode->SetRight(hash_table[index]);
     hash_table[index] = anode;
}


//----------------------------------------------------------------------
// Rand::
//   
//----------------------------------------------------------------------

void 
Rand::put_in_filelist(RandNode *anode)
{
    filelist[nelements] = anode;
    nelements++;
}

//----------------------------------------------------------------------
// Rand::
//   
//----------------------------------------------------------------------

RandNode* 
Rand::get_new_node(unsigned int id, unsigned int size)
{
 RandNode *anode;

 anode = new RandNode(id, size); 
 if (anode == NULL)
  ErrorMessage("Cannot allocate more memory for new nodes");
 
 //now decrement the remaining cache free space   
 cache_freespace -= size;
 
 //return the newly constructed node
 return anode;
}


//----------------------------------------------------------------------
// Rand::
//   
//----------------------------------------------------------------------

RandNode* 
Rand::getHorzPred(RandNode  *loc, unsigned int index)
{
 RandNode  *start = hash_table[index];
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
// Rand::
//   
//----------------------------------------------------------------------

void 
Rand::remove_docs(unsigned int size)
{
  RandNode *next, *HorzPred;
  unsigned int index,i;
 
  do{
    unsigned int randno = (unsigned int)(nelements * erand48(seed));
    next = filelist[randno];
    if (next == NULL) 
	ErrorMessage("This is definately a bug");

    //now move the last file to this position and decrement nelements on filelist
    filelist[randno] = filelist[nelements-1];
    nelements--;
    
    if (tail == next) { 
    tail = tail->GetDown();
    if (tail != NULL)
      tail->SetUp(NULL);
    }
  
    if (head == next) { 
	head = head->GetUp();
	if (head != NULL)
	    head->SetDown(NULL);
    }

    index = next->GetFileId() % MAX_SIZE;
    
    HorzPred = getHorzPred(next, index);

    if (HorzPred != NULL)
      HorzPred->SetRight(next->GetRight());
    else
      hash_table[index] = next->GetRight();

    cache_freespace += next->GetFileSize();
   
//    delete next;

  }while(cache_freespace < size);
  
}


//----------------------------------------------------------------------
// Rand::
//   
//----------------------------------------------------------------------

void 
Rand::update_cache(RandNode *loc)
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
// Rand::
//   
//----------------------------------------------------------------------
	
void 
Rand::put_in_cache_RANDOM(unsigned int id, unsigned int size)
{
 RandNode *anode;  
 unsigned int index = id % MAX_SIZE;
 if (cache_freespace >= size)  //do we have enough free space to accomodate this file
   {
     anode = get_new_node(id, size);
     put_in_hash_table(anode, index);
     put_in_filelist(anode);
     update_cache(anode);
   }
 else if (cache_size >= size)  //since there is not enough space, is the file bigger than the whole cache size
  {
    remove_docs(size);
    anode = get_new_node(id, size);
    put_in_hash_table(anode, index);
    put_in_filelist(anode);
    update_cache(anode);
  }
}



//----------------------------------------------------------------------
// Rand::
//   
//----------------------------------------------------------------------
	
float
Rand::compute_hit_ratio()
{
 return (noof_hits *100.0 / noof_request);
}


//----------------------------------------------------------------------
// Rand::
//   
//----------------------------------------------------------------------

float
Rand::compute_byte_hit_ratio()
{
 return (byte_hit *100.0 / byte_requested);
}


//----------------------------------------------------------------------
// Rand::
//   
//----------------------------------------------------------------------

void 
Rand::free_row(RandNode *startptr)
{
 RandNode *next;
 
 do{
   next = startptr->GetRight();
   delete startptr;
   startptr = next;
 }while(startptr != NULL);
}


//----------------------------------------------------------------------
// Rand::
//   
//----------------------------------------------------------------------

void 
Rand::free_all_nodes()
{
  for (unsigned int i=0; i< MAX_SIZE; i++)
   if (hash_table[i] != NULL)
     {
       free_row(hash_table[i]);
     }
}


//----------------------------------------------------------------------
// Rand::
//   
//----------------------------------------------------------------------

bool
Rand::simulate(unsigned fileid, unsigned size)
{
 RandNode *loc;

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
       put_in_cache_RANDOM(fileid,size);

       return false;
  }

}

