import org.apache.spark.graphx._
val myVertices = sc.makeRDD(Array(
    (1L, "Ann"),
    (2L, "Bill"),
    (3L, "Charles"),
    (4L, "Diane"),
    (5L, "Went to gym this morning")
))

val myEdges = sc.makeRDD(Array(
    Edge(1L, 2L, "is-friends-with"),
    Edge(2L, 3L, "is-friends-with"),
    Edge(3L, 4L, "is-friends-with"),
    Edge(3L, 1L, "is-friends-with"),
    Edge(4L, 1L, "is-friends-with"),
    Edge(1L, 5L, "Wrote-status")
))

val myGraph = Graph(myVertices, myEdges)
myGraph.pageRank(0.001).vertices.foreach(println)