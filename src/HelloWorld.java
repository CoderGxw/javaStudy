public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello,World!");
        Thread thread =new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Thread start");
            }
        });
        thread.start();
        
    }
}
