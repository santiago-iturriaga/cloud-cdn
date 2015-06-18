all: drivers herar

# Cache driver modules for simulating different replacement policies
drivers: CacheDriver lrudriver lfudriver gddriver fifodriver randdriver

# Master cache driver, which can use any replacement policy
CacheDriver: lru.h lfu.h gdsize.h rand.h fifo.h lru.cc lfu.cc gdsize.cc rand.cc fifo.cc CacheDriver.cc
	g++ -c CacheDriver.cc
	g++ -c lru.cc
	g++ -c lfu.cc
	g++ -c gdsize.cc
	g++ -c rand.cc
	g++ -c fifo.cc
	g++ -c heap.cc
	g++ -o CacheDriver lru.o lfu.o gdsize.o rand.o fifo.o heap.o CacheDriver.o

# LRU: Least Recently Used replacement policy
lrudriver: lru.h lru.cc lrudriver.cc
	g++ -c lrudriver.cc
	g++ -c lru.cc
	g++ -o lrudriver lru.o lrudriver.o

# LFU: Least Frequently Used replacement policy (with aging)
lfudriver: lfu.h lfu.cc lfudriver.cc
	g++ -c lfudriver.cc
	g++ -c lfu.cc
	g++ -o lfudriver lfu.o lfudriver.o

# RAND: Random replacement policy
randdriver: rand.h rand.cc randdriver.cc
	g++ -c randdriver.cc
	g++ -c rand.cc
	g++ -o randdriver rand.o randdriver.o

# FIFO: First-In-First-Out replacement policy
fifodriver: fifo.h fifo.cc fifodriver.cc
	g++ -c fifodriver.cc
	g++ -c fifo.cc
	g++ -o fifodriver fifo.o fifodriver.o

# GDSIZE: Greedy-Dual Size(1) replacement policy
gddriver: gdsize.h gdsize.cc heap.cc gddriver.cc
	g++ -c gddriver.cc
	g++ -c gdsize.cc
	g++ -c heap.cc
	g++ -o gddriver gdsize.o heap.o gddriver.o

# Heap data structure utilities
heap.o: heap.h heap.cc
	g++ -c heap.cc

# LRU module
lru.o: lrunode.h lru.h lru.cc
	g++ -c lru.cc

# LFU module
lfu.o: lfunode.h lfu.h lfu.cc
	g++ -c lfu.cc

# GDSIZE module
gdsize.o: gdnode.h gdsize.h gdsize.cc
	g++ -c gdsize.cc

# Hierarchical two-level caching configurations
#   herar1: LRU at child cache, LRU at parent cache
#   herar2: LRU at child cache, LFU at parent cache
#   herar3: LRU at child cache, GDSIZE at parent cache
#   herar4: LFU at child cache, LRU at parent cache
#   herar5: LFU at child cache, LFU at parent cache
#   herar6: LFU at child cache, GDSIZE at parent cache
#   herar7: GDSIZE at child cache, LRU at parent cache
#   herar8: GDSIZE at child cache, LFU at parent cache
#   herar9: GDSIZE at child cache, GDSIZE at parent cache
herar:  heap.o lru.o lfu.o gdsize.o herar1.o herar2.o herar3.o herar4.o herar5.o  herar6.o herar7.o herar8.o herar9.o 
	g++ -o herar1  heap.o lru.o lfu.o gdsize.o herar1.o 
	g++ -o herar2  heap.o lru.o lfu.o gdsize.o herar2.o 
	g++ -o herar3  heap.o lru.o lfu.o gdsize.o herar3.o 
	g++ -o herar4  heap.o lru.o lfu.o gdsize.o herar4.o 
	g++ -o herar5  heap.o lru.o lfu.o gdsize.o herar5.o 
	g++ -o herar6  heap.o lru.o lfu.o gdsize.o herar6.o 
	g++ -o herar7  heap.o lru.o lfu.o gdsize.o herar7.o 
	g++ -o herar8  heap.o lru.o lfu.o gdsize.o herar8.o 
	g++ -o herar9  heap.o lru.o lfu.o gdsize.o herar9.o 


# Note that the source code for these ones assume COMPLETE SHARING
# of all requests. Slight changes are needed to do partial and no sharing.
herar1.o: lru.h lfu.h gdsize.h herar1.cc herar2.cc herar3.cc herar4.cc herar5.cc herar6.cc herar7.cc herar8.cc herar9.cc
	g++ -c herar1.cc
	g++ -c herar2.cc
	g++ -c herar3.cc
	g++ -c herar4.cc
	g++ -c herar5.cc
	g++ -c herar6.cc
	g++ -c herar7.cc
	g++ -c herar8.cc
	g++ -c herar9.cc

clean:
	rm -f *.o *~
	rm -f herar1 herar2 herar3 herar4 herar5 herar6 herar7 herar8 herar9
	rm -f lrudriver lfudriver gddriver randdriver fifodriver CacheDriver
