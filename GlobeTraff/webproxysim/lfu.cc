
//lfu.cc


#include "lfu.h"
#include <stdio.h>


//----------------------------------------------------------------------
// Lfu::
//   
//----------------------------------------------------------------------
Lfu::Lfu(unsigned int initWarmup)
{
  //initialize the warmup values
  warmup = initWarmup;

  MAX_SIZE = 100000; //this is the maximum size of the hast table;

  FREQ_TABLE_SIZE = 10000; //this is the maximum of the unique popularities we want to track

  average_age = 3; // just initialize the average age to 3

  //allocate memory for the hash table
  if ( (hash_table = new LfuNode*[MAX_SIZE]) == NULL)
   {
     ErrorMessage("Not enough memory to allocate hash table");
   }
  
  if ((freqtable = new LfuNode*[FREQ_TABLE_SIZE]) == NULL)
    {
      ErrorMessage("Not enough memory to allocate freq table");
    }
}

//----------------------------------------------------------------------
// Lfu::
//   
//----------------------------------------------------------------------
Lfu::~Lfu()
{
  delete [] hash_table;
  delete [] freqtable;
}


//----------------------------------------------------------------------
// Lfu::initialize
//    This method creates the hash table and initializes the entries to
//    Null. It also initializes all other variables to their respective
//    initial values
//----------------------------------------------------------------------


void 
Lfu::initialize(float cacheSize)
{
 cache_size = cacheSize;

 noof_hits = noof_request = total_noof_request = docs_count = sumof_freqs = 0;
 cache_freespace = cache_size;
 
 //initialize the hash table
 for (unsigned i=0; i< MAX_SIZE;  i++)
     hash_table[i] =  NULL;

 for (unsigned i=0; i< FREQ_TABLE_SIZE;  i++)
     hash_table[i] =  NULL;
 
 head = tail = NULL;
 byte_requested = byte_hit =0.0;
}


//----------------------------------------------------------------------
// Lfu::
//   
//----------------------------------------------------------------------


void
Lfu::ErrorMessage(char* s)
{
  printf("\n **** %s ****",s);
  exit(1);
}

//----------------------------------------------------------------------
// Lfu::
//   
//----------------------------------------------------------------------


LfuNode* 
Lfu::in_cache(unsigned int id)
{
 unsigned int index = (id % MAX_SIZE);
 unsigned int found = false;
 LfuNode *next, *loc = NULL;
 
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
// Lfu::
//   
//----------------------------------------------------------------------

void
Lfu::update_statistics(unsigned size)
{
  ++total_noof_request;
  if (total_noof_request > warmup)
     {
	 ++noof_request;
	 byte_requested = byte_requested + size;
     } 
}

//----------------------------------------------------------------------
// Lfu::
//   
//----------------------------------------------------------------------

void 
Lfu::put_in_hash_table(LfuNode *anode, unsigned int index)
{
     anode->SetRight(hash_table[index]);
     hash_table[index] = anode;
}

//----------------------------------------------------------------------
// Lfu::
//   
//----------------------------------------------------------------------

LfuNode* 
Lfu::get_new_node(unsigned int id, unsigned int size)
{
 LfuNode *anode;

 anode = new LfuNode(id, size); 
 if (anode == NULL)
  ErrorMessage("Cannot allocate more memory for new nodes");
 
 anode->SetFreq(1);
 sumof_freqs++;
 docs_count++;

 //now decrement the remaining cache free space   
 cache_freespace -= size;
 
 //return the newly constructed node
 return anode;
}


//----------------------------------------------------------------------
// Lfu::
//   
//----------------------------------------------------------------------

LfuNode* 
Lfu::getHorzPred(LfuNode  *loc, unsigned int index)
{
 LfuNode  *start = hash_table[index];
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
// Lfu::
//   
//----------------------------------------------------------------------

void 
Lfu::remove_docs(unsigned int size)
{
  LfuNode *next, *HorzPred;
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

    i = next->GetFreq();
    if ((i < FREQ_TABLE_SIZE) && (freqtable[i] == next))
      freqtable[i] = NULL;

    cache_freespace += next->GetFileSize();
    
    docs_count--;
    sumof_freqs -= next->GetFreq();

    delete next;

  }while((cache_freespace < size)&&(tail != NULL));
}



//----------------------------------------------------------------------
// Lfu::
//   
//----------------------------------------------------------------------
	
void 
Lfu::put_in_cache(unsigned int id, unsigned int size)
{
 LfuNode *anode;  
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
//  Lfu::
//   
//----------------------------------------------------------------------
	
void 
Lfu::found_update(LfuNode *loc)
{
 unsigned int index = loc->GetFreq();
 LfuNode *predloc = loc->GetUp();
 LfuNode *succloc = loc->GetDown();

 if((loc != head)&&(succloc == NULL)) ErrorMessage("\nError in found update ");
 if ((loc == head)||((index+1) < succloc->GetFreq()))      //no need to detach loc 
   {
     if(index < FREQ_TABLE_SIZE)
	if ((predloc != NULL) && (predloc->GetFreq() == index))
	  freqtable[index] = predloc;
	else 
	  freqtable[index] = NULL;

       if ((index + 1) < FREQ_TABLE_SIZE)
          freqtable[index+1] = loc;
       loc->IncFreq();
       sumof_freqs++;
   }
else      //needs to detach loc since it is not at the head
 {

  if (predloc!= NULL)
    predloc->SetDown(succloc);
  if (succloc != NULL)
    succloc->SetUp(predloc);
  if (tail == loc)
      tail = succloc;

  if((index < FREQ_TABLE_SIZE)&&(freqtable[index] == loc))
       if ((predloc != NULL) && (predloc->GetFreq() == index))
	  freqtable[index] = predloc;
	else 
	  freqtable[index] = NULL;

  loc->SetDown(NULL);
  loc->SetUp(NULL);
  loc->IncFreq();
  sumof_freqs++;

  update_cache(loc);
 } 
}


//----------------------------------------------------------------------
//  Lfu::
//   
//----------------------------------------------------------------------
	
float
Lfu::compute_hit_ratio()
{
 return (noof_hits *100.0 / noof_request);
}


//----------------------------------------------------------------------
//  Lfu::
//   
//----------------------------------------------------------------------

float
 Lfu::compute_byte_hit_ratio()
{
 return (byte_hit *100.0 / byte_requested);
}


//----------------------------------------------------------------------
//  Lfu::
//   
//----------------------------------------------------------------------

void 
Lfu::free_row(LfuNode *startptr)
{
 LfuNode *next;
 
 do{
   next = startptr->GetRight();
   delete startptr;
   startptr = next;
 }while(startptr != NULL);
}


//----------------------------------------------------------------------
//  Lfu::
//   
//----------------------------------------------------------------------

void 
Lfu::free_all_nodes()
{
  for (unsigned int i=0; i< MAX_SIZE; i++)
   if (hash_table[i] != NULL)
     {
       free_row(hash_table[i]);
     }
}




//----------------------------------------------------------------------
//  Lfu::
//   
//----------------------------------------------------------------------

void 
Lfu::half_all_freqs()
{
  LfuNode *start = tail;
  sumof_freqs = 0;
  for (unsigned i=0; i<FREQ_TABLE_SIZE; i++)
    freqtable[i] = NULL;

  while (start != NULL)
    {
      start->SetFreq(start->GetFreq() / 2);
      if (start->GetFreq() == 0) start->IncFreq();
      unsigned freq = start->GetFreq();
      sumof_freqs += freq;
      if (freq < FREQ_TABLE_SIZE)
	freqtable[freq] = start;
      start = start->GetDown();
    }
}


//----------------------------------------------------------------------
//  Lfu::
//   
//----------------------------------------------------------------------

LfuNode*
Lfu::get_where_to_insert(LfuNode *loc)
{
  LfuNode  *where;
  unsigned int i, index = loc->GetFreq();

  if (head == NULL) 
    where = NULL;
  else if (index >= head->GetFreq()) 
    where = head;
  else if (index < tail->GetFreq()) 
    where = tail;
  else if (index < FREQ_TABLE_SIZE)   // freq is within the ranged being tracked
  {
    if (freqtable[index] != NULL)  //there is a node of this freq b4
      where = freqtable[index];

    else  //there is no node with this freq b4, then go backward or forward
      {
	i = index - 1;
	while((i>0) && (freqtable[i]==NULL))
	  --i;
	if (i==0)  //did not get any node backward with a lower freq
	   ErrorMessage("This is definately an Error in module get-where-to-insert.. must be able to locate sth backward");
	//else //if (freqtable[i]!=NULL) //got a node with lower freq
	where = freqtable[i];
      }
  }
 else  //loc->freq outside the range being tracked, need to get the first node within range
   {
     
     // starts search from head
     where = head->GetUp();
     while(where->GetFreq() > index)
       where = where->GetUp();
   }
  
  return where;
}
	

//----------------------------------------------------------------------
//  Lfu::
//   
//----------------------------------------------------------------------

void
Lfu::update_cache(LfuNode *loc)
{
 LfuNode *where, *predwhere, *succwhere;

 where = get_where_to_insert(loc);
 
 if (where == NULL)
   {
     head = loc; 
     tail = loc;
   } 
 else if (loc->GetFreq() >= where->GetFreq())  //inserts after where
  { 
    succwhere = where->GetDown();
    loc->SetDown(succwhere);
    loc->SetUp(where);
    where->SetDown(loc);
    if (succwhere != NULL)
      succwhere->SetUp(loc);
    else 
      head = loc;
  }
 else  //inserts b4 where
   { 
     predwhere = where->GetUp();
     loc->SetUp(predwhere);
     loc->SetDown(where);
     where->SetUp(loc);
     if (predwhere != NULL)
       predwhere->SetDown(loc);
     else 
       tail = loc;
   }

  if (loc->GetFreq() < FREQ_TABLE_SIZE)
   freqtable[loc->GetFreq()] = loc; 
}    

//----------------------------------------------------------------------
//  Lfu::
//   
//----------------------------------------------------------------------

bool
Lfu::simulate(unsigned fileid, unsigned size)
{
 LfuNode *loc;
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

// printf("in lfu %d\n", docs_count); 
  fflush(NULL); 
/*
 if ((sumof_freqs/docs_count) > average_age) { 
   half_all_freqs();
 }
*/
  return flag;

}



