from __future__ import division
from nltk.corpus import wordnet as wn
from senti_classifier.senti_classifier import synsets_scores  # @UnresolvedImport
from senti_classifier.senti_classifier import word_similarity  # @UnresolvedImport
from senti_classifier.senti_classifier import disambiguateWordSenses  # @UnresolvedImport
import sys

def get_word_sim(word1, word2):
    return word_similarity(word1, word2)

def get_disambiguated_synset(line, word):
    return disambiguateWordSenses(line, word)

def get_sentiscore(synset_name):
    return synsets_scores[synset_name]['pos'], synsets_scores[synset_name]['neg'] 

def main():

    f = open('synsets.txt', 'w')
    with open ("combined.txt", "r") as file:
        lines = file.readlines()
    
    words = lines[0].split(' ')

    for word in words:
        sense = get_disambiguated_synset(lines[0], word)
        if sense is not None:
            synset = sense.__reduce__()[2]['name']
            f.write(word + "\t" + synset + "\n")
    
    words = lines[1].split(' ')
    
    for word in words:
        sense = get_disambiguated_synset(lines[1], word)
        if sense is not None:
            synset = sense.__reduce__()[2]['name']
            f.write(word + "\t" + synset + "\n")
            
    f.close()
        
if __name__ == "__main__":
    main()
