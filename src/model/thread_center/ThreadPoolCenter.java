package model.thread_center;

import java.util.concurrent.*;

public class ThreadPoolCenter {

    private static ExecutorService executor;

    static{
        executor = new ThreadPoolExecutor(4, 4,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>());
    }


    public static ExecutorService getExecutor(){

        return executor;
    }

    public static void closeThreadPool() {
        executor.shutdown();
    }



}
