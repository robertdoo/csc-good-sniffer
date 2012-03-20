package lucene.Index;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.LockObtainFailedException;
import org.wltea.analyzer.lucene.IKAnalyzer;

import spider.SpiderConf;
import dataStruct.Result;

public class IndexProcesser {
	
	private final String indexDir1 = "FileIndex/index1";
	private final String indexDir2 = "FileIndex/index2";
	private final String indexDir3 = "FileIndex/index3";

	private String IndexDir;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private Analyzer analyzer = new IKAnalyzer();
	
	public IndexProcesser(int chose) {
		if(chose==SpiderConf.DEEP_CRAWLER){
			IndexDir = indexDir1;
		}
		else if(chose==SpiderConf.MAINPAGE_CRAWLER){
			IndexDir = indexDir2;
		}
		else{
			IndexDir = indexDir3;
		}
	}
	
	/**
	 * 把一条结果更新到索引中
	 */
	public synchronized void updateIndexByResult(Result result) {
		if (result == null)
			return;

		IndexWriter iw;
		try {
			File[] dir = new File(IndexDir).listFiles();
			if(dir.length == 0)
				iw = new IndexWriter(IndexDir, analyzer, true,
						IndexWriter.MaxFieldLength.UNLIMITED);
			else
				iw = new IndexWriter(IndexDir, analyzer, false,
						IndexWriter.MaxFieldLength.UNLIMITED);
			dir = null;
			iw.setMergeFactor(10);
			iw.setMaxMergeDocs(3000);

			Document doc = new Document();

			Field type = new Field("filetype", result.getFiletype(),
					Field.Store.YES, Field.Index.NOT_ANALYZED);
			Field title = new Field("title", result.getTitle(),
					Field.Store.YES, Field.Index.ANALYZED);
			Field content = new Field("content", result.getContent(),
					Field.Store.YES, Field.Index.ANALYZED);
			Field url = new Field("url", result.getUrl(),
					Field.Store.YES, Field.Index.NO);
			//没有时间的置为现在时刻
			if(result.getTime() == null)
				result.setTime(new Date());
			Field time = new Field("time", sdf.format(result.getTime()),
					Field.Store.YES, Field.Index.NOT_ANALYZED);
			Field pageMirrorPath = new Field("pageMirrorPath", result.getPageMirrorPath(),
					Field.Store.YES, Field.Index.NO);
			Field source = new Field("source", result.getSource(),
					Field.Store.YES, Field.Index.NOT_ANALYZED);

			doc.add(type);
			doc.add(title);
			doc.add(content);
			doc.add(url);
			doc.add(time);
			doc.add(pageMirrorPath);
			doc.add(source);

			iw.addDocument(doc);

			iw.close();
			System.out.println("已更新索引：" + result.getTitle());
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 更新一列结果
	 */
	public synchronized void updateIndexByResultList(LinkedList<Result> results){
		if (results == null)
			return;

		IndexWriter iw;
		try {
			File[] dir = new File(IndexDir).listFiles();
			if(dir.length == 0)
				iw = new IndexWriter(IndexDir, analyzer, true,
						IndexWriter.MaxFieldLength.UNLIMITED);
			else
				iw = new IndexWriter(IndexDir, analyzer, false,
						IndexWriter.MaxFieldLength.UNLIMITED);
			dir = null;
			iw.setMergeFactor(10);
			iw.setMaxMergeDocs(3000);

			for(Result r : results){
				Document doc = new Document();

				Field type = new Field("filetype", r.getFiletype(),
						Field.Store.YES, Field.Index.NOT_ANALYZED);
				Field title = new Field("title", r.getTitle(),
						Field.Store.YES, Field.Index.ANALYZED);
				Field content = new Field("content", r.getContent(),
						Field.Store.YES, Field.Index.ANALYZED);
				Field url = new Field("url", r.getUrl(),
						Field.Store.YES, Field.Index.NO);
				//没有时间的置为现在时刻
				if(r.getTime() == null)
					r.setTime(new Date());
				Field time = new Field("time", sdf.format(r.getTime()),
						Field.Store.YES, Field.Index.NOT_ANALYZED);
				Field pageMirrorPath = new Field("pageMirrorPath", r.getPageMirrorPath(),
						Field.Store.YES, Field.Index.NO);
				Field source = new Field("source", r.getSource(),
						Field.Store.YES, Field.Index.NOT_ANALYZED);

				doc.add(type);
				doc.add(title);
				doc.add(content);
				doc.add(url);
				doc.add(time);
				doc.add(pageMirrorPath);
				doc.add(source);

				iw.addDocument(doc);
			}

			iw.close();
			System.out.println("已更新索引：" + results.size() + "条记录!");
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*原来建立索引的方法
	public void createIndex(){//建立索引
		ResultSet rs = null;
		String sqlstr, filePath, fileDetail[] = null;
		
		sqlstr = "select * from file";
		 try{
			 IndexWriter iw = new IndexWriter(IndexDir, analyzer, true,
						IndexWriter.MaxFieldLength.UNLIMITED);
			 iw.setMergeFactor(10);
			 iw.setMaxMergeDocs(3000);
			 
			 System.out.println("正在建立索引……");
			 Document doc = null;
			 rs = connDB.executeQuery(sqlstr);
			 if(rs!=null){
				 rs.last();
				 System.out.println("数据库中需要索引的文件数量为："+rs.getRow()+"，如果数量太大请耐心等待……");
			 }
			 rs.beforeFirst();
			 while(rs.next()){
				doc = new Document();
				
				filePath = rs.getString("localPath");
				fileDetail = getFileDetail(filePath);
				if(fileDetail==null){
					System.out.println("!!!!!!!!!!!!!找不到文件："+fileDetail+"!!!!!!!!!!!!!!!!!!");
					continue;
				}
				
				Field type = new Field("filetype",fileDetail[0],Field.Store.YES,Field.Index.NOT_ANALYZED);
				Field title = new Field("title",fileDetail[1],Field.Store.YES,Field.Index.ANALYZED);
				Field content = new Field("content",fileDetail[2],Field.Store.YES,Field.Index.ANALYZED);
				Field url = new Field("url",fileDetail[3],Field.Store.YES,Field.Index.NO);
				Field time = new Field("time",fileDetail[4],Field.Store.YES,Field.Index.NOT_ANALYZED);
				Field pageMirrorPath = new Field("pageMirrorPath",fileDetail[5],Field.Store.YES,Field.Index.NO);
				
				doc.add(type);
				doc.add(title);
				doc.add(content);
				doc.add(url);
				doc.add(time);
				doc.add(pageMirrorPath);
				
				iw.addDocument(doc);
			 }
			 
			 //iw.optimize();//索引优化
			 iw.close(); 
			 System.out.println("==========================索引建立完毕!==============================");
		 } catch(IOException e){
			 e.printStackTrace();
		 } catch(Exception e){
			 e.printStackTrace();
		 }
	}
	
	public void updateIndex(Timestamp beginTime, Timestamp endTime){//更新索引
		ResultSet rs = null;
		String sqlstr, filePath, fileDetail[] = null;
		int count;
		
		sqlstr = "select * from file where storeTime >= '" + beginTime
		         + "' and storeTime <'" + endTime +"'";
		 try{
			 IndexWriter iw = new IndexWriter(IndexDir, analyzer, false,
						IndexWriter.MaxFieldLength.UNLIMITED);
			 iw.setMergeFactor(10);
			 iw.setMaxMergeDocs(3000);
			 
			 System.out.println("正在更新索引……");
			 Document doc = null;
			 rs = connDB.executeQuery(sqlstr);
			 count = 0;
			 while(rs.next()){
				count++;
				doc = new Document();
				
				filePath = rs.getString("localPath");
				fileDetail = getFileDetail(filePath);
				if(fileDetail==null){
					//System.out.println("!!!!!!!!!!!!!找不到文件："+fileDetail+"!!!!!!!!!!!!!!!!!!");
					continue;
				}
				
				Field type = new Field("filetype",fileDetail[0],Field.Store.YES,Field.Index.NOT_ANALYZED);
				Field title = new Field("title",fileDetail[1],Field.Store.YES,Field.Index.ANALYZED);
				Field content = new Field("content",fileDetail[2],Field.Store.YES,Field.Index.ANALYZED);
				Field url = new Field("url",fileDetail[3],Field.Store.YES,Field.Index.NO);
				Field time = new Field("time",fileDetail[4],Field.Store.YES,Field.Index.NOT_ANALYZED);
				Field pageMirrorPath = new Field("pageMirrorPath",fileDetail[5],Field.Store.YES,Field.Index.NO);
				
				doc.add(type);
				doc.add(title);
				doc.add(content);
				doc.add(url);
				doc.add(time);
				doc.add(pageMirrorPath);
				
				iw.addDocument(doc);
			 }
			 
			 //iw.optimize();//索引优化
			 iw.close(); 
			 System.out.println("==============索引更新完毕!更新的时间区间是"+beginTime+"----"+endTime+"====================");
			 System.out.println("本次更新文件数："+count+"个");
		 } catch(IOException e){
			 e.printStackTrace();
		 } catch(Exception e){
			 e.printStackTrace();
		 }
	}
	
	//处理txt文件
	public String[] getFileDetail(String path){
		String type, title, url, time, pageMirrorPath, line;
		StringBuffer content = new StringBuffer();
		
		File file = new File(path);
		if(!file.exists()){
			System.out.println(file.getName()+"不存在！");
			return null;
		}
		
		BufferedReader br = null;
		type = "";
		title = "";
		url = "";
		time = "";
		pageMirrorPath = "";
		try {
			br = new BufferedReader(new FileReader(file));
			
			if((type = br.readLine())==null)//第一行是文件类型
				type = "null";
			else 
				type = type.trim();
			
			if((url = br.readLine())==null)//下一行是链接
				url = "null";
				
			if((title = br.readLine())==null)//下一行是标题
				title = "null";
			else
				title = FilePreprocess.replace(title).replace("　", "").replace(" ", "");//全半角转换，去除空格和制表符

			if((time = br.readLine())==null)//下一行是时间
				time = "null";
			
			if((pageMirrorPath = br.readLine())==null)//下一行是网页快照的本地路径
				pageMirrorPath = "null";
			
			while((line=br.readLine())!=null){//后面的全是内容
				line = line.replace("　", "").replace(" ", "");
				content.append(line);
				if(content.length()>CONTENT_MAX_LENGTH)
					break;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String contentString = content.toString();
		contentString = FilePreprocess.replace(contentString);//全半角转换
		contentString = contentString.trim();//去除空格和制表符

		String[] detail = new String[6];
		detail[0] = type;
		detail[1] = title;
		detail[2] = contentString;
		detail[3] = url;
		detail[4] = time;
		detail[5] = pageMirrorPath;
		
		return detail;
	}
	
	//输出关键词word的倒排索引
	public void getIndexinfo(String field,String word){
		
		try {
			IndexReader reader = IndexReader.open(IndexDir);
			Term term = new Term(field,word);
			TermPositions termPositions = reader.termPositions();
			termPositions.seek(term);
			for(int i=0;i<reader.numDocs();i++){
				System.out.println(reader.document(i));
			}
			while (termPositions.next()) {
				System.out.println(reader.document(termPositions.doc()).get("title")+"：");
				//System.out.println(reader.document(termPositions.doc()).get("url")+"：");
				System.out.print(termPositions.doc());
				int freq = termPositions.freq();
				System.out.print("["+freq+"] ");
				for (int i = 0; i < freq; i++)
					System.out.print(termPositions.nextPosition()+" ");
				System.out.println();
			}
			
			System.out.println(reader.numDocs());
			reader.deleteDocuments(term);
			System.out.println(reader.numDocs());
			reader.undeleteAll();
			System.out.println(reader.numDocs());
					
			reader.close();	
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	*/
	
}
