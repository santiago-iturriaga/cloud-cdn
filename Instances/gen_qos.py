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
    distance_file = sys.argv[1]
    reg_users_file = sys.argv[2]
    dc_file = sys.argv[3]

    u_regions = []

    with open(reg_users_file) as reguf:
        for regul in reguf:
            data = regul.strip().split(' ')

            reg_id = int(data[2])
            reg_user_id = int(data[0])

            u_regions.append((reg_user_id, reg_id))

    dc_regions = []

    with open(dc_file) as regdcf:
        for regdcl in regdcf:
            data = regdcl.strip().split(' ')

            reg_id = int(data[2])
            reg_dc_id = int(data[0])
           
            dc_regions.append((reg_dc_id, reg_id))

    distances = []

    with open(distance_file) as distf:
        for distl in distf:
            data_raw = distl.strip().split('\t')

            data = []
            for d in data_raw:
                data.append(float(d))
                
            distances.append(data)

    qos = []

    for u in u_regions:
        u_reg = u[1]
       
        for dc in dc_regions:
            dc_reg = dc[1]

            avg_dist = distances[u_reg-1][dc_reg-1]
            u_dc_qos = int(round((avg_dist + avg_dist * 0.05 * random.random()) * 5000)) + random.randint(5,10)

            #print("{0} {1} {2} {3}".format(u_reg,dc_reg,avg_dist,u_dc_qos))

            qos.append((u[0], dc[0], u_dc_qos))

    for i in qos:
        print("{0} {1} {2}".format(i[0],i[1],i[2]))
   
    return 0

if __name__ == '__main__':
    main()

