// Updating App.java
//Errors Fixed

package com.fitnessapp;

import com.fitnessapp.model.*;
import com.fitnessapp.repository.InMemoryDataStore;
import com.fitnessapp.service.FitnessService;
import com.fitnessapp.service.RecommendationEngine;
import com.fitnessapp.util.ReminderScheduler;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        InMemoryDataStore store = new InMemoryDataStore();
        FitnessService fitnessService = new FitnessService(store);
        RecommendationEngine recommender = new RecommendationEngine(store);

        // Add sample user (for demo)
        User demo = new User("u1", "Alex", 30, 175, 70, FitnessGoal.LOSE_WEIGHT);
        store.saveUser(demo);

        System.out.println("=== Fitness Tracker (Console Demo) ===");
        Scanner sc = new Scanner(System.in);
        boolean running = true;
        while (running) {
            System.out.println("\nMenu: 1=add activity 2=view weekly summary 3=recommend 4=exit");
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1":
                    System.out.println("Enter userId:");
                    String userId = sc.nextLine().trim();
                    System.out.println("Enter type (RUN/CYCLE/WEIGHT_TRAIN):");
                    String t = sc.nextLine().trim();
                    System.out.println("Enter duration minutes:");
                    int minutes = Integer.parseInt(sc.nextLine().trim());
                    System.out.println("Enter intensity (1-10):");
                    int intensity = Integer.parseInt(sc.nextLine().trim());

                    ActivityRecord ar = new ActivityRecord(LocalDate.now(), ActivityType.valueOf(t.toUpperCase()), minutes, intensity);
                    fitnessService.addActivity(userId, ar);
                    System.out.println("Activity added.");
                    break;
                case "2":
                    System.out.println("Enter userId:");
                    userId = sc.nextLine().trim();
                    System.out.println(fitnessService.getWeeklySummary(userId));
                    break;
                case "3":
                    System.out.println("Enter userId:");
                    userId = sc.nextLine().trim();
                    List<WorkoutPlan> plans = recommender.recommendWorkouts(userId, 2);
                    System.out.println("Recommended workouts:");
                    plans.forEach(p -> System.out.println("- " + p));
                    break;
                case "4":
                    running = false;
                    break;
                default:
                    System.out.println("Unknown.");
            }
        }

        // start simulated reminder scheduler (non-blocking)
        ReminderScheduler.scheduleDailyReminder(() -> {
            System.out.println("[Reminder] Time to log your activity!");
        }, 86400); // 86400 seconds = 1 day (for demo you can set to 5 seconds)
        System.out.println("Exiting demo.");
    }
}
