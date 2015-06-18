//lfunode.h

#ifndef LFUNODE_H
#define LFUNODE_H

#include <stdlib.h>


class LfuNode {
 
 public:

  LfuNode(unsigned int initId, unsigned int initSize) //constructor
   { id = initId;  size = initSize; right = up = down = NULL; }

 ~LfuNode() {} //the destructor

 unsigned GetFileId() { return id; }
 unsigned GetFileSize() { return size; }
 LfuNode* GetRight() { return right; }
 LfuNode* GetUp() { return up; }
 LfuNode* GetDown () { return down; }
 void SetRight(LfuNode* initRight) { right = initRight; }
 void SetUp(LfuNode* initUp) { up = initUp; }
 void SetDown(LfuNode* initDown) { down = initDown; }

 void SetFreq(unsigned initFreq) { freq = initFreq; }
 unsigned GetFreq() {return freq; }
 void IncFreq() { freq++; }
 
 private:
  unsigned id; 
  unsigned size;
  unsigned freq;
  LfuNode *right;
  LfuNode *up;
  LfuNode *down;
};

#endif
