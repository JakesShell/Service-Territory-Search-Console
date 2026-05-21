package com.territory;

import java.util.List;
import java.util.Scanner;

public class Main {
    private static final TerritorySearchService service = new TerritorySearchService();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Service Territory Search Console");
        System.out.println("Internal lookup tool for territory coverage, region filtering, and state search.");
        System.out.println();

        while (true) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> displayAllStates();
                case "2" -> searchByName();
                case "3" -> searchByCode();
                case "4" -> filterByRegion();
                case "5" -> displayCoverageSummary();
                case "6" -> {
                    System.out.println("Exiting Service Territory Search Console.");
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }

            System.out.println();
        }
    }

    private static void printMenu() {
        System.out.println("Menu");
        System.out.println("----");
        System.out.println("1. Display All Covered States");
        System.out.println("2. Search State By Name");
        System.out.println("3. Search State By Code");
        System.out.println("4. Filter States By Region");
        System.out.println("5. View Coverage Summary");
        System.out.println("6. Exit");
        System.out.print("Choose an option: ");
    }

    private static void displayAllStates() {
        System.out.println();
        System.out.println("Covered States");
        System.out.println("--------------");

        for (TerritoryState state : service.getAllStates()) {
            System.out.printf("%-18s %-4s %-10s%n", state.getName(), state.getCode(), state.getRegion());
        }
    }

    private static void searchByName() {
        System.out.print("Enter full or partial state name: ");
        String query = scanner.nextLine();

        List<TerritoryState> matches = service.searchByName(query);

        if (matches.isEmpty()) {
            System.out.println("No states matched that search.");
            return;
        }

        System.out.println();
        System.out.println("Search Results");
        System.out.println("--------------");

        for (TerritoryState state : matches) {
            System.out.printf("%-18s %-4s %-10s%n", state.getName(), state.getCode(), state.getRegion());
        }
    }

    private static void searchByCode() {
        System.out.print("Enter state code: ");
        String code = scanner.nextLine();

        TerritoryState state = service.searchByCode(code);

        if (state == null) {
            System.out.println("No state found for that code.");
            return;
        }

        System.out.println();
        System.out.println("State Match");
        System.out.println("-----------");
        System.out.printf("%-18s %-4s %-10s%n", state.getName(), state.getCode(), state.getRegion());
    }

    private static void filterByRegion() {
        System.out.print("Enter region (Northeast, Midwest, South, West): ");
        String region = scanner.nextLine();

        List<TerritoryState> matches = service.filterByRegion(region);

        if (matches.isEmpty()) {
            System.out.println("No states found for that region.");
            return;
        }

        System.out.println();
        System.out.println("Region Results");
        System.out.println("--------------");

        for (TerritoryState state : matches) {
            System.out.printf("%-18s %-4s %-10s%n", state.getName(), state.getCode(), state.getRegion());
        }
    }

    private static void displayCoverageSummary() {
        System.out.println();
        System.out.println(service.buildCoverageSummary());
    }
}
