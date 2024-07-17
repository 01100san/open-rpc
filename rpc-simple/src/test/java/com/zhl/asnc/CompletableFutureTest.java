package com.zhl.asnc;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * <p>
 *
 * @author zhl
 * @since 2024-07-17 14:01
 */
public class CompletableFutureTest {
    public static void main(String[] args) {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        completableFuture.complete("hello CompletableFuture");
        new Thread(()->{
            try {
//                CompletableFuture<String> future = new CompletableFuture<>();
//                String value = future.get();
                String value = completableFuture.get();
                System.out.println( "["+Thread.currentThread().getName() +"] value = " + value);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}
