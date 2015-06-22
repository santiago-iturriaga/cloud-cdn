#!/usr/bin/env python
# -*- coding: utf-8 -*-
#
#  data_stats.py
#  
#  Copyright 2015 Santiago Iturriaga - INCO <siturria@saxo.fing.edu.uy>
#  
#  This program is free software; you can redistribute it and/or modify
#  it under the terms of the GNU General Public License as published by
#  the Free Software Foundation; either version 2 of the License, or
#  (at your option) any later version.
#  
#  This program is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU General Public License for more details.
#  
#  You should have received a copy of the GNU General Public License
#  along with this program; if not, write to the Free Software
#  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
#  MA 02110-1301, USA.
#  
#  

import sys

NUM_REGIONS=5

def main():
    workload_type = sys.argv[1]
    inst_num = sys.argv[2]
    
    requests = []
    for i in range(NUM_REGIONS):
        requests.append({})
        requests[i]['count'] = 0
        requests[i]['traffic'] = 0
        requests[i]['diffdocs'] = {}
    
    with open("{0}/data.{1}/workload.video".format(workload_type, inst_num)) as workload:
        for line in workload:
            data = line.strip().split("\t")
            time = float(data[0])
            docid = int(data[1])
            docsize = int(data[2])
            region = int(data[3])

            requests[region]['count'] = requests[region]['count'] + 1
            requests[region]['traffic'] = requests[region]['traffic'] + docsize
            requests[region]['diffdocs'][docid] = None
        
    for region in range(NUM_REGIONS):
        print("Region {0} =>".format(region))
        print("    # of requests: {0}".format(requests[region]['count']))
        print("    total traffic: {0}".format(requests[region]['traffic']))
        print("    # of docs    : {0}".format(len(requests[region]['diffdocs'])))
        
    return 0

if __name__ == '__main__':
    main()

