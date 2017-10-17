package template.spark

import org.apache.spark.sql.Dataset
import org.apache.spark.sql.functions._


final case class Rating(userId: Int, movieId: Int, rating: Double, timestamp: Int)

object Main extends InitSpark {
  def main(args: Array[String]) = {
    import spark.implicits._

    val version = spark.version
    println("SPARK VERSION = " + version)

    val sumHundred = spark.range(1, 101).reduce(_ + _)
    println(f"Sum 1 to 100 = $sumHundred")

    //val sourceFile = "hdfs://sandbox.hortonworks.com:8020/user/maria_dev/ratings.csv"
    //val sourceFile = "ratings.csv"
    val sourceFile = "/Users/jason/Downloads/ml-latest/ratings.csv"

    println("Reading from csv file: " + sourceFile)
    //val ratings: Dataset[Rating] = reader.csv(sourceFile).as[Rating]

    val ratings: Dataset[Rating] = reader.format("com.databricks.spark.csv").load(sourceFile).as[Rating]
    ratings.show(10)

    val averageRating = ratings.groupBy("movieId").avg("rating")
    averageRating.show(10)
    val counts = ratings.groupBy("movieId").count()
    counts.show(10)
    val averagesAndCount = counts.join(averageRating, "movieId")
    averagesAndCount.show(10)


    val topTen = averagesAndCount.orderBy("avg(rating)").show(10)
//    persons.show(2)
//    val averageAge = persons.agg(avg("age"))
//                     .first.get(0).asInstanceOf[Double]
//    println(f"Average Age: $averageAge%.2f")

    close
  }
}
