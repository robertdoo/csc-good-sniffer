package lucene.Search;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.CachingWrapperFilter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RangeFilter;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocCollector;
import org.apache.lucene.search.TopDocs;
import org.wltea.analyzer.lucene.IKAnalyzer;
import org.wltea.analyzer.lucene.IKQueryParser;
import org.wltea.analyzer.lucene.IKSimilarity;

import spider.SpiderConf;
import dataStruct.Result;
import dataStruct.Rule;
import dataStruct.SearchResult;

public class SearchProcesser {

	private final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	// private Query titleQuery = null;
	// private Query contentQuery = null;
	private Analyzer analyzer = new IKAnalyzer();

	private final String indexDir1 = SpiderConf.getPathByName(SpiderConf.SPIDER_INDEX_PATH);
	private final String indexDir2 = SpiderConf.getPathByName(SpiderConf.MAINPAGECRAWLER_INDEX_PATH);
	private final String indexDir3 = SpiderConf.getPathByName(SpiderConf.METASEARCH_INDEX_PATH);
	private String indexDir;

	/*
	private Result result;
	private LinkedList<Result> search_results = new LinkedList<Result>();
	private SearchResult searchResult = null;
	*/
	
	private IndexSearcher searcher;
	private TopDocCollector topCollector;
	private TopDocs topDocs;
	private ScoreDoc[] scoreDocs;
	private Document doc;
	
	private String typeDoc, titleDoc, contentDoc, urlDoc, timeDoc, 
	       pageMirrorPath, source, type;
	private Date begin, end;
	private long sTime;
	private SimpleDateFormat format = new SimpleDateFormat(TIME_FORMAT);
	
	//����ʵʱ��صĽ����
	public LinkedList<Result> monitorNewResults = new LinkedList<Result>();
	private HashMap<Integer, Boolean> resultCache = new HashMap<Integer, Boolean>();
	
	public SearchProcesser(int chose) {
		if (chose == SpiderConf.DEEP_CRAWLER)
			indexDir = indexDir1;
		else if (chose == SpiderConf.MAINPAGE_CRAWLER)
			indexDir = indexDir2;
		else
			indexDir = indexDir3;
		
	}

	/*
	 * public SearchProcesser(String indexDir) { this.indexDir = indexDir; }
	 */

	// �õ�����������������Ҫ��ʾ�ĸ�����Ϣ����һ��SearchResult����
	public synchronized SearchResult search(Rule rule) {
		Result result;
		LinkedList<Result> search_results = new LinkedList<Result>();
		SearchResult searchResult = null;
		
		if (!(new File(indexDir).exists())
				|| (new File(indexDir).listFiles().length == 0)) {
			System.out.println("�����ļ��в����ڻ��ļ���Ϊ��~��");
			return searchResult;
		}

		type = rule.getFileType();
		search_results.clear();
		
		try {
			searcher = new IndexSearcher(indexDir);
			// �����ѯ������������ʾ10000�������¼
			topCollector = new TopDocCollector(10000);
			// ʹ��IKAnalyzer��IKSimilarity���ƶ�������
			searcher.setSimilarity(new IKSimilarity());
			
			BooleanQuery bQuery = (BooleanQuery) parseRuleToQuery(rule);
			begin = new Date();
			searcher.search(bQuery, topCollector);
			end = new Date();
			sTime = (end.getTime() - begin.getTime());
			// ���ظ������ֻ��ƵĽ��������
			scoreDocs = topCollector.topDocs().scoreDocs;

			for (ScoreDoc scoreDoc : scoreDocs) {
				doc = searcher.doc(scoreDoc.doc);

				typeDoc = doc.get("filetype");
				if (!type.equals("�κθ�ʽ") && !type.equalsIgnoreCase(typeDoc))
					continue;
				titleDoc = doc.get("title");
				contentDoc = doc.get("content");
				urlDoc = doc.get("url");
				timeDoc = doc.get("time");
				pageMirrorPath = doc.get("pageMirrorPath");
				source = doc.get("source");

				result = new Result();
				// �ļ�����
				result.setFiletype(typeDoc);
				// ����
				result.setTitle(titleDoc);
				// ����
				result.setContent(contentDoc);
				// ����
				result.setUrl(urlDoc);
				// ʱ��,ת��ΪDate����
				Date datetime = new Date();
				try {
					datetime = format.parse(timeDoc);
				} catch (ParseException e) {
					// System.out.println(timeDoc + "ת��ʧ�ܣ�");
				}
				result.setTime(datetime);
				// ��ҳ���յ�·��
				result.setPageMirrorPath(pageMirrorPath);
				// ��Դ
				result.setSource(source);

				search_results.add(result);
			}
			System.out.println("�����" + search_results.size() + "����ѯ�����");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		searchResult = new SearchResult(sTime, rule, search_results);
		return searchResult;
	}
	
	public synchronized SearchResult search(Rule rule, String beginTime,
			String endTime) {
		Result result;
		LinkedList<Result> search_results = new LinkedList<Result>();
		SearchResult searchResult = null;
		
		if (!(new File(indexDir).exists())
				|| (new File(indexDir).listFiles().length == 0)) {
			System.out.println("�����ļ��в����ڻ��ļ���Ϊ��~��");
			return searchResult;
		}

		type = rule.getFileType();
		search_results.clear();

		try {
			searcher = new IndexSearcher(indexDir);
			// �����ѯ������������ʾ10000�������¼
			topCollector = new TopDocCollector(10000);
			// ʹ��IKAnalyzer��IKSimilarity���ƶ�������
			searcher.setSimilarity(new IKSimilarity());
			
			BooleanQuery bQuery = (BooleanQuery) parseRuleToQuery(rule);
			
			/*
			 * ����RangeQuery��BooleanClause���������ƣ���ʱû���ҵ�����취��
			 * ��������RangeFilter����
			BooleanQuery bQuery = new BooleanQuery();
			bQuery.add(parseRuleToQuery(rule), BooleanClause.Occur.MUST);
			Term t1 = new Term("time", bTime);
			Term t2 = new Term("time", eTime);
			RangeQuery timeRangeQuery = new RangeQuery(t1, t2, true);
			bQuery.add(timeRangeQuery, BooleanClause.Occur.MUST);
			 */
			
			// ʱ�䷶Χ������
			RangeFilter filter = new RangeFilter("time",
					                             beginTime + " 00:00:00",
					                             endTime + " 23:59:59",
					                             true, true);
			CachingWrapperFilter cwFilter = new CachingWrapperFilter(filter);

			// �����ʱ�併������
			Sort sort = new Sort();
			sort.setSort(new SortField("time", SortField.STRING, true));

			begin = new Date();
			// ����
			topDocs = searcher.search(bQuery, cwFilter, 1000, sort);
			scoreDocs = topDocs.scoreDocs;
			end = new Date();
			sTime = (end.getTime() - begin.getTime());

			for (ScoreDoc scoreDoc : scoreDocs) {
				doc = searcher.doc(scoreDoc.doc);

				typeDoc = doc.get("filetype");
				if (!type.equals("�κθ�ʽ") && !type.equalsIgnoreCase(typeDoc))
					continue;
				titleDoc = doc.get("title");
				contentDoc = doc.get("content");
				urlDoc = doc.get("url");
				timeDoc = doc.get("time");
				pageMirrorPath = doc.get("pageMirrorPath");
				source = doc.get("source");

				result = new Result();
				// �ļ�����
				result.setFiletype(typeDoc);
				// ����
				result.setTitle(titleDoc);
				// ����
				result.setContent(contentDoc);
				// ����
				result.setUrl(urlDoc);
				// ʱ��,ת��ΪDate����
				Date datetime = new Date();
				try {
					datetime = format.parse(timeDoc);
				} catch (ParseException e) {
					// System.out.println(timeDoc + "ת��ʧ�ܣ�");
				}
				result.setTime(datetime);
				// ��ҳ���յ�·��
				result.setPageMirrorPath(pageMirrorPath);
				// ��Դ
				result.setSource(source);

				search_results.add(result);
			}
			System.out.println("�����" + search_results.size() + "����ѯ�����");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		searchResult = new SearchResult(sTime, rule, search_results);
		return searchResult;
	}

	// ר������ʵʱ��ص���������
	public synchronized void monitorSearch(Rule rule) {
		Result result;
		int hashCode;
		monitorNewResults.clear();	
		
		if (!(new File(indexDir).exists())
				|| (new File(indexDir).listFiles().length == 0)) {
			System.out.println("�����ļ��в����ڻ��ļ���Ϊ��~��");
			return;
		}

		try {
			searcher = new IndexSearcher(indexDir);
			// �����ѯ������������ʾ10000�������¼
			topCollector = new TopDocCollector(10000);
			// ʹ��IKAnalyzer��IKSimilarity���ƶ�������
			searcher.setSimilarity(new IKSimilarity());
			
			BooleanQuery bQuery = (BooleanQuery) parseRuleToQuery(rule);
			searcher.search(bQuery, topCollector);

			// ���ظ������ֻ��ƵĽ��������
			scoreDocs = topCollector.topDocs().scoreDocs;

			for (ScoreDoc scoreDoc : scoreDocs) {
				doc = searcher.doc(scoreDoc.doc);

				hashCode = (doc.get("title") + doc.get("url")).hashCode();
				if(resultCache.get(hashCode) == null){
					resultCache.put(hashCode, true);
					try {
						result = new Result(doc.get("title"),// ����
								doc.get("content"),// ����
								doc.get("url"),// ����
								format.parse(doc.get("time")),// ʱ��,ת��ΪDate����
								doc.get("filetype"),// �ļ�����
								doc.get("pageMirrorPath"),// ��ҳ���յ�·��
								doc.get("source")// ��Դ
						);
					} catch (ParseException e) {
						result = new Result(doc.get("title"),// ����
								doc.get("content"),// ����
								doc.get("url"),// ����
								new Date(),// ʱ��,ʹ������ʱ��
								doc.get("filetype"),// �ļ�����
								doc.get("pageMirrorPath"),// ��ҳ���յ�·��
								doc.get("source")// ��Դ
						);
					}	
					monitorNewResults.add(result);
				}		
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getContentByTitle(String title) {

		if (!(new File(indexDir).exists())
				|| (new File(indexDir).listFiles().length == 0)) {
			System.out.println("�����ļ��в����ڻ��ļ���Ϊ��~��");
			return null;
		}

		String content = "";

		try {

			Query query = IKQueryParser.parse("title", title);
			// System.out.println(query.toString());

			TopDocCollector topCollector = new TopDocCollector(1000);

			IndexSearcher searcher = new IndexSearcher(indexDir);

			searcher.setSimilarity(new IKSimilarity());

			searcher.search(query, topCollector);

			ScoreDoc[] scoreDocs = topCollector.topDocs().scoreDocs;

			// System.out.println(scoreDocs.length);

			// System.out.println(query.toString());

			Document doc = null;

			for (ScoreDoc scoreDoc : scoreDocs) {
				doc = searcher.doc(scoreDoc.doc);
				content = doc.get("content");
				System.out.println(doc.get("title"));
				if (doc.get("title").equals(title))
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;

	}

	// �����ؼ���
	public String parseKeyword(String mustWord, String fullWord, String notWord) {
		String queryStr = "";
		String[] mustWords = mustWord.split(" ");
		for (String str : mustWords) {
			if (str.startsWith("+"))
				queryStr += " + " + str.substring(1);
			else
				queryStr += " " + str;
		}

		String[] fullWords = fullWord.split(" ");
		for (String str : fullWords) {
			if (str.length() == 0)
				continue;
			if (str.startsWith("+"))
				queryStr += " + " + "\"" + str.substring(1) + "\"";
			else
				queryStr += "\"" + str + "\"";
		}

		String[] notWords = notWord.split(" ");
		for (String str : notWords) {
			if (str.length() == 0)
				continue;
			if (str.startsWith("+"))
				queryStr += " - " + str.substring(1);
			else
				queryStr += " - " + str;
		}
		/*
		 * String notWordParse; if(mustWord.startsWith("+"))mustWord =
		 * " "+mustWord.substring(1); mustWord.replaceAll(" +", " ");
		 * if(mustWord.trim().length()!=0) queryStr = queryStr + mustWord +
		 * " ";//Ҫ�����Ĺؼ���
		 * 
		 * if(fullWord.trim().length()!=0){ if(mustWord.trim().length()!=0)
		 * queryStr = queryStr + "+\"" + fullWord + "\"" + " ";//Ҫ��ȫ�����Ĺؼ��� else
		 * queryStr = queryStr + "\"" + fullWord + "\"" + " "; }
		 * 
		 * notWord.replaceAll(" -", " "); if(notWord.trim().length()!=0){
		 * notWordParse = "-"; for(int i=0;i<notWord.length();i++){
		 * if(notWord.charAt(i)==' ') notWordParse += " -"; else notWordParse +=
		 * notWord.charAt(i); } queryStr = queryStr + notWordParse +
		 * " ";//�������Ĺؼ��� }
		 */

		return queryStr;
	}

	public Query parseRuleToQuery(Rule rule) throws Exception {
		BooleanQuery bQuery = new BooleanQuery();
		QueryParser titleParser = new QueryParser("title", analyzer);
		QueryParser contentParser = new QueryParser("content", analyzer);

		Query titleMustWordQuery = null;
		Query contentMustWordQuery = null;
		Query titleFullWordQuery = null;
		Query contentFullWordQuery = null;
		Query titleNotWordQuery = null;
		Query contentNotWordQuery = null;
		// TermQuery fileTypeQuery = null;

		String mustWord = rule.getWords();
		String fullWord = rule.getSnippet();
		String notWord = rule.getUnwantedWords();
		String fileType = rule.getFileType();

		String[] mustWords = mustWord.split(" ");
		String[] fullWords = fullWord.split(" ");
		String[] notWords = notWord.split(" ");

		String queryStr = "";
		for (String str : mustWords) {
			if (str.startsWith("+"))
				queryStr += " + " + str.substring(1);
			else
				queryStr += " " + str;
		}
		if (queryStr.length() != 0) {
			titleMustWordQuery = titleParser.parse(queryStr);
			contentMustWordQuery = contentParser.parse(queryStr);
		}

		queryStr = "";
		for (String str : fullWords) {
			if (str.length() == 0)
				continue;
			if (str.startsWith("+"))
				queryStr += " + " + "\"" + str.substring(1) + "\"";
			else
				queryStr += "\"" + str + "\"";
		}
		if (queryStr.length() != 0) {
			titleFullWordQuery = titleParser.parse(queryStr);
			contentFullWordQuery = contentParser.parse(queryStr);
		}

		queryStr = "";
		for (String str : notWords) {
			if (str.length() == 0)
				continue;
			if (str.startsWith("+"))
				queryStr += " " + str.substring(1);
			else
				queryStr += " " + str;
		}
		if (queryStr.length() != 0) {
			titleNotWordQuery = titleParser.parse(queryStr);
			contentNotWordQuery = contentParser.parse(queryStr);
		}

		fileType = fileType.equals("�κθ�ʽ") ? "" : fileType;
		// if(fileType.length()!=0)fileTypeQuery = new TermQuery(new
		// Term("filetype",fileType));

		if (titleMustWordQuery != null) {
			bQuery.add(titleMustWordQuery, BooleanClause.Occur.SHOULD);
			bQuery.add(contentMustWordQuery, BooleanClause.Occur.SHOULD);
		}
		if (titleFullWordQuery != null) {
			bQuery.add(titleFullWordQuery, BooleanClause.Occur.SHOULD);
			bQuery.add(contentFullWordQuery, BooleanClause.Occur.SHOULD);
		}
		if (titleNotWordQuery != null) {
			bQuery.add(titleNotWordQuery, BooleanClause.Occur.MUST_NOT);
			bQuery.add(contentNotWordQuery, BooleanClause.Occur.MUST_NOT);
		}
		/*
		 * if(fileTypeQuery!=null){ bQuery.add(fileTypeQuery,
		 * BooleanClause.Occur.SHOULD); }
		 */
		System.out.println("��ѯ�ִ�Ϊ��" + bQuery.toString());
		return bQuery;
	}
}
