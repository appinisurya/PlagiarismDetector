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
    
    lines = ['Training takes a lot of effort']

    for line in lines:
        words = line.split(' ')
        for word in words:
            print word
            sense = lesk(line, word)
            if sense is not None:
                synset = sense.__reduce__()[2]['name']
                print word + "\t" + synset + "\n"
        
if __name__ == "__main__":
    main()
