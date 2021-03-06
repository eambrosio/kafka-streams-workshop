# Learn kafka streams by making the tests pass

The goal of this project is to provide a bunch of tests with some challenges to be completed for anyone who wants to improve his/her Kafka streams skills.

The tests will start up a whole Kafka ecosystem infrastructure for you (Apache Kafka, Apache Zookeeper and Schema Registry) so, this way, you only need to focus on the fun part: play around with the Kafka Streams API!

I will be adding more challenges so please stay tuned. Collaborations, ideas on new challenges & PRs are more than welcome.


# The challenges

## 1. Filtering VIP clients


<details>
  <summary>Click to expand!</summary>
  
In this challenge you will need to filter a stream of client events. These events will follow the following structure:

```
    {"name": "name", "type": "string"},
    {"name": "age",  "type": "int"},
    {"name": "vip",  "type": "boolean"}
```

To make the test pass you must get rid of all the clients who are not VIPs (vip field equals to false).

### Instructions

1. Start by executing the test (FilterTopologySpec) to make sure that the test runs smoothly. You can execute the test in your IDE or by executing the following console command:

```
mvn test
```

The test should execute properly but it also should fail (do not worry you need to make it pass).

2. Now it is time to make the test pass! To do so goes to the FilterTopologyBuilder class (within the filtering package)
3. Add your code to the filterVIPClients method
4. Execute the test again until you get the green flag ;)
</details>

## 2. Enriching sale events

<details>
  <summary>Click to expand!</summary>
  
  In this challenge you will be facing a pretty common use case when working with streaming systems: enrich the events with information from a third-party system. In this scenario
  for the sake of simplicity we will be using an in-memory hashmap to enrich events that simulate sales. The challenge is quite simple, the events will follow the following structure:
  
  ```
      {"name": "amount", "type": "float"},
      {"name": "product",  "type": "string"},
      {"name": "storeid",  "type": "int"}
  ```
  
  On receiving an event you should check if the storeid exists within the storesInformation hashmap, if so, you should pick up the values storeAddress and storeCity from the hashmap, create a  
  new event with the following structure:
  
  ```
      {"name": "amount", "type": "float"},
      {"name": "product",  "type": "string"},
      {"name": "storeaddress",  "type": "string"},
      {"name": "storecity", "type": "string"}
  ```
  
  and send this event to the outputtopic. If the hashmap does not include the storeid of the event you should redirect this event to a error output topic.
  
  If you have any further questions we strongly recommend to have a look at the EnrichmentTopologySpec test.
  
  ### Instructions
  
  1. Start by executing the EnrichmentTopologySpec and check out that the tests run properly and fail
  2. Go to the EnrichmentTopologyBuilder class (the one within the enrichment package)
  3. Add your code within the createTopology method
  4. Execute the test again until it passes
</details>


## 3. Joining coupons and purchase events

<details>
  <summary>Click to expand!</summary>
  
  Let's say that you are working for an on-line retail company. The company wants to launch an innovative marketing campaign to apply discounts to products. The idea is to emit coupons or offers that will be available
  just for 5 minutes, this way the clients that want to take advantage of the discount should buy the product within the next 5 minutes after the coupon has been launched.
  
  To pass this challenge you have to join the coupons and purchase streams (taking into account the 5 minutes slot!!)
  
  See following the coupon events structure:
  
  ```
      {"name": "timestamp", "type": "long"},
      {"name": "productid",  "type": "string"},
      {"name": "discount",  "type": "float"}
  ```
  
  And this is the purchase events structure:
  
  ```
      {"name": "timestamp", "type": "long"},
      {"name": "productid",  "type": "string"},
      {"name": "productdescription",  "type": "string"},
      {"name": "amount",  "type": "float"}
  ```
  
  When you are dealing with this challenge take into account that the stream events will join by their key!
  
  ### Instructions
  
  1. Start by executing the JoinTopologySpec and check out that the tests run properly and fail
  2. Have a look at the GenericTimeStampExtractor that I have prepared for you (to better understand how we deal with message event times)
  3. Go to the JoinTopologyBuilder class (the one within the joining package)
  4. Add your code within the createTopology method (there are some tips that might help you)
  5. Execute the test again until it passes
</details>

## 4. Aggregating songs data


<details>
  <summary>Click to expand!</summary>
  
In this challenge you will need to aggregate a stream of song events. These events will follow the following structure:

```
    {"name": "title", "type": "string"},
    {"name": "duration", "type": "int"},
    {"name": "artist",  "type": "string"},
    {"name": "album",  "type": "string"},
    {"name": "genre", "type": "string"}
```

To make the test pass you must aggregate the songs by artist, let's say that our stream has just received the following songs:

```
new Song("Closer", 122, "Nine Inch Nails", "The Downward Spiral", "rock")
new Song("Heresy", 98, "Nine Inch Nails", "The Downward Spiral", "rock")
new Song("Wide Awake", 265, "Audioslave", "Revelations", "rock")
new Song("Wish", 112, "Nine Inch Nails", "Broken", "rock")
new Song("Until we fall", 215, "Audioslave", "Revelations", "rock")
```

As we said, our streaming app should aggregate the songs by artist (generating internally a Ktable) and should output the results to out output topic. On receiving the previous songs our output should be:

```
Nine Inch Nails     3
Audioslave          2
```

### Instructions

1. Start by executing the test (AggregateTopologySpec) to make sure that the test runs smoothly. The test should execute properly but it also should fail (do not worry you need to make it pass).
2. Now it is time to make the test pass! To do so goes to the AggregateTopologyBuilder class (within the aggregating package)
3. Add your code to the createTopology(...) method
4. Execute the test again until you get the green flag ;)
</details>

# Solutions

There is a solutions package where you will be able to find my proposed solution for the challenges. Try not to cheat and make the test pass on your own.

Do please let me know if you find a better solution by submitting your PRs.