
 /* To generate weather for location at longitude -98.76 and latitude 26.70 for
 * the month of February do:
 * java WeatherGenerator -98.76 26.70 3
 */

public class WeatherGenerator {

    static final int WET = 1; // Use this value to represent a wet day
    static final int DRY = 2; // Use this value to represent a dry day 
    
    // Number of days in each month, January is index 0, February is index 1...
    static final int[] numberOfDaysInMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    
    /* 
     * Description:
     *      this method works under the assumption that under the same directory as WeatherGenerator.java, 
     *      there exist drywet.txt and wetwet.txt that contains probabilities of the next day being wet 
     *      with today being a dry/wet day.  
     */
    public static void populateArrays(double[][] drywet, double[][] wetwet) {

        StdIn.setFile("drywet.txt");

	for(int i=0; i < drywet.length; i++){
            for(int j=0; j<14; j++){
                drywet[i][j] = StdIn.readDouble();
            }
        }

	StdIn.setFile("wetwet.txt");

	for(int i=0; i < drywet.length; i++){
            for(int j=0; j<14; j++){
                wetwet[i][j] = StdIn.readDouble();
            }
        }
    }

    /* 
     * Description:
     *      this method uses drywet and wetwet arrays populated by populateArrays, and longitude and latitude
     *      of the target location to populate drywetProbability and wetwetProbability with the 
     *      probability of dry/wet day is followed by a wet day each month at that location.
     *      In other words, extracting the probabilities of the location.
     */
    public static void populateLocationProbabilities( double[] drywetProbability, double[] wetwetProbability, 
                                     double longitude, double latitude, 
                                     double[][] drywet, double[][] wetwet){

        for (int i = 0; i < drywet.length; i++) {
            if (drywet[i][0] == longitude && drywet[i][1] == latitude) {
                for (int j = 0; j < drywetProbability.length; j++) {
                    drywetProbability[j] = drywet[i][j + 2];
                }
                for (int j = 0; j < wetwetProbability.length; j++) {
                    wetwetProbability[j] = wetwet[i][j + 2];
                }
            }
        }
    }

    /* 
     * Description:
     *      Given the number of days in a month, and probabilities of weather changing at a certain location, 
     *      the method should return  the forecast for the month.
     *      The first day of the month has a 50% chance to be a wet day, [0,0.5) (wet), [0.5,1) (dry)
     *      Use StdRandom.uniform() to generate a real number uniformly in [0,1)
     */

    public static int[] forecastGenerator( double drywetProbability, double wetwetProbability, int numberOfDays) {
        int[] forecast = new int[numberOfDays];
        int weather = 0;
        for (int i = 0; i < numberOfDays; i++) {
            double num = StdRandom.uniform();
            if (i == 0) {
                if (num < 0.5) {
                    weather = 1;
                    forecast[i] = 1;
                }
                else {
                    weather = 2;
                    forecast[i] = 2;
                }
            }
            else {
                if (weather == 1) {
                    if (num < wetwetProbability) {
                        weather = 1;
                        forecast[i] = 1;
                    }
                    else {
                        weather = 2;
                        forecast[i] = 2;
                    }
                    
                }
                else {
                    if (weather == 2) {
                        if (num < drywetProbability) {
                        weather = 1;
                        forecast[i] = 1;
                        }
                        else {
                            weather = 2;
                            forecast[i] = 2;
                        }
                    }
                }
            }
        }
        return forecast;
    }

    /* 
     * Description:
     *      This method takes the number of locations that is stored in wetwet.txt and drywet.txt (the number of
     *      lines in each file), and takes in the month number (January is index 0, February is index 1... ),  
     *      and the longitude and the latitude of the location we want to make the prediction on.
     *      This method calls all previous methods (populateArrays(), populateLocationProbabilities(), 
     *      forecastGenerator() in this order). 
     */
    public static int[] oneMonthForecast(int numberOfLocations, int month, double longitude, double latitude ){
        double[][] drywet = new double[4100][14];
        double[][] wetwet = new double[4100][14];
        populateArrays(drywet, wetwet);

        double[] drywetProbabilities = new double[12];
        double[] wetwetProbabilities = new double[12];
        populateLocationProbabilities(drywetProbabilities, wetwetProbabilities, longitude, latitude, drywet, wetwet);
        int numberOfDays = numberOfDaysInMonth[month];
        double drywetProbability = drywetProbabilities[month];
        double wetwetProbability = wetwetProbabilities[month];
        int[] forecast = forecastGenerator(drywetProbability, wetwetProbability, numberOfDays);
        return forecast;
    }

    /********
     * 
     * * Methods to analyze forecasts 
     * 
     ******/

    /* 
     * Description:
     *      Returns the number of mode (WET or DRY) days in forecast.
     */ 
    public static int numberOfWetDryDays (int[] forecast, int mode) {
        int wetCounter = 0;
        int dryCounter = 0;
        for (int i = 0; i < forecast.length; i++) {
            if (forecast[i] == 1) {
                wetCounter++;
            }
            else if (forecast[i] == 2) {
                dryCounter++;
            }
        }
        if (mode == 1) {
            return wetCounter;
        }
        else if (mode == 2) {
            return dryCounter;
        }
        return 0;
    }

    /* 
     * Description:
     *      Find the longest number of consecutive mode (WET or DRY) days in forecast.
     */ 
    public static int lengthOfLongestSpell (int[] forecast, int mode) {
        int wetCounter = 0;
        int dryCounter = 0;
        int longestSpellWet = 0;
        int longestSpellDry = 0;

        for (int i = 0; i < forecast.length; i++) {
            if (forecast[i] == 1) {
                wetCounter++;
                dryCounter = 0;
            }
            else if (forecast[i] == 2) {
                dryCounter++;
                wetCounter = 0;
            }
            if (wetCounter > longestSpellWet) {
                longestSpellWet = wetCounter;
            }
            else if (dryCounter > longestSpellDry) {
                longestSpellDry = dryCounter;
            }
        }
        if (mode == 1) {
            return longestSpellWet;
        }
        else if (mode == 2) {
            return longestSpellDry;
        }
        return 0;
    }

    /* 
     * Description:
     *      Given the forecast of a month at certain location, this method finds the index of the
     *      first day of a 7 day period with the least amount of rain. If multiple exist, return 
     *      the earliest.
     */ 

    public static int bestWeekToTravel(int[] forecast) {
        int firstDayIndex = -1;
        int minWeekWetDays = 31;
        int weekWetDays = 0;
        for (int i = 0; i < forecast.length - 7; i++) {
            int bound = i + 7;
            for (int j = i; j < (bound); j++) {
                if (forecast[j] == 1) {
                    weekWetDays++;
                }
            }
            if (weekWetDays < minWeekWetDays) {
                minWeekWetDays = weekWetDays;
                firstDayIndex = i;
            }
            weekWetDays = 0;

            
        }   
        return firstDayIndex;
    }
    /*
     * Reads the files containing the transition probabilities for US locations.
     * Execution:
     *   java WeatherGenerator -97.58 26.02 3
     */
    public static void main (String[] args) {

        int numberOfRows    = 4100; // Total number of locations
        int numberOfColumns = 14;   // Total number of 14 columns in file 
        
        // File format: longitude, latitude, 12 months of transition probabilities
        double longitude = Double.parseDouble(args[0]);
        double latitude  = Double.parseDouble(args[1]);
        int    month     = Integer.parseInt(args[2]);
        
        int[] forecast = oneMonthForecast( numberOfRows,  month,  longitude,  latitude );
        

        int drySpell = lengthOfLongestSpell(forecast, DRY);
        int wetSpell = lengthOfLongestSpell(forecast, WET);
        int bestWeek = bestWeekToTravel(forecast);

        StdOut.println("There are " + forecast.length + " days in the forecast for month " + month);
        StdOut.println(drySpell + " days of dry spell.");
        StdOut.println("The bestWeekToTravel starts on: " + bestWeek);

        for ( int i = 0; i < forecast.length; i++ ) {
            // This is the ternary operator. (conditional) ? executed if true : executed if false
            String weather = (forecast[i] == WET) ? "Wet" : "Dry";  
            StdOut.println("Day " + (i) + " is forecasted to be " + weather);
        }
    }
}
