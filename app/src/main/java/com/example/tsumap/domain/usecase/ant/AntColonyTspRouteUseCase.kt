package com.example.tsumap.domain.usecase.ant

import com.example.tsumap.domain.usecase.astar.AStarUseCase
import com.example.tsumap.domain.usecase.genetic.TspResult
import kotlin.math.pow

class AntColonyTspRouteUseCase(
    grid: Array<IntArray>
) {
    private val astar = AStarUseCase(grid)

    fun invoke(points: List<Pair<Int, Int>>): TspResult {
        val n = points.size
        val distMatrix = buildDistanceMatrix(points)
        val pathMatrix = buildPathMatrix(points, n)
        val order = (if (n <= 2) listOf(0, 1) else acoTsp(n, distMatrix)) + listOf(0)
        val fullPath = stitchPaths(order, pathMatrix)
        val totalDist = calcTotalDist(order, distMatrix)

        return TspResult(
            order = order.dropLast(1).map { points[it] },
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

    private fun acoTsp(
        n: Int,
        dist: Array<IntArray>,
        nAnts: Int = n.coerceAtLeast(10),
        iterations: Int = 200,
        alpha: Double = 1.0,
        beta: Double = 3.0,
        rho: Double = 0.15,
        q: Double = 100.0
    ): List<Int> {
        val pheromone = Array(n) { DoubleArray(n) { 1.0 } }
        val eta = Array(n) { i ->
            DoubleArray(n) { j ->
                if (i == j || dist[i][j] <= 0) 0.0 else 1.0 / dist[i][j]
            }
        }

        var bestRoute = greedyRoute(n, dist)
        var bestDistance = routeDist(bestRoute, dist)
        depositOnRoute(bestRoute, bestDistance, pheromone, q)

        repeat(iterations) {
            val allRoutes = List(nAnts) { buildAntRoute(n, pheromone, eta, alpha, beta) }
            val allDistances = allRoutes.map { routeDist(it, dist) }

            for (i in 0 until n)
                for (j in 0 until n)
                    pheromone[i][j] = (pheromone[i][j] * (1.0 - rho)).coerceAtLeast(1e-10)

            allRoutes.forEachIndexed { idx, route -> depositOnRoute(route, allDistances[idx], pheromone, q) }
            depositOnRoute(bestRoute, bestDistance, pheromone, q * 2)

            val iterBestIdx = allDistances.indexOf(allDistances.min())
            if (allDistances[iterBestIdx] < bestDistance) {
                bestDistance = allDistances[iterBestIdx]
                bestRoute = allRoutes[iterBestIdx]
            }
        }

        return bestRoute
    }

    private fun buildAntRoute(
        n: Int,
        pheromone: Array<DoubleArray>,
        eta: Array<DoubleArray>,
        alpha: Double,
        beta: Double
    ): List<Int> {
        val visited = BooleanArray(n)
        val route = mutableListOf<Int>()
        var current = (0 until n).random()
        visited[current] = true
        route.add(current)

        repeat(n - 1) {
            val next = selectNextCity(current, visited, pheromone, eta, alpha, beta)
            visited[next] = true
            route.add(next)
            current = next
        }

        return route
    }

    private fun selectNextCity(
        current: Int,
        visited: BooleanArray,
        pheromone: Array<DoubleArray>,
        eta: Array<DoubleArray>,
        alpha: Double,
        beta: Double
    ): Int {
        val n = visited.size
        val scores = DoubleArray(n)
        var total = 0.0

        for (j in 0 until n) {
            if (visited[j]) continue
            val score = pheromone[current][j].pow(alpha) * eta[current][j].pow(beta)
            scores[j] = score
            total += score
        }

        if (total == 0.0) return (0 until n).first { !visited[it] }

        var r = Math.random() * total
        for (j in 0 until n) {
            if (visited[j]) continue
            r -= scores[j]
            if (r <= 0.0) return j
        }

        return (0 until n).last { !visited[it] }
    }

    private fun depositOnRoute(route: List<Int>, dist: Int, pheromone: Array<DoubleArray>, q: Double) {
        val delta = q / dist
        for (i in 0 until route.size - 1) {
            val a = route[i]
            val b = route[i + 1]
            pheromone[a][b] += delta
            pheromone[b][a] += delta
        }
    }

    private fun greedyRoute(n: Int, dist: Array<IntArray>): List<Int> {
        val visited = BooleanArray(n)
        val route = mutableListOf(0)
        visited[0] = true
        repeat(n - 1) {
            val current = route.last()
            val next = (0 until n).filter { !visited[it] }.minBy { dist[current][it] }
            route.add(next)
            visited[next] = true
        }
        return route
    }

    private fun routeDist(route: List<Int>, dist: Array<IntArray>): Int {
        var total = 0
        for (i in 0 until route.size - 1) total += dist[route[i]][route[i + 1]]
        return total
    }
}