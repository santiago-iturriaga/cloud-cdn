#!/usr/bin/env python
# -*- coding: utf-8 -*-
#
#  merge_all.py
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

def main():
    workload = sys.argv[1]
    geo_loc = sys.argv[2]

    with open(workload) as fwork:
        with open(geo_loc) as floc:
            for line_work in fwork:
                line_work = line_work.strip()

                if (len(line_work) > 0):
                    work_data = line_work.split(' ')

                    #['505245.156250', '79305', '877150', '1']
                    #print(work_data)
                    
                    work_arrival = int(work_data[0])
                    work_docid = int(work_data[1])
                    work_docsize = int(work_data[2])
                    work_loc = -1
                    
                    line_loc = floc.readline()
                    line_loc = line_loc.strip()

                    if (len(line_loc) > 0):
                        loc = int(float(line_loc)) - 1

                        #print(loc)

                        work_loc = loc
                    else:
                        print('ERROR! {0}'.format(line_work))
                        sys.exit(-1)

                    print("{0} {1} {2} {3}".format(work_arrival, work_docid, work_docsize, work_loc))
    return 0

if __name__ == '__main__':
    main()

