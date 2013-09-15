#!/usr/bin/env python
# -*- coding: utf-8 -*-
# 
# Git ignore empty folders, annoying, so we traverse the tree 
# adding or removing placeholders as needed. 
# 
# Jim Gunnarsson

import os

placeholder = '.placeholder.txt'

for dirname, dirnames, filenames in os.walk('.'):
   
   if '.git' in dirnames:
      # Don't go into any .git directories.
      dirnames.remove('.git')
   
   if (placeholder in filenames) and (len(filenames) > 1):
      # Found placeholder in a none empty folder, remove it
      os.remove(os.path.join(dirname, placeholder))
      print "Placeholder removed: {d}".format(d = os.path.join(dirname))
      
   if len(filenames) == 0:
      # Empty dir, add placeholder
      f = open(os.path.join(dirname, placeholder), 'w')
      f.write('Placeholder for empty files, created with add_placeholder.py\n\n')
      print "Placeholder added: {d}".format(d = os.path.join(dirname))


