
import twitter4j.*;

import java.util.*;
import java.io.*;

public class TwitterJ {
    private Twitter twitter;
    private PrintStream consolePrint;
    private List<Status> statuses;
    private List<String> terms;
    private String popularWord;
    private int frequencyMax;

   public TwitterJ(PrintStream console)
    {
        // Makes an instance of Twitter - this is re-useable and thread safe.
        // Connects to Twitter and performs authorizations.
        twitter = TwitterFactory.getSingleton();
        consolePrint = console;
        statuses = new ArrayList<>();
        terms = new ArrayList<>();
    }

    /*  Part 1 */
    /*
     * This method tweets a given message.
     * @param String  a message you wish to Tweet out
     */
    public void tweetOut(String message) throws TwitterException, IOException
    {
        twitter.updateStatus(message);
    }


    /*  Part 2 */
    /*
     * This method queries the tweets of a particular user's handle.
     * @param String  the Twitter handle (username) without the @sign
     */
    @SuppressWarnings("unchecked")
    public void queryHandle(String handle) throws TwitterException, IOException
    {
        statuses.clear();
        terms.clear();
        fetchTweets(handle);
        splitIntoWords();
        removeCommonEnglishWords();
        sortAndRemoveEmpties();
    }

    /*
     * This method fetches the most recent 2,000 tweets of a particular user's handle and
     * stores them in an arrayList of Status objects.  Populates statuses.
     * @param String  the Twitter handle (username) without the @sign
     */
    public void fetchTweets(String handle) throws TwitterException, IOException
    {
        // Creates file for dedebugging purposes
        PrintStream fileout = new PrintStream(new FileOutputStream("tweets.txt"));
        Paging page = new Paging (1,200);
        int p = 1;
        while (p <= 10)
        {
            page.setPage(p);
            statuses.addAll(twitter.getUserTimeline(handle,page));
            p++;
        }
        int numberTweets = statuses.size();
        fileout.println("Number of tweets = " + numberTweets);

        int count=1;
        for (Status j: statuses)
        {
            fileout.println(count+".  "+j.getText());
            count++;
        }
    }

    /*
     * This method takes each status and splits them into individual words.
     * Remove punctuation by calling removePunctuation, then store the word in terms.
     */
    public void splitIntoWords()
    {
        for(Status s:statuses){
            for(String p:s.getText().split(" ")){
                p = removePunctuation(p);
                terms.add(p);
            }
        }

    }

    /*
     * This method removes common punctuation from each individual word.
     * Consider reusing code you wrote for a previous lab.
     * Consider if you want to remove the # or @ from your words. Could be interesting to keep (or remove).
     * @ param String  the word you wish to remove punctuation from
     * @ return String the word without any punctuation
     */
    private String removePunctuation( String s ) {
        String[] punc = {".", "?", "!", ",", ":", ";", "-", "{", "}", "[", "]", "(", ")", "'", "\"","“","”","#","@"};
        for(String p:punc){
            while(s.contains(p)){
                s = s.replace(p,"");
            }
        }
        return s;
    }

    /*
     * This method removes common English words from the list of terms.
     * Remove all words found in commonWords.txt  from the argument list.
     * The count will not be given in commonWords.txt. You must count the number of words in this method.
     * This method should NOT throw an exception.  Use try/catch.
     */
    @SuppressWarnings("unchecked")
    private void removeCommonEnglishWords()
    {
        ArrayList<String> common = new ArrayList<>();
        try(FileReader file = new FileReader("commonWords.txt");BufferedReader reader = new BufferedReader(file)){

            String line;
            while((line = reader.readLine())!=null)   {
                common.add(line);

            }
        }catch(FileNotFoundException badFile){}catch(IOException e){}

        for(int i = 0;i<terms.size();i++){
            for(String s: common){
                if(terms.get(i).equalsIgnoreCase(s)){
                    terms.remove(i);
                    if(i!=0){
                        i--;
                    }
                }
            }
        }


    }

    /*
     * This method sorts the words in terms in alphabetically (and lexicographic) order.
     * You should use your sorting code you wrote earlier this year.
     * Remove all empty strings while you are at it.
     */
    @SuppressWarnings("unchecked")
    public void sortAndRemoveEmpties()
    {
        for(int i = 0; i < terms.size()-1;i++){
            int first = i;
            for(int j = i;j<terms.size();j++){
                if(terms.get(first).compareTo(terms.get(j))>0){
                    first = j;
                }
            }
            String temp = terms.get(i);
            terms.set(i,terms.get(first));
            terms.set(first,temp);

        }

        for(int k = 0;k<terms.size();k++){
            if(terms.get(k).equals(" ")){
                terms.remove(k);
                k--;
            }
        }


    }

    /*
     * This method returns the most common word from terms.
     * Consider case - should it be case sensitive?  The choice is yours.
     * @return String the word that appears the most times
     * @post will populate the frequencyMax variable with the frequency of the most common word
     */
    @SuppressWarnings("unchecked")
    public String mostPopularWord()
    {
        int max = 0;
        int maxCount = 0;
        for(int i = 0;i<terms.size()-1;i++){
            int count = 1;
            for(int j = i+1; j<terms.size();j++){
                if(terms.get(i).equalsIgnoreCase(terms.get(j))){
                    count++;
                }
            }
            if(count > maxCount){
                maxCount = count;
                max = i;
            }
        }
        popularWord = terms.get(max);
        return terms.get(max).toLowerCase();
    }

    /*
     * This method returns the number of times the most common word appears.
     * Note:  variable is populated in mostPopularWord()
     * @return int frequency of most common word
     */
    public int getFrequencyMax()
    {
        String word = mostPopularWord();
        int freq = 0;
        for(String s: terms){
            if(word.equalsIgnoreCase(s)){
                freq++;
            }
        }
        frequencyMax = freq;
        return freq;
    }

    public void termsOut(){
        for(String s:terms){
            System.out.println(s);
        }
    }




    /*  Part 3 */
    public void investigate () throws TwitterException, IOException
    {
        String[] people = {"BarackObama","justinbieber","katyperry","Cristiano","elonmusk"
        ,"ladygaga","narendramodi","YouTube","BillGates","NASA"};
        Scanner kb = new Scanner(System.in);
        int points = 0;

        System.out.println("Welcome to guess that tweet\nThe goal is to guess who sent the tweet\n" +
                "You have five guesses and a little bit of the tweet is show each time, and the whole tweet shown after 3 gueeses" +
                "\nThe list of famous people is ");
        for(String s:people){
            System.out.print(s + " ");
        }
        System.out.println("\nWould you like to play?");
            String yn = kb.nextLine();
        while(yn.equalsIgnoreCase("yes")){
            String[] possible = {"BarackObama","justinbieber","katyperry","Cristiano","elonmusk"
                    ,"ladygaga","narendramodi","YouTube","BillGates","NASA"};
            String person = people[(int)(Math.random()*(people.length+1))];
            fetchTweets(person);
            Status tweet = statuses.get((int)(Math.random()*(statuses.size()+1)));
            while(tweet.getText().contains("https")){
                tweet = statuses.get((int)(Math.random()*(statuses.size()+1)));
            }
            for(int i = 1;i<6;i++){
                if(i<4){
                    System.out.println("\"" + tweet.getText().substring(0,(int)(tweet.getText().length()*(i/3.0))) + "\"");
                }else{
                    System.out.println("\"" + tweet.getText() + "\"");
                }
                System.out.println("The possible users are ");
                for(String p:possible){
                    if(p!=null){
                        System.out.print(p +", ");
                    }
                }
                System.out.println("Who tweeted it?");
                String guess = kb.nextLine();
                if(guess.equalsIgnoreCase(person)){
                    System.out.println("That was correct!");
                    System.out.println("The whole tweet was\n " + tweet.getText());
                    points++;
                    i = 6;
                }else{
                    System.out.println("Sorry that was incorrect");
                    for(int j = 0; j < possible.length;j++){
                        if(possible[j].equalsIgnoreCase(guess)){
                            possible[j] = " ";
                        }
                    }
                    if(i==5){
                        System.out.println("The person was " + person);
                    }
                }
            }//end of for loop
            System.out.println("Would you like to play again?");
            yn = kb.nextLine();
        }//end of while loop

        System.out.println("The points you earned were " + points);
        System.out.println("Thanks for playing!");


        //Enter your code here
    }

    /*
     * This method determines how many people near Churchill Downs
     * tweet about the Kentucky Derby.
     */
    public void sampleInvestigate ()
    {
        Query query = new Query("Kentucky Derby");
        query.setCount(100);
        query.setGeoCode(new GeoLocation(38.2018,-85.7687), 5, Query.MILES);
        query.setSince("2021-5-1");
        try {
            QueryResult result = twitter.search(query);
            System.out.println("Count : " + result.getTweets().size()) ;
            for (Status tweet : result.getTweets()) {
                System.out.println("@"+tweet.getUser().getName()+ ": " + tweet.getText());
            }
        }
        catch (TwitterException e) {
            e.printStackTrace();
        }
        System.out.println();
    }
}
