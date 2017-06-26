package fitnesspro.rmse;

import java.sql.ResultSet;
import java.sql.SQLException;

import fitnesspro.db.Database;

public class RMSECalculator {
    Database database;
    int numValues;
    double sumSquaredValues;
    String ratingTableName;

    public RMSECalculator() {
        this.database = null;
        this.numValues = 0;
        this.sumSquaredValues = 0.0;
        this.ratingTableName = "ratings";
    }

    public RMSECalculator(Database database) {
        this();
        this.database = database;
    }

    public RMSECalculator(Database database, String ratingTableName) {
        this(database);
        this.ratingTableName = ratingTableName;
    }

    public void add(double realRating, double prediction) {
        double delta = realRating - prediction;
        sumSquaredValues += delta * delta;
        numValues++;
    }

    public void add(int uid, int eid, double prediction) throws SQLException {
        double realRating = getRealRating(uid, eid);
        add(realRating, prediction);
    }

    public double rmse() {
        return Math.sqrt(sumSquaredValues / numValues);
    }

    private double getRealRating(int uid, int eid) throws SQLException {
        String query = "SELECT rating FROM " + ratingTableName + " WHERE uid = " + uid + " AND eid = " + eid;
        ResultSet result = null;
        if(database != null) {
            result = database.queryDB(query);
            if (result.next())
                return (double) result.getInt(1);
        }
        else {
            System.err.println("Database not set. Please initialize RMSECalculator with a Database object.");
        }
        return 0.0;
    }
}
