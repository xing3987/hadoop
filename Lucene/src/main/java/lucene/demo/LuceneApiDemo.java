package lucene.demo;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.junit.Before;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * lucene 相关的api操作
 * <p>
 * 辅助工具：Lucene Index ToolBox : https://github.com/DmitryKey/luke/releases 用来查看lucene保存的数据
 */
public class LuceneApiDemo {

    private static final String docPath = "G:\\datas\\lucene";
    //创建和使用标准分词器，一个字分一次
    //private Analyzer analyzer = new StandardAnalyzer();
    private Analyzer analyzer = new IKAnalyzer();
    private FSDirectory fs = null;

    @Before
    public void before() throws IOException {
        fs = FSDirectory.open(Paths.get(docPath));
    }

    @Test
    public void create() throws IOException {
        Article article = new Article();
        article.setId(1l);
        article.setAuthor("张三丰");
        article.setTitle("张三丰重出武林。");
        article.setUrl("http://www.zsf.com");
        article.setContent("张三丰重出武林，武林又将掀起腥风血雨。");

        Article article2 = new Article();
        article2.setId(2l);
        article2.setAuthor("张无忌");
        article2.setTitle("张无忌重出武林。");
        article2.setUrl("http://www.zsf.com/zwj");
        article2.setContent("张无忌重出武林，欲找回失散多年的屠龙宝刀。");


        //写入索引的配置，设置了分词器
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        //创建indexWriter指定写入目录和配置
        IndexWriter indexWriter = new IndexWriter(fs, config);
        //写出document文件到指定目录
        indexWriter.addDocument(article.toDocument());
        indexWriter.addDocument(article2.toDocument());
        //关闭io
        indexWriter.close();
    }

    @Test
    public void search() throws IOException, ParseException {

        //获取文档读取器
        DirectoryReader reader = DirectoryReader.open(fs);
        //索引查询器
        IndexSearcher searcher = new IndexSearcher(reader);

        //查询条件
        String queryStr = "张三丰";
        //创建一个查询条件解析器
        QueryParser parser = new QueryParser("content", analyzer);
        //对查询条件进行解析,查询分隔后的content中含有‘张’or'三'or‘丰’的内容
        Query query = parser.parse(queryStr);
        //TermQuery将查询条件当成是一个固定的词
        //Query query = new TermQuery(new Term("author", "张三丰"));
        //在索引中查询，最初10条
        TopDocs topDocs = searcher.search(query, 10);

        //获取查询到的文档id和得分，注意这里的文档id不是指定义article中的ID属性，而是一个doc的唯一标识
        ScoreDoc[] docs = topDocs.scoreDocs;
        for (ScoreDoc soreDoc : docs) {
            //从索引中查询到文档的id
            int docid = soreDoc.doc;
            //根据id到文档中查找文档的内容
            Document doc = searcher.doc(docid);
            //将doc转换成Article
            Article article = Article.parseArticle(doc);
            System.out.println(article);
        }

        reader.close();
    }

    @Test
    public void delete() throws IOException, ParseException {
        //写入索引的配置，设置了分词器
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        //创建indexWriter指定写入目录和配置
        IndexWriter indexWriter = new IndexWriter(fs, config);

        //Term词条查找，内容必须完全匹配，不分词
        //indexWriter.deleteDocuments(new Term("content","无"));

        QueryParser parser = new QueryParser("title", analyzer);
        Query query = parser.parse("三");

        //LongPoint是建立索引的(范围删除)
        //Query query = LongPoint.newRangeQuery("id", 0L, 10L);
        //删除指定LongPoint属性的数据
        //Query query= LongPoint.newExactQuery("id",2l);

        indexWriter.deleteDocuments(query);
        indexWriter.commit();
        indexWriter.close();
    }

    /**
     * lucene的update比较特殊，update的代价太高，先删除，然后在插入
     *
     * @throws IOException
     * @throws ParseException
     */
    @Test
    public void update() throws IOException, ParseException {
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(fs, indexWriterConfig);

        Article article = new Article();
        article.setId(1L);
        article.setAuthor("老张同学");
        article.setTitle("重返校园");
        article.setContent("老张同学，百岁诞辰，重返校园，迎娶白富美，走上人生巅峰！！！");
        article.setUrl("http://www.edu360.cn/a111");

        indexWriter.updateDocument(new Term("author", "张三丰"), article.toDocument());
        indexWriter.commit();
        indexWriter.close();
    }

    /**
     * 可以从多个字段中查找
     *
     * @throws IOException
     * @throws ParseException
     */
    @Test
    public void testMultiField() throws IOException, ParseException {

        DirectoryReader directoryReader = DirectoryReader.open(fs);
        IndexSearcher indexSearcher = new IndexSearcher(directoryReader);

        String[] fields = {"title", "content"};
        //多字段的查询转换器
        MultiFieldQueryParser queryParser = new MultiFieldQueryParser(fields, analyzer);
        Query query = queryParser.parse("老师");

        TopDocs topDocs = indexSearcher.search(query, 10);
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            int doc = scoreDoc.doc;
            Document document = indexSearcher.doc(doc);
            Article article = Article.parseArticle(document);
            System.out.println(article);
        }

        directoryReader.close();
    }

    /**
     * 查找全部的数据
     *
     * @throws IOException
     * @throws ParseException
     */
    @Test
    public void testMatchAll() throws IOException, ParseException {

        DirectoryReader directoryReader = DirectoryReader.open(fs);
        IndexSearcher indexSearcher = new IndexSearcher(directoryReader);

        Query query = new MatchAllDocsQuery();

        TopDocs topDocs = indexSearcher.search(query, 10);
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            int doc = scoreDoc.doc;
            Document document = indexSearcher.doc(doc);
            Article article = Article.parseArticle(document);
            System.out.println(article);
        }

        directoryReader.close();
    }

    /**
     * 布尔查询，可以组合多个查询条件
     *
     * @throws Exception
     */
    @Test
    public void testBooleanQuery() throws Exception {
        DirectoryReader directoryReader = DirectoryReader.open(fs);
        IndexSearcher indexSearcher = new IndexSearcher(directoryReader);

        Query query1 = new TermQuery(new Term("title", "张"));
        Query query2 = new TermQuery(new Term("content", "老"));
        //BooleanClause bc1 = new BooleanClause(query1, BooleanClause.Occur.SHOULD); 或者
        BooleanClause bc1 = new BooleanClause(query1, BooleanClause.Occur.MUST);
        BooleanClause bc2 = new BooleanClause(query2, BooleanClause.Occur.MUST_NOT);
        BooleanQuery boolQuery = new BooleanQuery.Builder().add(bc1).add(bc2).build();
        System.out.println(boolQuery);

        TopDocs topDocs = indexSearcher.search(boolQuery, 10);
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            int doc = scoreDoc.doc;
            Document document = indexSearcher.doc(doc);
            Article article = Article.parseArticle(document);
            System.out.println(article);
        }

        directoryReader.close();
    }

    @Test
    public void testQueryParser() throws Exception {

        DirectoryReader directoryReader = DirectoryReader.open(fs);
        IndexSearcher indexSearcher = new IndexSearcher(directoryReader);

        //创建一个QueryParser对象。参数1：默认搜索域 参数2：分析器对象。
        QueryParser queryParser = new QueryParser("title", analyzer);

        //使用AND 或者OR条件，注意要是大写
        Query query = queryParser.parse("title:无 OR title:三");
        //Query query = queryParser.parse("title:无 AND title:三");
        System.out.println(query);

        TopDocs topDocs = indexSearcher.search(query, 10);
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            int doc = scoreDoc.doc;
            Document document = indexSearcher.doc(doc);
            Article article = Article.parseArticle(document);
            System.out.println(article);
        }

        directoryReader.close();
    }
}






























