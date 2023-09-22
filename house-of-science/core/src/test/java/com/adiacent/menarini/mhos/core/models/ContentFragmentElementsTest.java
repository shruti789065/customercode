package com.adiacent.menarini.mhos.core.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ContentFragmentElementsTest {

    private ContentFragmentElements models;

    @BeforeEach
    void setUp() {
        models = new ContentFragmentElements();

        Stream<String> authStream = Stream.of("Luis Ortega","hu yihn");
        ContentFragmentElementValue auth = new ContentFragmentElementValue();
        auth.setValue(authStream.collect(Collectors.toList()));
        auth.setType("auth");
        models.setAuthor(auth);

        ContentFragmentElementSingleValue desc = new ContentFragmentElementSingleValue();
        desc.setType("desc");
        desc.setValue("abstract");
        models.setDescription(desc);


        Stream<String> typeStream = Stream.of("A", "B", "C", "D", "E");
        ContentFragmentElementValue typology = new ContentFragmentElementValue();
        typology.setValue(typeStream.collect(Collectors.toList()));
        typology.setType("type");
        models.setTypology(typology);

        Stream<String> tagsStream = Stream.of("A", "B", "C", "D", "E");
        ContentFragmentElementValue tags = new ContentFragmentElementValue();
        tags.setValue(tagsStream.collect(Collectors.toList()));
        tags.setType("generic tags");
        models.setTags(tags);


        Stream<String> sourceStream = Stream.of("A", "B", "C", "D", "E");
        ContentFragmentElementValue source = new ContentFragmentElementValue();
        source.setValue(sourceStream.collect(Collectors.toList()));
        source.setType("sources");
        models.setSource(source);


        ContentFragmentElementSingleValue lnk = new ContentFragmentElementSingleValue();
        lnk.setValue("http://localhost:4502/content/en");
        lnk.setType("link");
        models.setLink(lnk);


        ContentFragmentElementSingleValue artDate = new ContentFragmentElementSingleValue();
        lnk.setValue(LocalDate.now());
        lnk.setType("date");
        models.setArticleDate(artDate);


        Stream<String> topicsStream = Stream.of("A", "B", "C", "D", "E");
        ContentFragmentElementValue topics = new ContentFragmentElementValue();
        topics.setValue(topicsStream.collect(Collectors.toList()));
        topics.setType("topics");
        models.setTopic(topics);

    }

    @Test
    void getTopic() {
        assertTrue(models.getTopic().getValue().size() > 0);
    }

    @Test
    void getSource() {
        assertTrue(models.getSource().getValue().size() > 0);
    }


    @Test
    void getTags() {
        assertTrue(models.getTags().getValue().size() > 0);
    }

    @Test
    void getLink() {
        assertNotNull(models.getLink().getType());
    }

    @Test
    void getDescription() {
        assertNotNull(models.getDescription().getType());
    }

    @Test
    void getArticleDate() {
        assertNotNull(models.getArticleDate());
    }


}