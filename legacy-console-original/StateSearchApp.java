import java.util.Scanner;

public class StateSearchApp {
    private static final String[] stateNames = {
        "Alabama", "Alaska", "Arizona", "Arkansas", "California", "Colorado", "Connecticut", "Delaware", "Florida",
        "Georgia", "Hawaii", "Idaho", "Illinois", "Indiana", "Iowa", "Kansas", "Kentucky", "Louisiana", "Maine",
        "Maryland", "Massachusetts", "Michigan", "Minnesota", "Mississippi", "Missouri", "Montana", "Nebraska",
        "Nevada", "New Hampshire", "New Jersey", "New Mexico", "New York", "North Carolina", "North Dakota", "Ohio",
        "Oklahoma", "Oregon", "Pennsylvania", "Rhode Island", "South Carolina", "South Dakota", "Tennessee", "Texas",
        "Utah", "Vermont", "Virginia", "Washington", "West Virginia", "Wisconsin", "Wyoming"
    };

    public static void main(String[] args) {
        while (true) {
            displayMenu();
            int choice = getUserChoice();

            switch (choice) {
                case 1:
                    displayStates();
                    break;
                case 2:
                    searchStates();
                    break;
                case 3:
                    System.out.println("Exiting program...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void displayMenu() {
        System.out.println("State Search Application");
        System.out.println("1. Display the text");
        System.out.println("2. Search");
        System.out.println("3. Exit program");
        System.out.print("Enter your choice (1-3): ");
    }

    private static int getUserChoice() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextInt();
    }

    private static void displayStates() {
        System.out.println("List of 50 states:");
        for (String state : stateNames) {
            System.out.println(state);
        }
    }

    private static void searchStates() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a pattern to search for: ");
        String pattern = scanner.nextLine();

        int[] indices = boyerMooreSearch(String.join(" ", stateNames), pattern);
        if (indices.length == 0) {
            System.out.println("Pattern not found in the text.");
        } else {
            System.out.print("Pattern found at indices: ");
            for (int index : indices) {
                System.out.print(index + " ");
            }
            System.out.println();
        }
    }

    private static int[] boyerMooreSearch(String text, String pattern) {
        int n = text.length();
        int m = pattern.length();
        int[] badCharShift = new int[256];

        // Precompute the bad character shift table
        for (int i = 0; i < 256; i++) {
            badCharShift[i] = m;
        }
        for (int i = 0; i < m - 1; i++) {
            badCharShift[pattern.charAt(i)] = m - i - 1;
        }

        // Perform the search
        int[] indices = new int[n];
        int count = 0;
        int i = m - 1;
        while (i < n) {
            int j = m - 1;
            while (j >= 0 && text.charAt(i) == pattern.charAt(j)) {
                i--;
                j--;
            }
            if (j < 0) {
                indices[count++] = i + 1;
                i += badCharShift[text.charAt(i + m)];
            } else {
                i += Math.max(1, j - badCharShift[text.charAt(i)]);
            }
        }

        // Trim the indices array to the correct size
        int[] result = new int[count];
        System.arraycopy(indices, 0, result, 0, count);
        return result;
    }
}