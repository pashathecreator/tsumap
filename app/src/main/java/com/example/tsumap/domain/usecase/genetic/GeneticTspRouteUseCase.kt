package com.example.tsumap.domain.usecase.genetic

import com.example.tsumap.domain.usecase.astar.AStarUseCase
import kotlin.math.abs

class GeneticTspRouteUseCase(
    private val grid: Array<IntArray>
) {
    private val astar = AStarUseCase(grid)

    fun invoke(points: List<Pair<Int, Int>>): TspResult {
        val n = points.size

        val distMatrix = buildDistanceMatrix(points)
        val pathMatrix = buildPathMatrix(points, n)

        val order = if (n <= 2) listOf(0, 1) else geneticTsp(n, distMatrix)

        val fullPath = stitchPaths(order, pathMatrix)
        val totalDist = calcTotalDist(order, distMatrix)

        return TspResult(
            order = order.map { points[it] },
            fullPath = fullPath,
            totalDistance = totalDist
        )
    }

    private fun buildDistanceMatrix(points: List<Pair<Int, Int>>): Array<IntArray> {
        val n = points.size
        val dist = Array(n) { IntArray(n) { Int.MAX_VALUE / 2 } }
        for (i in 0 until n) {
            dist[i][i] = 0
            for (j in i + 1 until n) {
                val path = astar.invoke(points[i], points[j])
                val d = if (path.isEmpty()) Int.MAX_VALUE / 2 else path.size - 1
                dist[i][j] = d
                dist[j][i] = d
            }
        }
        return dist
    }

    private fun buildPathMatrix(
        points: List<Pair<Int, Int>>,
        n: Int
    ): Array<Array<List<Pair<Int, Int>>>> {
        val paths = Array(n) { Array(n) { emptyList<Pair<Int, Int>>() } }
        for (i in 0 until n) {
            for (j in i + 1 until n) {
                val path = astar.invoke(points[i], points[j])
                paths[i][j] = path
                paths[j][i] = path.reversed()
            }
        }
        return paths
    }


    private fun stitchPaths(
        order: List<Int>,
        pathMatrix: Array<Array<List<Pair<Int, Int>>>>
    ): List<Pair<Int, Int>> {
        val result = mutableListOf<Pair<Int, Int>>()
        for (i in 0 until order.size - 1) {
            val segment = pathMatrix[order[i]][order[i + 1]]
            if (segment.isEmpty()) continue
            if (result.isEmpty()) result.addAll(segment)
            else result.addAll(segment.drop(1))
        }
        return result
    }

    private fun calcTotalDist(order: List<Int>, dist: Array<IntArray>): Int {
        var total = 0
        for (i in 0 until order.size - 1) total += dist[order[i]][order[i + 1]]
        return total
    }

    private fun geneticTsp(
        n: Int,
        dist: Array<IntArray>,
        popSize: Int = 80,
        generations: Int = 400,
        mutationRate: Double = 0.02,
        eliteSize: Int = 8,
        tournamentSize: Int = 4
    ): List<Int> {
        var population = List(popSize) { (0 until n).shuffled() }
        var distances = population.map { routeDist(it, dist) }
        var bestRoute = population[distances.indexOf(distances.min())]
        var bestDist = distances.min()

        repeat(generations) {
            population = nextGeneration(population, distances, dist, mutationRate, eliteSize, tournamentSize)
            distances = population.map { routeDist(it, dist) }
            val genBest = distances.min()
            if (genBest < bestDist) {
                bestDist = genBest
                bestRoute = population[distances.indexOf(genBest)]
            }
        }
        return bestRoute
    }

    private fun routeDist(route: List<Int>, dist: Array<IntArray>): Int {
        var total = 0
        for (i in 0 until route.size - 1) total += dist[route[i]][route[i + 1]]
        return total
    }

    private fun tournamentSelect(population: List<List<Int>>, distances: List<Int>, k: Int): List<Int> {
        val indices = (population.indices).shuffled().take(k)
        return population[indices.minBy { distances[it] }]
    }

    private fun orderedCrossover(p1: List<Int>, p2: List<Int>): List<Int> {
        val n = p1.size
        val (start, end) = listOf((0 until n).random(), (0 until n).random()).sorted()
        val child = MutableList(n) { -1 }
        for (i in start..end) child[i] = p1[i]
        val remaining = p2.filter { it !in child }
        var idx = 0
        for (i in child.indices) if (child[i] == -1) child[i] = remaining[idx++]
        return child
    }

    private fun inversionMutation(route: List<Int>, rate: Double): List<Int> {
        if (Math.random() >= rate) return route
        val r = route.toMutableList()
        val (i, j) = listOf((r.indices).random(), (r.indices).random()).sorted()
        r.subList(i, j + 1).reverse()
        return r
    }

    private fun getElite(population: List<List<Int>>, distances: List<Int>, size: Int): List<List<Int>> =
        population.indices.sortedBy { distances[it] }.take(size).map { population[it] }

    private fun nextGeneration(
        population: List<List<Int>>, distances: List<Int>, dist: Array<IntArray>,
        mutationRate: Double, eliteSize: Int, tournamentSize: Int
    ): List<List<Int>> {
        val next = mutableListOf<List<Int>>()
        next.addAll(getElite(population, distances, eliteSize))
        while (next.size < population.size) {
            val child = orderedCrossover(
                tournamentSelect(population, distances, tournamentSize),
                tournamentSelect(population, distances, tournamentSize)
            )
            next.add(inversionMutation(child, mutationRate))
        }
        return next
    }
}

data class TspResult(
    val order: List<Pair<Int, Int>>,
    val fullPath: List<Pair<Int, Int>>,
    val totalDistance: Int
)