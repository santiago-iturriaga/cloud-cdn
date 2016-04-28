#!/usr/bin/env python2
# -*- coding: utf-8 -*-
#
#  transform_f201604.py
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

# Transforma las instancias generadas por ProWGen multiplicandolas
# tantas veces como MULTIPLIER para crear nuevas instancias manteniendo
# la distribución original.

# Además agrega un número de proveedor de forma aleatoria 
# entre 0 y NUM_PROV-1

import random
import os

NUM_PROV = [2,4,8]
NUM_INSTANCES = 5
NAME_DIMS = ['low','medium','high']
DIMS_SIZE = [1000,2000,4000]

def get_filename(dim, inst, ftype):
    return '{0}/data.{1}/{2}.video'.format(NAME_DIMS[dim],inst,ftype)

def get_filename_f201604(dim, inst, ftype):
    return '{0}.f201604'.format(get_filename(dim, inst, ftype))

def main(args):
    random.seed()

    for dim in range(3):
        for inst in range(5):
            print('Dimension {0} >> Instance {1}'.format(NAME_DIMS[dim], inst))

            try:
                os.remove(get_filename_f201604(dim,inst,'docs'))
            except:
                pass

            try:
                os.remove(get_filename_f201604(dim,inst,'workload'))
            except:
                pass
            
            docs_count = 0
            
            with open(get_filename(dim,inst,'docs'), 'r') as videos_in: 
                for v_in in videos_in:
                    docs_count = docs_count + 1
            
            next_doc_id = 0
            doc_id_mapping = {}

            with open(get_filename_f201604(dim,inst,'docs'), 'w') as videos_out: 
                with open(get_filename_f201604(dim,inst,'workload'), 'w') as workload_out:
                    with open(get_filename(dim,inst,'workload'), 'r') as workload_in:
                        for w_in in workload_in:
                            w_split = w_in.split(' ')
                            
                            w_time = int(w_split[0])
                            w_doc_id = int(w_split[1])
                            w_size = random.randint(554,1385)*(1024*1024)
                            w_orig = int(w_split[3])

                            if w_doc_id < (DIMS_SIZE[dim] * 0.10) \
                            or (w_doc_id > docs_count / 5 and w_doc_id - docs_count / 5 < (DIMS_SIZE[dim] * 0.20)) \
                            or docs_count - w_doc_id <= (DIMS_SIZE[dim] * 0.70):
                                if w_doc_id in doc_id_mapping:
                                    new_doc_id = doc_id_mapping[w_doc_id]
                                else:
                                    new_doc_id = next_doc_id
                                    doc_id_mapping[w_doc_id] = new_doc_id
                                    doc_prov_id = random.randint(0,NUM_PROV[dim]-1)
                                    videos_out.write("{0} {1} {2}\n".format(new_doc_id,w_size,doc_prov_id))
                                    #print('{0}'.format(new_doc_id))
                                    
                                    next_doc_id = next_doc_id + 1

                                #print('{0} {1} {2} {3}'.format(w_time, w_refs, w_size, w_orig))
                                workload_out.write('{0} {1} {2} {3}\n'.format(w_time, new_doc_id, w_size, w_orig))

    return 0

if __name__ == '__main__':
    import sys
    sys.exit(main(sys.argv))
