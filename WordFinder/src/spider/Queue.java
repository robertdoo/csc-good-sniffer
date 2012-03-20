package spider;


import java.util.LinkedList;

public class Queue{
	private LinkedList<String> queue = new LinkedList<String>();
	
	public void enQueue(String t){
		queue.addLast(t);
	}
	public String deQueue(){
		return queue.removeFirst();
	}
	public boolean isQueueEmpty(){
		return queue.isEmpty();
	}
	public boolean contains(String t){
		return queue.contains(t);
	}
	public boolean empty(){
		return queue.isEmpty();
	}
	public String get(int index){
		return queue.get(index);
	}
	public int getLength(){
		return queue.toArray().length;
	}
	public String[] toArray(){
		return (String[])queue.toArray();
	}
}


