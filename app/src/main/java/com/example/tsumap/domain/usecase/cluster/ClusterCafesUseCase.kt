package com.example.tsumap.domain.usecase.cluster

import kotlin.math.abs
import kotlin.math.max

data class CafeCluster(
    val id: Int, val medoid: Pair<Int, Int>, val members: List<Pair<Int, Int>>
)

data class ClusteringResult(
    val clusters: List<CafeCluster>
)

class ClusterCafesUseCase {

    private fun dist(current: Pair<Int, Int>, goal: Pair<Int, Int>): Int {
        val dx = abs(goal.first - current.first)
        val dy = abs(goal.second - current.second)
        return max(dx, dy)
    }

    fun invoke(
        cafes: List<Pair<Int, Int>>, k: Int, nInit: Int = 10, maxIter: Int = 100
    ): ClusteringResult {
        val (labels, medoidIndices) = kMedoids(cafes, k, nInit, maxIter)

        val clusters = medoidIndices.mapIndexed { clusterId, medoidIdx ->
            val members = cafes.indices.filter { labels[it] == clusterId }.map { cafes[it] }
            CafeCluster(id = clusterId, medoid = cafes[medoidIdx], members = members)
        }

        return ClusteringResult(clusters = clusters)
    }

    private fun kMedoids(
        cafes: List<Pair<Int, Int>>, k: Int, nInit: Int, maxIter: Int
    ): Pair<IntArray, List<Int>> {
        val n = cafes.size
        var bestLabels = IntArray(n)
        var bestMedoids = emptyList<Int>()
        var bestCost = Long.MAX_VALUE

        repeat(nInit) {
            val medoids = (0 until n).shuffled().take(k).toMutableList()

            var labels = IntArray(n)

            for (iter in 0 until maxIter) {
                labels = IntArray(n) { i ->
                    medoids.indices.minBy { mi -> dist(cafes[i], cafes[medoids[mi]]) }
                }

                val newMedoids = medoids.indices.map { clusterId ->
                    val members = (0 until n).filter { labels[it] == clusterId }
                    if (members.isEmpty()) return@map medoids[clusterId]

                    members.minBy { candidate ->
                        members.sumOf { other -> dist(cafes[candidate], cafes[other]).toLong() }
                    }
                }

                if (newMedoids.toSet() == medoids.toSet()) break

                medoids.clear()
                medoids.addAll(newMedoids)
            }

            val cost = (0 until n).sumOf { i ->
                dist(cafes[i], cafes[medoids[labels[i]]]).toLong()
            }

            if (cost < bestCost) {
                bestCost = cost
                bestLabels = labels
                bestMedoids = medoids.toList()
            }
        }

        return bestLabels to bestMedoids
    }
}