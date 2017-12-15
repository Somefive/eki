import thulac
import re

analyzer = thulac.thulac(seg_only=True)
pattern = re.compile(r'.*<(.*)>=(.*)')

analyzer_fields = ['英文刊名', '题名', '英文篇名', '摘要', '英文摘要']

fout = open('../data/corpus-split.txt', 'w')
line_count = 0
with open('../data/corpus.txt') as f:
    for line in f:
        result = pattern.match(line)
        if result and result.groups()[0] in analyzer_fields:
            a_list = list(filter(lambda x: len(x) > 0, map(lambda x: x[0].strip(), analyzer.cut(result.groups()[1]))))
            if len(a_list) > 0:
                fout.write(' '.join(a_list) + '\n')
        line_count += 1
        if line_count % 1000 == 0:
            print('\rline: %10i' % line_count, end='')
fout.close()