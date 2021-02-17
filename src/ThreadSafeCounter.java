public class ThreadSafeCounter {
    private int counter =0;
    public void increment(){
        synchronized (this) {
            counter++;
            System.out.println("ThreadSafeCounter"+counter);

        }
    }
    public int get(){
        synchronized (this) {
            return counter;
        }
    }
}
