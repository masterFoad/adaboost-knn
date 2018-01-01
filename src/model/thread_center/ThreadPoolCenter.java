package model.thread_center;

import java.util.concurrent.*;

public class ThreadPoolCenter {

    private static BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(3, true);
    private static RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();
    private static ExecutorService executor;

    static{
        executor = new ThreadPoolExecutor(3, 3, 0L, TimeUnit.MILLISECONDS, queue, handler);

    }

    public static ThreadPoolCenter instance;

    public static ExecutorService getExecutor(){
//        if(instance==null){
//            instance = new ThreadPoolCenter();
//        }

        return executor;
    }

    public static void closeThreadPool() {
        executor.shutdown();
    }



}
