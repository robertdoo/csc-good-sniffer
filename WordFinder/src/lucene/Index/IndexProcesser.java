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
	 * ��һ��������µ�������
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
			//û��ʱ�����Ϊ����ʱ��
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
			System.out.println("�Ѹ���������" + result.getTitle());
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ����һ�н��
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
				//û��ʱ�����Ϊ����ʱ��
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
			System.out.println("�Ѹ���������" + results.size() + "����¼!");
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*ԭ�����������ķ���
	public void createIndex(){//��������
		ResultSet rs = null;
		String sqlstr, filePath, fileDetail[] = null;
		
		sqlstr = "select * from file";
		 try{
			 IndexWriter iw = new IndexWriter(IndexDir, analyzer, true,
						IndexWriter.MaxFieldLength.UNLIMITED);
			 iw.setMergeFactor(10);
			 iw.setMaxMergeDocs(3000);
			 
			 System.out.println("���ڽ�����������");
			 Document doc = null;
			 rs = connDB.executeQuery(sqlstr);
			 if(rs!=null){
				 rs.last();
				 System.out.println("���ݿ�����Ҫ�������ļ�����Ϊ��"+rs.getRow()+"���������̫�������ĵȴ�����");
			 }
			 rs.beforeFirst();
			 while(rs.next()){
				doc = new Document();
				
				filePath = rs.getString("localPath");
				fileDetail = getFileDetail(filePath);
				if(fileDetail==null){
					System.out.println("!!!!!!!!!!!!!�Ҳ����ļ���"+fileDetail+"!!!!!!!!!!!!!!!!!!");
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
			 
			 //iw.optimize();//�����Ż�
			 iw.close(); 
			 System.out.println("==========================�����������!==============================");
		 } catch(IOException e){
			 e.printStackTrace();
		 } catch(Exception e){
			 e.printStackTrace();
		 }
	}
	
	public void updateIndex(Timestamp beginTime, Timestamp endTime){//��������
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
			 
			 System.out.println("���ڸ�����������");
			 Document doc = null;
			 rs = connDB.executeQuery(sqlstr);
			 count = 0;
			 while(rs.next()){
				count++;
				doc = new Document();
				
				filePath = rs.getString("localPath");
				fileDetail = getFileDetail(filePath);
				if(fileDetail==null){
					//System.out.println("!!!!!!!!!!!!!�Ҳ����ļ���"+fileDetail+"!!!!!!!!!!!!!!!!!!");
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
			 
			 //iw.optimize();//�����Ż�
			 iw.close(); 
			 System.out.println("==============�����������!���µ�ʱ��������"+beginTime+"----"+endTime+"====================");
			 System.out.println("���θ����ļ�����"+count+"��");
		 } catch(IOException e){
			 e.printStackTrace();
		 } catch(Exception e){
			 e.printStackTrace();
		 }
	}
	
	//����txt�ļ�
	public String[] getFileDetail(String path){
		String type, title, url, time, pageMirrorPath, line;
		StringBuffer content = new StringBuffer();
		
		File file = new File(path);
		if(!file.exists()){
			System.out.println(file.getName()+"�����ڣ�");
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
			
			if((type = br.readLine())==null)//��һ�����ļ�����
				type = "null";
			else 
				type = type.trim();
			
			if((url = br.readLine())==null)//��һ��������
				url = "null";
				
			if((title = br.readLine())==null)//��һ���Ǳ���
				title = "null";
			else
				title = FilePreprocess.replace(title).replace("��", "").replace(" ", "");//ȫ���ת����ȥ���ո���Ʊ��

			if((time = br.readLine())==null)//��һ����ʱ��
				time = "null";
			
			if((pageMirrorPath = br.readLine())==null)//��һ������ҳ���յı���·��
				pageMirrorPath = "null";
			
			while((line=br.readLine())!=null){//�����ȫ������
				line = line.replace("��", "").replace(" ", "");
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
		contentString = FilePreprocess.replace(contentString);//ȫ���ת��
		contentString = contentString.trim();//ȥ���ո���Ʊ��

		String[] detail = new String[6];
		detail[0] = type;
		detail[1] = title;
		detail[2] = contentString;
		detail[3] = url;
		detail[4] = time;
		detail[5] = pageMirrorPath;
		
		return detail;
	}
	
	//����ؼ���word�ĵ�������
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
				System.out.println(reader.document(termPositions.doc()).get("title")+"��");
				//System.out.println(reader.document(termPositions.doc()).get("url")+"��");
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
