package com.example.geonotesteaching.service;

public class VirtualDemo {
    public static void runIO() {
        try (var exec = java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 1; i <= 50; i++) {
                int taskId = i;
                exec.submit(() -> {
                    try {
                        Thread.sleep(200 + (int)(Math.random() * 100)); // 200–300ms
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    System.out.println("Tarea " + taskId + " ejecutada en " + Thread.currentThread());
                });
            }
        }
        System.out.println("✅ Todas las tareas lanzadas (Virtual Threads demo)");
    }
}