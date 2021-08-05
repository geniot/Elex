package io.github.geniot.elex.model;

import io.github.geniot.elex.Utils;
import io.github.geniot.elex.model.lucene.SerializableRAMDirectory;
import io.github.geniot.indexedtreemap.IndexedTreeSet;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static io.github.geniot.elex.model.Headword.referenceToHeadwords;

public class ZipDictionary implements IDictionary {
    private URI uri;
    private Map<String, String> env;

    private Base64.Encoder encoder = Base64.getUrlEncoder();
    private Base64.Decoder decoder = Base64.getUrlDecoder();

    private static String ENTRIES = "/entries/";
    private static String FT_INDEX = "/ft-index.ser";
    private static String PROPS = "/dictionary.properties";
    private static String ICON_FILE = "/icon.png";

    private FileSystem zipFileSystem = null;

    public ZipDictionary(String s) throws Exception {
        this(URI.create("jar:" + new File(s).toURI()));
    }

    public ZipDictionary(File s) throws Exception {
        this(URI.create("jar:" + s.toURI()));
    }

    public ZipDictionary(URI u) throws Exception {
        this.uri = u;
        env = new HashMap<>();
        HashMap envCreate = new HashMap<>();
        envCreate.put("create", "true");
        try {
            zipFileSystem = FileSystems.getFileSystem(uri);
        } catch (FileSystemNotFoundException ex) {
            zipFileSystem = FileSystems.newFileSystem(uri, envCreate);
        }
        Path pathInZipFile = zipFileSystem.getPath(ENTRIES);
        Files.createDirectories(pathInZipFile);
    }

    @Override
    public void close() throws Exception {
        if (zipFileSystem != null) {
            zipFileSystem.close();
        }
    }

    @Override
    public void createOrUpdate(String headword, String entry) throws Exception {
        String encodedHeadword = encoder.encodeToString(headword.getBytes(StandardCharsets.UTF_8));
        Path pathInZipFile = zipFileSystem.getPath(ENTRIES + encodedHeadword);
        Files.deleteIfExists(pathInZipFile);
        Files.write(pathInZipFile, entry.getBytes(StandardCharsets.UTF_8));

        SortedMap<String, String> entries = new TreeMap<>();
        entries.put(headword, entry);
        updateFullTextIndex(entries, false);
    }

    @Override
    public void bulkCreateOrUpdate(SortedMap<String, String> entries) throws Exception {

        for (Map.Entry<String, String> entry : entries.entrySet()) {
            String headword = entry.getKey();
            String content = entry.getValue();
            String encodedHeadword = encoder.encodeToString(headword.getBytes(StandardCharsets.UTF_8));
            Path pathInZipFile = zipFileSystem.getPath(ENTRIES + encodedHeadword);
            Files.write(pathInZipFile, content.getBytes(StandardCharsets.UTF_8));
        }

        updateFullTextIndex(entries, false);
    }

    @Override
    public String read(String headword) throws Exception {
        String encodedHeadword = encoder.encodeToString(headword.getBytes(StandardCharsets.UTF_8));
        Path pathInZipFile = zipFileSystem.getPath(ENTRIES + encodedHeadword);
        if (!Files.exists(pathInZipFile)) {
            return null;
        }
        String article = new String(Files.readAllBytes(pathInZipFile), StandardCharsets.UTF_8);
        return article;
    }

    @Override
    public void delete(String headword) throws Exception {
        String encodedHeadword = encoder.encodeToString(headword.getBytes(StandardCharsets.UTF_8));
        Path pathInZipFile = zipFileSystem.getPath(ENTRIES + encodedHeadword);
        Files.deleteIfExists(pathInZipFile);

        SortedMap<String, String> entries = new TreeMap<>();
        entries.put(headword, null);
        updateFullTextIndex(entries, true);
    }

    @Override
    public IndexedTreeSet<Headword> getIndex() throws Exception {
        IndexedTreeSet<Headword> index = new IndexedTreeSet<>();

        Path pathInZipFile = zipFileSystem.getPath(ENTRIES);
        Stream<Path> files = Files.list(pathInZipFile);
        files.forEach(new Consumer<Path>() {
            @Override
            public void accept(Path path) {
                String reference = new String(decoder.decode(path.getFileName().toString()), StandardCharsets.UTF_8);
                index.addAll(referenceToHeadwords(reference));
            }
        });

        return index;
    }

    private void updateFullTextIndex(SortedMap<String, String> entries, boolean isDelete) throws Exception {
        SerializableRAMDirectory directory = getFullTextIndex();
        StandardAnalyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(directory, indexWriterConfig);

        if (isDelete) {
            for (Map.Entry<String, String> entry : entries.entrySet()) {
                writer.deleteDocuments(new Term("headword", entry.getKey()));
            }
        } else {
            for (Map.Entry<String, String> entry : entries.entrySet()) {
                Document document = new Document();
                document.add(new TextField("headword", entry.getKey(), Field.Store.YES));
                document.add(new TextField("contents", entry.getValue(), Field.Store.NO));
                writer.addDocument(document);
            }
        }
        writer.close();
        setFullTextIndex(directory);
    }

    protected void setFullTextIndex(SerializableRAMDirectory directory) throws Exception {
        Path pathInZipFile = zipFileSystem.getPath(FT_INDEX);
        Files.deleteIfExists(pathInZipFile);
        Files.write(pathInZipFile, Utils.serializeIndex(directory));
    }

    protected SerializableRAMDirectory getFullTextIndex() throws Exception {
        SerializableRAMDirectory directory = new SerializableRAMDirectory();
        Path pathInZipFile = zipFileSystem.getPath(FT_INDEX);
        if (Files.exists(pathInZipFile)) {
            directory = Utils.deserializeIndex(Files.readAllBytes(pathInZipFile));
        }
        return directory;
    }

    @Override
    public IndexedTreeSet<SearchResult> search(String queryString) throws Exception {
        IndexedTreeSet<SearchResult> searchResults = new IndexedTreeSet<>();
        SerializableRAMDirectory directory = getFullTextIndex();
        StandardAnalyzer analyzer = new StandardAnalyzer();
        Query query = new QueryParser("contents", analyzer).parse(QueryParser.escape(queryString));
        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(indexReader);
        TopDocs topDocs = searcher.search(query, 10);
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            SearchResult searchResult = new SearchResult();
            searchResult.setScore(scoreDoc.score);
            String headword = searcher.doc(scoreDoc.doc).get("headword");
            searchResult.setHeadword(headword);
            String encodedHeadword = encoder.encodeToString(headword.getBytes(StandardCharsets.UTF_8));
            Path pathInZipFile = zipFileSystem.getPath(ENTRIES + encodedHeadword);
            //todo prepare article text for full-text results presentation
            searchResult.setText(new String(Files.readAllBytes(pathInZipFile), StandardCharsets.UTF_8));
            searchResults.add(searchResult);
        }
        return searchResults;
    }

    @Override
    public Properties getProperties() throws Exception {
        Properties properties = new Properties();
        Path pathInZipFile = zipFileSystem.getPath(PROPS);
        if (Files.exists(pathInZipFile)) {
            ByteArrayInputStream bais = new ByteArrayInputStream(Files.readAllBytes(pathInZipFile));
            properties.load(bais);
        }
        return properties;
    }

    @Override
    public void setProperties(Properties properties) throws Exception {
        Path pathInZipFile = zipFileSystem.getPath(PROPS);
        Files.deleteIfExists(pathInZipFile);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        properties.store(baos, "");
        Files.write(pathInZipFile, baos.toByteArray());
    }

    @Override
    public byte[] getIcon() throws Exception {
        Path pathInZipFile = zipFileSystem.getPath(ICON_FILE);
        if (Files.exists(pathInZipFile)) {
            return Files.readAllBytes(pathInZipFile);
        }
        return null;
    }

    @Override
    public void setIcon(byte[] iconBytes) throws Exception {
        Path pathInZipFile = zipFileSystem.getPath(ICON_FILE);
        Files.deleteIfExists(pathInZipFile);
        Files.write(pathInZipFile, iconBytes);

    }

    @Override
    public String toString() {
        try {
            return (String) getProperties().get(DictionaryProperty.NAME.name());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

