#!/usr/bin/env python2
# -*- coding: utf-8 -*-
#
#  transform_f201603.py
#
#  Copyright 2016 Santiago Iturriaga <santiago@marvin>
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

import random
import os

NUM_PROV = [2,4,6]
MAX_VIDEOS = [1000,4000,9000]
MIN_REQUESTS = [100000,800000,3600000]

NUM_INSTANCES = 5
NAME_DIMS = ['low','medium','high']

def get_filename(dim, inst, ftype):
    return '{0}/data.{1}/{2}.video'.format(NAME_DIMS[dim],inst,ftype)

def get_filename_f201603(dim, inst, ftype):
    return '{0}.f201603'.format(get_filename(dim, inst, ftype))

def main(args):
    random.seed()

    for dim in range(3):
        for inst in range(5):
            print('Dimension {0} >> Instance {1}'.format(NAME_DIMS[dim], inst))

            try:
                os.remove(get_filename_f201603(dim,inst,'docs'))
            except:
                pass

            try:
                os.remove(get_filename_f201603(dim,inst,'workload'))
            except:
                pass

            print('> docs ({0})'.format(MAX_VIDEOS[dim]))
            with open(get_filename_f201603(dim,inst,'docs'), 'w') as videos_out:
                with open(get_filename(dim,inst,'docs')) as videos_in:
                    for v_in in videos_in:
                        v_split = v_in.split(' ')
                        v_id = int(v_split[0])
                        v_size = int(v_split[1])
                        p_id = random.randint(0,NUM_PROV[dim]-1)

                        if v_id >= MAX_VIDEOS[dim]:
                            break

                        #print("{0} {1} {2}".format(v_id,v_size,p_id))
                        videos_out.write("{0} {1} {2}\n".format(v_id,v_size,p_id))

            total_requests = 0;

            print('> workload ({0})'.format(MIN_REQUESTS[dim]))
            with open(get_filename_f201603(dim,inst,'workload'), 'w') as workload_out:
                while total_requests < MIN_REQUESTS[dim]:
                    with open(get_filename(dim,inst,'workload')) as workload_in:
                        for w_in in workload_in:
                            w_split = w_in.split(' ')
                            w_time = int(w_split[0])
                            w_d_id = int(w_split[1])
                            w_size = int(w_split[2])
                            w_orig = int(w_split[3])

                            total_requests = total_requests + 1

                            w_d_id = w_d_id % MAX_VIDEOS[dim]

                            #print('{0} {1} {2} {3}'.format(w_time, w_refs, w_size, w_orig))
                            workload_out.write('{0} {1} {2} {3}\n'.format(w_time, w_d_id, w_size, w_orig))

    return 0

if __name__ == '__main__':
    import sys
    sys.exit(main(sys.argv))
