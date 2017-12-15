from flask import Flask, request
from flask_cors import CORS
from bson.json_util import dumps
import thulac
from gensim.models.word2vec import *

app = Flask('Word2Vec')
CORS(app)
analyzer = thulac.thulac(seg_only=True)
model = Word2Vec.load('../data/word2vec-model')

@app.route('/')
def hello():
    return 'This is word2vec app.'

@app.route('/similar', methods=['POST'])
def similar():
    json = request.get_json()
    query = json['query'] if 'query' in json else ''
    positive = list(filter(lambda x: len(x) > 0, map(lambda x: x[0].strip(), analyzer.cut(query))))
    try:
        return dumps(list(map(lambda x: x[0], model.wv.most_similar(positive=positive)[0:5])), ensure_ascii=False)
    except:
        return dumps(list())

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=9001)