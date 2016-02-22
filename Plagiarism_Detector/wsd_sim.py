from __future__ import division
from nltk.corpus import wordnet as wn
from nltk.corpus import genesis
from nltk.corpus import wordnet_ic
from nltk.stem import PorterStemmer
from itertools import chain
from senti_classifier.senti_classifier import disambiguateWordSenses  # @UnresolvedImport
import sys

nltk_tag = {'NN':wn.NOUN, 'JJ':wn.ADJ, 'VB':wn.VERB, 'RB':wn.ADV}
genesis_ic = wn.ic(genesis, False, 0.0)
brown_ic = wordnet_ic.ic('ic-brown.dat')
semcor_ic = wordnet_ic.ic('ic-semcor.dat')
ps = PorterStemmer()

def get_disambiguated_synset(line, word):
    return disambiguateWordSenses(line, word)

def lesk(context_sentence, ambiguous_word, pos=None, stem=True, hyperhypo=True):
    max_overlaps = 0; lesk_sense = None
    context_sentence = context_sentence.split()
    for ss in wn.synsets(ambiguous_word):
        # If POS is specified.
        if pos and ss.pos is not pos:
            continue

        lesk_dictionary = []

        # Includes definition.
        lesk_dictionary += ss.definition.split()
        # Includes lemma_names.
        lesk_dictionary += ss.lemma_names

        # Optional: includes lemma_names of hypernyms and hyponyms.
        if hyperhypo == True:
            lesk_dictionary += list(chain(*[i.lemma_names for i in ss.hypernyms() + ss.hyponyms()]))       

        if stem == True:  # Matching exact words causes sparsity, so lets match stems.
            lesk_dictionary = [ps.stem(i) for i in lesk_dictionary]
            context_sentence = [ps.stem(i) for i in context_sentence] 

        overlaps = set(lesk_dictionary).intersection(context_sentence)

        if len(overlaps) > max_overlaps:
            lesk_sense = ss
            max_overlaps = len(overlaps)
    return lesk_sense

def main():
    
    with open ("combined.txt", "r") as file:
        lines = file.readlines()
    
    words = []
    synsets = []
    words1 = lines[0].split(' ')
    
    for word in words1:
        # sense = get_disambiguated_synset(lines[0], word)
        sense = lesk(lines[0], word)
        if sense is not None:
            synset = sense.__reduce__()[2]['name']
            words.append(word)
            synsets.append(synset)
            # f.write(word + "\t" + synset + "\n")
    
    words2 = lines[1].split(' ')
    
    for word in words2:
        # sense = get_disambiguated_synset(lines[1], word)
        sense = lesk(lines[1], word)
        if sense is not None:
            synset = sense.__reduce__()[2]['name']
            words.append(word)
            synsets.append(synset)
            # f.write(word + "\t" + synset + "\n")
            
    f = open('similarities.txt', 'w')
    
    for w1 in range(len(words)):
        for w2 in range(w1 + 1, len(words)):
            sim = wn.synset(synsets[w1]).wup_similarity(wn.synset(synsets[w2]))
            if sim >= 0.5:
                # l = words[w1] + "_" + synsets[w1] + "\t" + words[w2] + "_" + synsets[w2] + "\t" + str(sim) + "\n"
                l = words[w1] + "\t" + words[w2] + "\t" + str(sim) + "\n"
                f.write(l)
 
    f.close()

if __name__ == "__main__":
    main()
