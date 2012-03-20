package dataStruct;

public class AbstractSentence {
	private int id;
	private String sentence;
	private int correlation;
	
	public AbstractSentence(int id, String sentence, int correlation) {
		super();
		this.id = id;
		this.sentence = sentence;
		this.correlation = correlation;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSentence() {
		return sentence;
	}

	public void setSentence(String sentence) {
		this.sentence = sentence;
	}

	public int getCorrelation() {
		return correlation;
	}

	public void setCorrelation(int correlation) {
		this.correlation = correlation;
	}
	
	

}
