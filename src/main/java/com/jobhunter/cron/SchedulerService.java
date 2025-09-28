package com.jobhunter.cron;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SchedulerService {

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public void scheduleWorkflow(Runnable workflow, long initialDelay, long period, TimeUnit unit) {
        System.out.println("Scheduling workflow to run every " + period + " " + unit.toString().toLowerCase() + ".");
        scheduler.scheduleAtFixedRate(workflow, initialDelay, period, unit);
    }

    public void close() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }
}

