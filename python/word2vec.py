from gensim.models.word2vec import *

sentences = LineSentence('../data/corpus-split.txt')
model = Word2Vec(sentences, size=100, window=5, min_count=5, workers=4)
model.save('../data/word2vec-model')