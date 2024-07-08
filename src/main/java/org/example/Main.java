package org.example;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

public class Main {
    public static ArrayBlockingQueue<String> threadAQueue = new ArrayBlockingQueue<>(100);
    public static ArrayBlockingQueue<String> threadBQueue = new ArrayBlockingQueue<>(100);
    public static ArrayBlockingQueue<String> threadCQueue = new ArrayBlockingQueue<>(100);

    public static void main(String[] args) throws InterruptedException {

        Thread myThread = new Thread(() -> {
            for (int i = 0; i < 10_000; i++) {
                try {
                    String text = generateText("abc", 100_000);
                    threadAQueue.put(text);
                    threadBQueue.put(text);
                    threadCQueue.put(text);
                } catch (InterruptedException e) {
                    return;
                }
            }
        });
        myThread.start();

        Map<Character, Integer> maxCountMap = new HashMap<>();
        maxCountMap.put('a', 0);
        maxCountMap.put('b', 0);
        maxCountMap.put('c', 0);

        Map<Character, String> maxTextMap = new HashMap<>();
        maxTextMap.put('a', " ");
        maxTextMap.put('b', " ");
        maxTextMap.put('c', " ");

        Thread threadA = new Thread(() -> {
            for (int i = 0; i < 10_000; i++) {
                try {
                    String text = threadAQueue.take();
                    int count = countMaxABC(text, 'a');
                    if (count > maxCountMap.get('a')) {
                        maxCountMap.put('a', count);
                        maxTextMap.put('a', text);
                    }
                } catch (InterruptedException e) {
                    return;
                }
            }
        });
        threadA.start();

        Thread threadB = new Thread(() -> {
            for (int i = 0; i < 10_000; i++) {
                try {
                    String text = threadBQueue.take();
                    int count = countMaxABC(text, 'b');
                    if (count > maxCountMap.get('b')) {
                        maxCountMap.put('b', count);
                        maxTextMap.put('b', text);
                    }
                } catch (InterruptedException e) {
                    return;
                }
            }
        });
        threadB.start();

        Thread threadC = new Thread(() -> {
            for (int i = 0; i < 10_000; i++) {
                try {
                    String text = threadCQueue.take();
                    int count = countMaxABC(text, 'c');
                    if (count > maxCountMap.get('c')) {
                        maxCountMap.put('c', count);
                        maxTextMap.put('c', text);
                    }
                } catch (InterruptedException e) {
                    return;
                }
            }
        });
        threadC.start();

        myThread.join();
        threadA.join();
        threadB.join();
        threadC.join();

        System.out.println("Максимальное число повторений 'a' = " + maxCountMap.get('a') + " , в тексте: " + "\n" + maxTextMap.get('a') + "\n");
        System.out.println("Максимальное число повторений 'b' = " + maxCountMap.get('b') + " , в тексте: " + "\n" + maxTextMap.get('b') + "\n");
        System.out.println("Максимальное число повторений 'c' = " + maxCountMap.get('c') + " , в тексте: " + "\n" + maxTextMap.get('c') + "\n");
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static int countMaxABC(String text, char symbol) {
        int maxSize = 0;
        for (int j = 0; j < text.length(); j++) {
            if (text.charAt(j) == symbol) {
                maxSize++;
            }
        }
        return maxSize;
    }
}