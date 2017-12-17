# CNKI Knowledge Search System

Author: Somefive

### Introduction

CNKI Knowledge Search System is a search engine based on **CNKI_journal Corpus**. It has basic search ability and some experimental functions. 

##### General Search

By default, you can type terms like `计算机` or `computer` to search relative publications. This search will automatically search in several fields such as title, abstract, organizations.

##### Analyzers

This system is indexing based on several different analyzer including `StandardAnalyzer`, `IKAnalyzer`, `JcsegAnalyzer`, `CJKAnalyzer`. You can try different analyzers to see their performance.

##### Advanced Search Options

There are generally two different modes: strict mode and loose mode. They will construct queries with `AND` and `OR`  boolean operators separately. You can also configure many detailed fields in **Advance Search**.

##### Related Words Suggestion

When you type terms in general search box, related words will be suggested below which are constructed by **word2vec**. This suggestion service is separated from query service.

### Project Structure

There are two separate projects: [EKi - backend](https://github.com/Somefive/eki), [AMe - frontend](https://github.com/Somefive/ame). 

**EKi** is mainly a restful backend based on [Play 2 framework](https://playframework.com/) in scala. IKAnalyzer depends on Lucene 4 while JcsegAnalyzer depends on Lucene 6 so some original IKAnalyzer Java codes are imported directly with some small modifications to be compatible with latter Lucene function interfaces. The suggestion service is built by python3. It is a lite Flask service with gensim(for word2vec) and THULAC(for word splitting).

**AMe** is a single page app built on [Quasar framework](http://quasar-framework.org), a [vue](https://vuejs.org) framework. [Lodash](https://lodash.com/) and [axios](https://github.com/axios/axios) library are used to help development.

The names of **EKi** and **AMe** are taken from a book *通り雨中の駅* written by *Kawabata Yasunari*. **EKi** corresponds to *駅* while **AMe** matches *雨*.

I intended to deploy this project on my server however my server is cheap and cannot allow backend run effectively. In a word, **Poverty limits my innovation.**. Just kidding. I am just lazy.

### Deploy

Required Environment: `java-1.8`, `sbt-0.13`, `python3`, `node v8`, `npm 5.6.0`.
This system is originally developed on `Ubuntu 16.10`.

#### EKi

Before everything starts, you should have one copy of original [corpus.txt](https://pan.baidu.com/s/1bDbQB8) in data folder.
Run `chmod +x -R ./scripts` which allow you to execute bash scripts inside it.

##### Query Service

First, run `./scripts/deploy.sh` to compile java & scala files into `target/`.
Second, run `./scripts/indexing.sh` to index data.
Last, run `./scripts/launch.sh` to start service.

##### Suggestion Service

Run `./scripts/wv-build.sh` to build word vector.
Then, run `./scripts/wv-serve.sh` to start service.

#### AMe

Run `npm run dev` can start frontend directly.
Alternatively way is to run `npm run build` to generate static files in `dist/` which is recommended in production environment.