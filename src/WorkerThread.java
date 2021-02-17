import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 工作者线程示例代码
 */
public class WorkerThread {
    public static void main(String[] args) {
        Helper helper = new Helper();
        helper.init();
        //此处hlper的客户端线程为main线程
        helper.submit("doSomething");


    }
    static class Helper{
        private final BlockingQueue<String> workQueue = new ArrayBlockingQueue<String>(100);

        //用于处理队列workQueue中任务的工作者线程
        private final Thread workThread = new Thread(){
            @Override
            public void run() {
                String task = null;
                while(true){
                    try {
                        task=workQueue.take();
                    } catch (InterruptedException e) {
                        break;
                    }
                    System.out.println(doProcess(task));
                }
            }
        };

        public void init(){
            workThread.start();
        }

        protected String doProcess(String task){
            return task+"=>processed.";
        }

        public void submit(String task){
            try {
                workQueue.put(task);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
