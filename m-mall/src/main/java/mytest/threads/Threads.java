package mytest.threads;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Threads {
    public static void main(String[] args) {
        LinkedList<Integer> list = Lists.newLinkedList();

        new Thread(new Producer(list)).start();
        ExecutorService service = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 1; i++){
            service.execute(new Consumer(list));
        }
        service.shutdown();
    }
}

@AllArgsConstructor
class Producer implements Runnable{

    private LinkedList<Integer> list;

    @Override
    public void run() {
        synchronized (list) {
            while (true) {
                while (list.size() < 10) {
                    list.offer((int) (Math.random() * 10));
                    System.out.println("offer::" + list);
                }
                try {
                    System.out.println("offer sleep");
                    Thread.sleep(100);
//                        TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    System.out.println("offer ：：中断");
                }
            }
        }
    }
}

@AllArgsConstructor
class Consumer implements Runnable {

    private LinkedList<Integer> list;

    @Override
    public void run() {
        synchronized (list) {
            while (true) {
                while (list.peek() != null) {
                    list.poll();
                    System.out.println(Thread.currentThread().getId() + " poll:: " + list);
                }
                try {
                    System.out.println("poll sleep");
                    Thread.sleep(100);
//                        TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    System.out.println("poll：：中断");
                }
            }
        }
    }
}

