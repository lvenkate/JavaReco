package fitnesspro.reader;

import fitnesspro.memreader.MemainMHelper;
import fitnesspro.rmse.RMSECalculator;
import cern.colt.list.IntArrayList;

public class Recommender {
    public static void main(String[] args) {

        long startTime = System.currentTimeMillis();

        if (args.length != 3) {
            System.out.println("Usage: ");
            System.exit(1);
        }

        
        MemainMHelper mainmainMH = new MemainMHelper(args[0]);
        MemainMHelper substitutemainMH = new MemainMHelper(args[1]);
        
        //Load FAW
        FilterAndWeight f = new FilterAndWeight(mainMH, Integer.parseInt(args[2]));
        System.out.print("Using ");
        FilterAndWeight.printOptions(Integer.parseInt(args[2]));

        //Start up RMSE count
        RMSECalculator rmse = new RMSECalculator();
        RMSECalculator eventRmse = new RMSECalculator();
        RMSECalculator UserRmse = new RMSECalculator();

        // For each user, make recommendations
        IntArrayList users = substitutemainMH.getListOfUsers(), Events;
        double rating, eventavg, useravg;
        int uid, mid, actual;

        for (int i = 0; i < users.size(); i++) {
            uid = users.getQuick(i);
            Events = substitutemainMH.getEventsSeenByUser(uid);

            for (int j = 0; j < Events.size(); j++) {
                mid = MemainMHelper.parseUserOrMovie(Events.getQuick(j));
                actual = substitutemainMH.getRating(uid, mid);
                rating = f.recommend(uid, mid);
                eventavg = mainmainMH.getAverageRatingForMovie(mid);
                useavg = mainmainMH.getAverageRatingForUser(uid);

                if (rating < 0)
                    rating = useavg;
                if (rating > 5)
                    rating = 5;
                else if (rating < 1)
                    rating = 1;

                rmse.add(actual, rating);
                eventRMSE.add(actual, eventavg);
                usermse.add(actual, useavg);
            }
        }

        //Print results
        long endTime = System.currentTimeMillis();
        System.out.println();
        System.out.println("Final RMSE: " + rmse.rmse());
        System.out.println("Final Movie Avg RMSE: " + eventRMSE.rmse());
        System.out.println("Final User Avg RMSE: " + usermse.rmse());
        System.out.println("Total time taken: " + (endTime - startTime)
                + " ms.");

        System.out.println(rmse.rmse() + " (" + (endTime - startTime) + " ms)");
    }
}

