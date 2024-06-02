import twitter4j.*;
import java.io.*;
import java.util.Scanner;
import java.util.Date;

public class TwitterRunner {
    private static PrintStream consolePrint;

    public static void main (String []args) throws TwitterException, IOException
    {
        consolePrint = System.out; // this preserves the standard output so we can get to it later

        // PART 1
        //Test out the property file you setup for your twitter account
        TwitterJ tweety = new TwitterJ(consolePrint);

        // Create and set a String called message here
        String message = "REPLACE THIS STRING WITH YOUR OWN MESSAGE";
        // tweety.tweetOut(message);


        // PART 2
        // Choose a public Twitter user's handle


         Scanner scan = new Scanner(System.in);
         consolePrint.print("Please enter a Twitter handle, do not include the @symbol --> ");
         String twitter_handle = scan.next();

         //Find and print the most popular word they tweet
         while (!twitter_handle.equals("done"))
         {
             tweety.queryHandle(twitter_handle);
            consolePrint.println("The most common word from @" + twitter_handle + " is: " + tweety.mostPopularWord()+ ".");
				consolePrint.println("The word appears " + tweety.getFrequencyMax() + " times.");
            consolePrint.println();
//            tweety.termsOut();
            consolePrint.print("Please enter a Twitter handle, do not include the @ symbol --> ");
            twitter_handle = scan.next();
         }



        // PART 3
        tweety.investigate();

    }//end of main method
}
