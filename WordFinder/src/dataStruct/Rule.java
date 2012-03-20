package dataStruct;

public class Rule {
	private String	name;
	private String	language;
	private String	words;
	private String	snippet;
	private String	unwantedWords;
	private String	fileType;
	private String	site;
	private String	cnt;
	private String ruleMapping;
	private String  categoryName;
	private boolean isRelative;
	private String sysnonymWords;
	
	public Rule(String name, String language, String words, String snippet,
			String unwantedWords, String fileType, String site, String ruleMapping,
			String categoryName, boolean isRelative, String sysnonymWords)
	{
		this.name = name;
		this.language = language;
		this.words = words;
		this.snippet = snippet;
		this.unwantedWords = unwantedWords;
		this.fileType = fileType;
		this.site = site;
		this.ruleMapping = ruleMapping;
		this.categoryName = categoryName;
		this.isRelative = isRelative;
		this.sysnonymWords = sysnonymWords;
	}
	
	public Rule(String name, String language, String words, String snippet,
			String unwantedWords, String fileType, String site, String cnt,
			String ruleMapping, String categoryName, boolean isRelative,
			String sysnonymWords)
	{
		this(name, language, words, snippet, unwantedWords, fileType, site,
				ruleMapping, categoryName, isRelative, sysnonymWords);
		this.cnt = cnt;
	}
	
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getLanguage()
	{
		return language;
	}

	public void setLanguage(String language)
	{
		this.language = language;
	}

	public String getWords()
	{
		return words;
	}

	public void setWords(String words)
	{
		this.words = words;
	}

	public String getSnippet()
	{
		return snippet;
	}

	public void setSnippet(String snippet)
	{
		this.snippet = snippet;
	}

	public String getUnwantedWords()
	{
		return unwantedWords;
	}

	public void setUnwantedWords(String unwantedWords)
	{
		this.unwantedWords = unwantedWords;
	}

	public String getFileType()
	{
		return fileType;
	}

	public void setFileType(String fileType)
	{
		this.fileType = fileType;
	}

	public String getSite()
	{
		return site;
	}

	public void setSite(String site)
	{
		this.site = site;
	}

	public String getCnt()
	{
		return cnt;
	}

	public void setCnt(String cnt)
	{
		this.cnt = cnt;
	}

	public String getRuleMapping() {
		return ruleMapping;
	}

	public void setRuleMapping(String ruleMapping) {
		this.ruleMapping = ruleMapping;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public boolean isRelative() {
		return isRelative;
	}

	public void setRelative(boolean isRelative) {
		this.isRelative = isRelative;
	}

	public String getSysnonymWords() {
		return sysnonymWords;
	}

	public void setSysnonymWords(String sysnonymWords) {
		this.sysnonymWords = sysnonymWords;
	}
	
}
