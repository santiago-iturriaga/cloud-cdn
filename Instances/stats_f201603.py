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

NUM_INSTANCES = 5
NAME_DIMS = ['low','medium','high']

def get_filename(dim, inst, ftype):
    return '{0}/data.{1}/{2}.video'.format(NAME_DIMS[dim],inst,ftype)

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

            max_v_id = 0

            with open(get_filename(dim,inst,'docs')) as videos_in:
                for v_in in videos_in:
                    v_split = v_in.split(' ')
                    v_size = int(v_split[1])
                    v_id = int(v_split[0])
                    max_v_id = v_id

            workload_stats = []
            for i in range(max_v_id):
                workload_stats.append(0)
            workload_stats.append(0)

            print(len(workload_stats))

            with open(get_filename(dim,inst,'workload')) as workload_in:
                for w_in in workload_in:
                    w_split = w_in.split(' ')
                    w_time = int(w_split[0])
                    w_size = int(w_split[2])
                    w_orig = int(w_split[3])
                    w_d_id = int(w_split[1])

                    workload_stats[w_d_id] = workload_stats[w_d_id] + 1

            with open("{0}.stats".format(get_filename(dim,inst,'workload')), 'w') as workload_out:
                for i in range(len(workload_stats)):
                    workload_out.write("{0} {1}\n".format(i,workload_stats[i]))
    return 0

if __name__ == '__main__':
    import sys
    sys.exit(main(sys.argv))
