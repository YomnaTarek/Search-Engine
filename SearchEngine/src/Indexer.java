
public class Indexer {

	public Indexer() 
	{
		
	}
	
	 private static class Index extends Thread{
		 int threadId;
		 int key; //for synchronization purpose
		 public Index(int id, int k)
		 {
			 this.threadId = id;
			 this.key = k;
		 }
		 
		 public void run()
		 {
			 
		 }
	 }


	
     }
