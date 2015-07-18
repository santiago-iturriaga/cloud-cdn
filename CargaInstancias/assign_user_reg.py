#!/usr/bin/env python
# -*- coding: utf-8 -*-
#
#  assign_user_reg.py
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
import random

def main():
    workload_file = sys.argv[1]
    reg_users_file = sys.argv[2]

    regions = {}

    with open(reg_users_file) as regf:
        for regl in regf:
            data = regl.strip().split(' ')

            reg_id = int(data[2])
            reg_user_id = int(data[0])

            if not reg_id in regions:
                regions[reg_id] = []
            
            regions[reg_id].append(reg_user_id)

    with open(workload_file) as workf:
        for workl in workf:
            data = workl.strip().split(' ')
            work_arrival = int(data[0])
            work_docid = int(data[1])
            work_docsize = int(data[2])
            work_loc = int(data[3])

            max_reg = len(regions[work_loc])-1
            user_loc = random.randint(0, max_reg)
            work_loc = regions[work_loc][user_loc]

            print("{0} {1} {2} {3}".format(work_arrival, work_docid, work_docsize, work_loc))
    
    return 0

if __name__ == '__main__':
    main()

