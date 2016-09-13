#Reformats all headers to give them one homogeneous look
#Execute:
#find ../ -name *.java -not -path "*/generated-sources/*" -not -path "*/generated-test-sources/*" -exec python HeaderReformat.py {} \;

import re, mmap, sys, warnings, os

def _warning(
    message,
    category = UserWarning,
    inputFile = '',
    lineno = -1):
    print(message)
warnings.showwarning = _warning

inputFile = sys.argv[1]
authors="Joris Kinable"
year="2016"
data =""

with open(inputFile, 'r+') as f:
  data = mmap.mmap(f.fileno(), 0)
  
   
  #try parsing double header
  p= re.compile('/\*(.*?)\*/\r?\n\n?/\*(.*?)\*/', re.DOTALL)
  p = p.search(data)
  
  if p:
    firstHeader=p.group(1)
    secondHeader=p.group(2)
        
    #check whether there's a copyright statement present
    p=re.compile('\(C\)')
    p=p.search(secondHeader)
    if p: #found copyright statement: extract authors and contributors
      #Extract Copyright information
      #p=re.compile('\(C\)\s*Copyright\s*(\d+)\-?(\d+)?,\s*(by)?\s+(.*?)(\sand\sContributors\.?)?')
      p=re.compile('\(C\)\s*Copyright\s*(\d+)\-?(\d+)?,\s*(by)?\s*(.*)?')
      p=p.search(secondHeader)
      if not p:
	raise Exception('Cannot parse copyright statement in 2nd header: '+inputFile+"\n"+secondHeader)
      year=p.group(1)
      authors=p.group(4)
      #strip off "and Contributors" and variations thereof
      p=re.compile('(.*)(\sand)')
      p=p.search(authors)
      if p:
	authors=p.group(1)
      authors=re.sub('[.]', '', authors)
      
    else:
      #Search for "Original author block"
      p=re.compile('Original\sAuthor:\s*(.*)')
      p=p.search(secondHeader)
      p2=re.compile('\@author\s*(Original)?(\:)?\s*(.*)')
      p2=p2.search(data)
      if p:
	authors=p.group(1)
      elif p2:
	authors=p2.group(3)
      else:
	warnings.warn('Cannot find original author in 2nd header: '+inputFile+"; using default author\n")
      
      #Search for "Initial version" to extract year April-2016: Initial version;
      p=re.compile('(.*)?([0-9]{4,4})(.*)?Initial')
      p=p.search(secondHeader)
      if p:
	year=p.group(2)
      else: #try searching for a year in the first header
	p=re.compile('\(C\).*?[0-9]{4,4}-([0-9]{4,4})')
	p=p.search(firstHeader)
	if not p:
	  warnings.warn('Cannot find Initial version in 2nd header: '+inputFile+"; using default year\n")
	else:
	  year=p.group(1)
  else:
    #try parsing single header:
    p= re.compile('/\*\s*Copyright (.*?)\*/', re.DOTALL)
    p = p.search(data)
    if p: #Parse single header
      firstHeader=p.group(1)
      
      #search for author in data
      p=re.compile('Copyright\s*([0-9]{4,4})\s(.*)') #Copyright 2012 David Hadka
      p = p.search(data)
      if p:
	year=p.group(1)
	authors=p.group(2)
      else:
	warnings.warn('Cannot find author in data: '+inputFile+"; using default author\n")
	warnings.warn('Cannot find year in data: '+inputFile+"; using default year\n")
      
    else:
      warnings.warn('Did not find any headers: '+inputFile+"\n")

print("(C) Copyright "+year+"-2016, by "+authors+", and Contributors."+" "+inputFile)

#strip away double header
p= re.compile('/\*(.*?)\*/\r?\n\n?/\*(.*?)\*/\n*', re.DOTALL)
data=p.sub('', data)
#strip away single header
p= re.compile('/\*\s*Copyright (.*?)\*/', re.DOTALL)
data=p.sub('', data)
#print("result:")
#print(data)

newHeader= """line %d
	    line %d
	    line %d""" % (1, 2, 3)
	    
	    
newHeader= """/* ==========================================
 * jORLib : Java Operations Research Library
 * ==========================================
 *
 * Project Info:  http://www.coin-or.org/projects/jORLib.xml
 * Project Creator:  Joris Kinable (https://github.com/jkinable)
 *
 * (C) Copyright %s-2016, by %s and Contributors.
 *
 * This program and the accompanying materials are licensed under LGPLv2.1
 * as published by the Free Software Foundation.
 */\n""" % (year, authors)
 
#print(newHeader)

"""
outputFile = inputFile + ".tmp"
out = open(outputFile, "w")
out.write(newHeader)
out.write(data)
out.close()
os.rename(outputFile, inputFile)
"""
