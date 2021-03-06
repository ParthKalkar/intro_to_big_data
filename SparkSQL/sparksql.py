# -*- coding: utf-8 -*-
"""SparkSQL.ipynb

Automatically generated by Colaboratory.

Original file is located at
    https://colab.research.google.com/drive/1otw9q-YOC5JFTTnpdu1gBfJ6hxwoFNXF
"""

from pyspark.sql import SparkSession

# since everyone will be using cluster at the same time
# let's make sure that everyone has resource. that is why 
# the configuration uses dynamic resource allocation and
# maximum 1 executor 
spark = SparkSession \
    .builder \
    .appName("Python Spark SQL basic example") \
    .config("spark.dynamicAllocation.enabled", "true")\
    .config("spark.dynamicAllocation.shuffleTracking.enabled", "true")\
    .config("spark.dynamicAllocation.maxExecutors", "1")\
    .getOrCreate()

songs_df = spark.read.load("./train_triplets.txt",
                     format="csv", sep="\t", inferSchema="true", 
                     header="false")

songs_df.printSchema()

songs_df.createOrReplaceTempView("songs")
songs_df = songs_df.withColumnRenamed("_c0", "user")\
                   .withColumnRenamed("_c1", "song")\
                   .withColumnRenamed("_c2", "play_count")
songs_df.printSchema()

played_more_than_10_times = spark.sql("select song from songs where play_count > 10")

played_more_than_10_times.count()

username = "b80344d063b5ccb3212f76538f3d9e43d87dca9e"
played_by_user = spark.sql(f"select song from songs where user=\"{username}\"")
played_by_user.count()

first_ten_entries = spark.sql(f"select user from songs limit 10")
print(first_ten_entries.collect()[0])

username = "b80344d063b5ccb3212f76538f3d9e43d87dca9e"
played_by_user_more_than_10_times = spark.sql(f"select song from songs where user=\"{username}\" and play_count > 10")
played_by_user_more_than_10_times.count()

"""## Yelp dataset"""

business = spark.read.json("./yelp-dataset/yelp_academic_dataset_business.json")
reviews = spark.read.json("./yelp-dataset/yelp_academic_dataset_review.json")
users = spark.read.json("./yelp-dataset/yelp_academic_dataset_user.json")
business.createOrReplaceTempView("business")
reviews.createOrReplaceTempView("reviews")
users.createOrReplaceTempView("users")

spark.sql("select state, count(state) as count from business group by state order by count(state) desc").show()

spark.sql("""
select count(distinct(*)) from (
    select explode(split(categories, \",\s*\")) as category from business
)
""").show()

spark.sql("""
select category, count(category) from 
    (
        select explode(split(categories, \",\s*\")) as category 
        from business where city=\"Phoenix\"
    )
group by category order by count(category) desc limit 10
""").show()

spark.sql("""
with business_category (select *, explode(split(categories, \",\s*\")) as category from business)
select categories from business where categories like '%Restaurant%' and categories like '%Chinese%'
""").show()

spark.sql("""
select 
    count(*) as friend_count 
from 
    users 
where 
    size(split(friends, \",\s*\")) > 1000
""").show()

spark.sql("""
with business_ratings as (
    select 
        business_id, year(to_date(date)) as year, avg(stars) as rating 
    from 
        reviews group by business_id, year(to_date(date))
),
business_2014 as (
    select 
        business_id, rating 
    from 
        business_ratings 
    where 
        year=2014
),
business_2017 as (
    select 
        business_id, rating 
    from 
        business_ratings where year=2017
)
select 
    business_2014.business_id, business_2014.rating, business_2017.rating 
from 
    business_2014 
inner join 
    business_2017 
on 
    business_2014.business_id=business_2017.business_id 
where 
    business_2017.rating < business_2014.rating 
""").show()

"""## Last query"""

spark.sql("""



with last_reviews(
    with chinese_restaurant(
    select * from business where categories like '%Chinese%' and categories like '%Restaurant%'
    )
    select user_id, max(to_date(date)) 
    from reviews join chinese_restaurant on reviews.business_id=chinese_restaurant.business_id
    group by user_id
)

select distinct(explode(split(friends, \",\s*\"))) as friend
from last_reviews join users on last_reviews.user_id=users.user_id

""").show()