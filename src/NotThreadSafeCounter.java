public class NotThreadSafeCounter {
    private int counter =0;
    public void increment(){
        counter++;
        System.out.println("NotThreadSafeCounter"+counter);
    }
    public int get(){
        return counter;
    }
}
