package test;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.util.CoreMap;

public class StanfordLemmatizer {

    
	protected StanfordCoreNLP pipeline;
    

    public StanfordLemmatizer() {
        // Create StanfordCoreNLP object properties, with POS tagging
        // (required for lemmatization), and lemmatization
        Properties props;
        props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma");
        //final MaxentTagger tagger = new MaxentTagger("libs/english-left3words-distsim.tagger");

        //props.put("pos.model", "libs/english-left3words-distsim.tagger");
        //final MaxentTagger tagger = new MaxentTagger("taggers/english-left3words-distsim.tagger");

//        String modPath = "C:/Users/Railan/Documents/2015.2/Projeto Data Mining/Standford/stanford-english-corenlp-2016-01-10-models/edu/stanford/nlp/models/";
//        props.put("pos.model", modPath + "pos-tagger/english-left3words/english-left3words-distsim.tagger");
//        props.put("ner.model", modPath + "ner/english.all.3class.distsim.crf.ser.gz");
//        props.put("parse.model", modPath + "lexparser/englishPCFG.ser.gz");
//        props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
//        props.put("sutime.binders","0");
//        props.put("sutime.rules", modPath + "sutime/defs.sutime.txt, " + modPath + "sutime/english.sutime.txt");
//        props.put("dcoref.demonym", modPath + "dcoref/demonyms.txt");
//        props.put("dcoref.states", modPath + "dcoref/state-abbreviations.txt");
//        props.put("dcoref.animate", modPath + "dcoref/animate.unigrams.txt");
//        props.put("dcoref.inanimate", modPath + "dcoref/inanimate.unigrams.txt");
//        props.put("dcoref.big.gender.number", modPath + "dcoref/gender.data.gz");
//        props.put("dcoref.countries", modPath + "dcoref/countries");
//        props.put("dcoref.states.provinces", modPath + "dcoref/statesandprovinces");
//        props.put("dcoref.singleton.model", modPath + "dcoref/singleton.predictor.ser");        //final MaxentTagger tagger = new MaxentTagger("taggers/english-left3words-distsim.tagger");


        /*
         * This is a pipeline that takes in a string and returns various analyzed linguistic forms. 
         * The String is tokenized via a tokenizer (such as PTBTokenizerAnnotator), 
         * and then other sequence model style annotation can be used to add things like lemmas, 
         * POS tags, and named entities. These are returned as a list of CoreLabels. 
         * Other analysis components build and store parse trees, dependency graphs, etc. 
         * 
         * This class is designed to apply multiple Annotators to an Annotation. 
         * The idea is that you first build up the pipeline by adding Annotators, 
         * and then you take the objects you wish to annotate and pass them in and 
         * get in return a fully annotated object.
         * 
         *  StanfordCoreNLP loads a lot of models, so you probably
         *  only want to do this once per execution
         */
        this.pipeline = new StanfordCoreNLP(props);
    }

    public List<String> lemmatize(String documentText)
    {
        List<String> lemmas = new LinkedList<String>();
        // Create an empty Annotation just with the given text
        Annotation document = new Annotation(documentText);
        // run all Annotators on this text
        this.pipeline.annotate(document);
        // Iterate over all of the sentences found
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        for(CoreMap sentence: sentences) {
            // Iterate over all tokens in a sentence
            for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
                // Retrieve and add the lemma for each word into the
                // list of lemmas
                lemmas.add(token.get(LemmaAnnotation.class));
            }
        }
        return lemmas;
    }


    public static void main(String[] args) {
        //System.out.println("Starting Stanford Lemmatizer");
        String text = "is slightly different than what's documented.";
        StanfordLemmatizer slem = new StanfordLemmatizer();
        System.out.println(slem.lemmatize(text));
    }

}
