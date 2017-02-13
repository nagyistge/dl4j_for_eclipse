package deeplearning4j_nlp_parent.deeplearning4j_nlp.src.main.java.org.deeplearning4j.models.sequencevectors.transformers.impl.iterables;
//package org.deeplearning4j.models.sequencevectors.transformers.impl.iterables;

import java.util.Iterator;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import deeplearning4j_nlp_parent.deeplearning4j_nlp.src.main.java.org.deeplearning4j.models.sequencevectors.sequence.Sequence;
//import org.deeplearning4j.models.sequencevectors.sequence.Sequence;
//import org.deeplearning4j.models.sequencevectors.transformers.impl.SentenceTransformer;
//import org.deeplearning4j.models.word2vec.VocabWord;
//import org.deeplearning4j.text.documentiterator.LabelAwareIterator;
//import org.deeplearning4j.text.documentiterator.LabelledDocument;
import deeplearning4j_nlp_parent.deeplearning4j_nlp.src.main.java.org.deeplearning4j.models.sequencevectors.transformers.impl.SentenceTransformer;
import deeplearning4j_nlp_parent.deeplearning4j_nlp.src.main.java.org.deeplearning4j.models.word2vec.VocabWord;
import deeplearning4j_nlp_parent.deeplearning4j_nlp.src.main.java.org.deeplearning4j.text.documentiterator.LabelAwareIterator;
import deeplearning4j_nlp_parent.deeplearning4j_nlp.src.main.java.org.deeplearning4j.text.documentiterator.LabelledDocument;

/**
 * @author raver119@gmail.com
 */
@Slf4j
public class BasicTransformerIterator implements Iterator<Sequence<VocabWord>> {
    protected final LabelAwareIterator iterator;
    protected boolean allowMultithreading;
    protected final SentenceTransformer sentenceTransformer;

    public BasicTransformerIterator(@NonNull LabelAwareIterator iterator, @NonNull SentenceTransformer transformer) {
        this.iterator = iterator;
        this.allowMultithreading = false;
        this.sentenceTransformer = transformer;

        this.iterator.reset();
    }

    @Override
    public boolean hasNext() {
        return this.iterator.hasNextDocument();
    }

    @Override
    public Sequence<VocabWord> next() {
        LabelledDocument document = iterator.nextDocument();
        if  (document.getContent() == null) return new Sequence<>();
        Sequence<VocabWord> sequence = sentenceTransformer.transformToSequence(document.getContent());

        for (String label: document.getLabels()) {
            if (label != null && !label.isEmpty())
                sequence.addSequenceLabel(new VocabWord(1.0, label));
        }
                /*
                if (document.getLabel() != null && !document.getLabel().isEmpty()) {
                    sequence.setSequenceLabel(new VocabWord(1.0, document.getLabel()));
                }*/

        return sequence;
    }


    public void reset() {
        //
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}