package spider;

//import TextAbstract.StoreData.EntryAddressProcess;
/**
 * 地址格式过滤器接口
 */
public interface LinkFilter {
	public boolean accept(String url,String entryAddress);
}
