package de.vsti.aiduke.solution.part2;

import java.util.Scanner;

/**
 * Solution for workshop tasks part II:
 *
 * @author alexander.ruehl
 */
public class Part2 {
    // API key gets picked up from environment and must be set prior to run the solution
    static String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY");

    public static void main(String[] args) {
        Part2 part2 = new Part2();
        part2.execute();
    }

    Part2() {

    }

    public void execute() {
        boolean ragMode = true;
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Hello, I am your assistant. Please enter your question. Type 'exit' to quit or 'rag'/'norag' to switch modes.");
            while (true) {
                String input = scanner.nextLine();

                switch (input) {
                    case "exit" -> {
                        System.out.println("Good bye!");
                        System.exit(0);
                    }
                    case "rag" -> {
                        ragMode = true;
                        System.out.println("RAG on");
                    }
                    case "norag" -> {
                        ragMode = false;
                        System.out.println("RAG off");
                    }
                    default -> {
                        String answer = "This is my answer";
                        System.out.println(answer);
                    }
                }
            }
        }
    }
}
