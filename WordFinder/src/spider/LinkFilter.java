package spider;

//import TextAbstract.StoreData.EntryAddressProcess;
/**
 * ��ַ��ʽ�������ӿ�
 */
public interface LinkFilter {
	public boolean accept(String url,String entryAddress);
}
