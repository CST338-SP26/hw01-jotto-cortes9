/**
 * @author Luis Cortes
 * @version 0.1.0
 * @Since 2/12/2026
 **/
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Jotto {

        private static final int WORD_SIZE = 5;
        private static final boolean DEBUG = true;
        private final ArrayList<String> wordList = new ArrayList<>();
        private final ArrayList<String> playGuesses = new ArrayList<>();
        private final ArrayList<String> playWords = new ArrayList<>();

        private String currentWord = "";
        private final String filename;
        private int score = 0;

        public Jotto(String filename) {
                this.filename = filename;
                readWords();
        }

        public ArrayList<String> readWords() {

                wordList.clear();

                try {
                        File file = new File(filename);
                        Scanner scan = new Scanner(file);

                        while (scan.hasNextLine()) {
                                String word = scan.nextLine().trim().toLowerCase();

                                if (!wordList.contains(word)) {
                                        wordList.add(word);
                                }
                        }

                        scan.close();

                } catch (Exception e) {
                        System.out.println("Couldn't open " + filename);
                }

                return wordList;
        }

        public boolean pickWord() {

                if (wordList.size() == 0) return false;

                Random rand = new Random();
                currentWord = wordList.get(rand.nextInt(wordList.size()));

                if (playWords.contains(currentWord) &&
                        playWords.size() == wordList.size()) {

                        System.out.println("You've guessed them all!");
                        return false;
                }

                if (playWords.contains(currentWord)) {
                        return pickWord();
                }

                playWords.add(currentWord);

                // ✅ DEBUG now prints ONLY the word
                if (DEBUG) {
                        System.out.println(currentWord);
                }

                return true;
        }

        public int getLetterCount(String guess) {

                if (guess.equals(currentWord)) return WORD_SIZE;

                int count = 0;

                ArrayList<Character> letters = new ArrayList<>();

                for (int i = 0; i < currentWord.length(); i++) {
                        char c = currentWord.charAt(i);
                        if (!letters.contains(c)) {
                                letters.add(c);
                        }
                }

                for (int i = 0; i < guess.length(); i++) {
                        char g = guess.charAt(i);

                        if (letters.contains(g)) {
                                letters.remove((Character) g);
                                count++;
                        }
                }

                return count;
        }

        public boolean addPlayerGuess(String guess) {

                if (!playGuesses.contains(guess)) {
                        playGuesses.add(guess);
                        return true;
                }

                return false;
        }

        public int guess() {

                Scanner scan = new Scanner(System.in);
                ArrayList<String> currentGuesses = new ArrayList<>();

                int roundScore = WORD_SIZE + 1;

                while (true) {

                        System.out.println("Current Score: " + roundScore);
                        System.out.print("What is your guess (q to quit): ");

                        String guess = scan.nextLine().trim().toLowerCase();

                        if (guess.equals("q")) {
                                roundScore = Math.min(roundScore, 0);
                                break;
                        }

                        if (guess.length() != WORD_SIZE) {
                                System.out.println("Word must be 5 characters (" + guess + " is " + guess.length() + ")");
                                continue;
                        }

                        addPlayerGuess(guess);

                        if (guess.equals(currentWord)) {
                                System.out.println("DINGDINGDING!!! the word was " + currentWord);
                                currentGuesses.add(guess);
                                playerGuessScores(currentGuesses);
                                return roundScore;
                        }

                        if (currentGuesses.contains(guess)) {
                                System.out.println("You already entered \"" + guess + "\".");
                                continue;
                        }

                        currentGuesses.add(guess);

                        int letterCount = getLetterCount(guess);

                        if (letterCount != WORD_SIZE) {
                                System.out.println(guess + " has a Jotto score of " + letterCount);
                        } else {
                                System.out.println("The word you chose is an anagram.");
                        }

                        playerGuessScores(currentGuesses);

                        roundScore--;
                }

                return roundScore;
        }

        public void playerGuessScores(ArrayList<String> guesses) {

                System.out.println("Guess\t\tScore");

                for (String g : guesses) {
                        System.out.println(g + "\t\t" + getLetterCount(g));
                }

                System.out.println();
        }

        public String showWordList() {

                String result = "Current word list:\n";

                for (String w : wordList) {
                        result += w + "\n";
                }

                return result;
        }

        public String showPlayedWords() {

                if (playWords.size() == 0) {
                        return "No words have been played\n";
                }

                String result = "Current list of played words:\n";

                for (String w : playWords) {
                        result += w + "\n";
                }

                return result;
        }

        public ArrayList<String> showPlayerGuesses() {

                if (playGuesses.size() == 0) {
                        System.out.println("No guesses yet");
                } else {
                        System.out.println("Current player guesses:");
                        for (String g : playGuesses) {
                                System.out.println(g);
                        }
                }

                return playGuesses;
        }

        public void updateWordList() {

                try {
                        FileWriter writer = new FileWriter(filename);

                        for (String guess : playGuesses) {
                                if (!wordList.contains(guess)) {
                                        wordList.add(guess);
                                }
                        }

                        for (String word : wordList) {
                                writer.write(word + "\n");
                        }

                        writer.close();

                } catch (Exception e) {
                        System.out.println("Couldn't open " + filename);
                }
        }

        public void play() {

                Scanner scan = new Scanner(System.in);

                while (true) {

                        System.out.println("Current Score: " + score);
                        System.out.println("1: Start game");
                        System.out.println("2: Show word list");
                        System.out.println("3: Show played words");
                        System.out.println("4: Show player guesses");
                        System.out.println("zz: Exit");

                        String choice = scan.nextLine().trim().toLowerCase();

                        if (choice.equals("zz")) {
                                System.out.println("Final score: " + score);
                                break;
                        }

                        if (choice.equals("1")) {
                                if (pickWord()) {
                                        int roundScore = guess();
                                        score += roundScore;
                                }
                        }
                        else if (choice.equals("2")) {
                                System.out.println(showWordList());
                        }
                        else if (choice.equals("3")) {
                                System.out.println(showPlayedWords());
                        }
                        else if (choice.equals("4")) {
                                showPlayerGuesses();
                        }
                        else {
                                System.out.println("Unknown option.");
                        }
                }
        }

        static void main(String[] args) {
                Jotto game = new Jotto("wordList.txt");
                game.play();
        }
}
