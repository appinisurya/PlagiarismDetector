from __future__ import division
from nltk.corpus import wordnet as wn
from nltk.corpus import genesis
from nltk.corpus import wordnet_ic
import sys

nltk_tag = {'NN':wn.NOUN, 'JJ':wn.ADJ, 'VB':wn.VERB, 'RB':wn.ADV}
genesis_ic = wn.ic(genesis, False, 0.0)
brown_ic = wordnet_ic.ic('ic-brown.dat')
semcor_ic = wordnet_ic.ic('ic-semcor.dat')
oov = []

def similarity(word1, word2):
  
  w1, penn_tag1 = word1.split('_')
  w2, penn_tag2 = word2.split('_')
  
  if penn_tag1[:2] in nltk_tag:
      t1 = nltk_tag[penn_tag1[:2]]
  else:
      t1 = '.'
      
  if penn_tag2[:2] in nltk_tag:
      t2 = nltk_tag[penn_tag2[:2]]
  else:
      t2 = '.'

  synsets1 = wn.synsets(w1)
  synsets2 = wn.synsets(w2)
  
  if len(synsets1) == 0 and word1 not in oov:
      oov.append(word1)
       
  if len(synsets2) == 0 and word2 not in oov:
      oov.append(word2)
        
  sim_scores = []
  
  for synset1 in synsets1:
    for synset2 in synsets2:
      if(((synset1.pos == t1) and (synset2.pos == t2) and (t1 == t2))):
        sim_scores.append(synset1.wup_similarity(synset2))
      elif((synset1.pos == t1) and (synset2.pos == '.')):
        sim_scores.append(synset1.wup_similarity(synset2))
      elif((synset1.pos == '.') and (synset2.pos == t2)):
        sim_scores.append(synset1.wup_similarity(synset2))
      elif((synset1.pos == '.') and (synset2.pos == '.')):
        sim_scores.append(synset1.wup_similarity(synset2))
      
  if len(sim_scores) == 0:
    return 0
  else:
    return max(sim_scores)

def main():
  
  lines = [line.strip() for line in open('input.txt')]
  i = 0
  j = 0
  f = open(sys.argv[1], 'w')
  ov = open(sys.argv[2], 'w')
  
  for w1 in range(len(lines)):
	  for w2 in range(w1 + 1, len(lines)):
	      sim = similarity(lines[w1], lines[w2])
	      if sim > 0:
	      	l = lines[w1] + "\t" + lines[w2] + "\t" + str(sim) + "\n"
	      	f.write(l)
	      j = j + 1   
	  i = i + 1
	  j = 0
  f.close()
  
  for word in oov:
      ov.write(word + "\n")
            
  ov.close()
  
  
if __name__ == "__main__":
  main()
