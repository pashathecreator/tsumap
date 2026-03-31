package com.example.tsumap.useCases

import java.util.PriorityQueue
import kotlin.math.abs
import kotlin.math.max

class AStarUseCase(
    private val grid: Array<IntArray>
) {
    private val rows = grid.size
    private val cols = grid[0].size

    private val directions = arrayOf(
        Pair(0, 1),
        Pair(0, -1),
        Pair(1, 0),
        Pair(-1, 0),
        Pair(1, 1),
        Pair(-1, 1),
        Pair(1, -1),
        Pair(-1, -1)
    )

    fun invoke(start: Pair<Int, Int>, end: Pair<Int, Int>): List<Pair<Int, Int>> {
        val toVisit = PriorityQueue<Node>(compareBy { it.f })
        val visited = Array(rows) { BooleanArray(cols) }
        val bestCost = Array(rows) { IntArray(cols) { Int.MAX_VALUE } }

        val startNode = Node(start.first, start.second, 0, heuristic(start, end))
        toVisit.add(startNode)
        bestCost[start.first][start.second] = 0

        while (toVisit.isNotEmpty()) {
            val current = toVisit.poll()

            if (current.x == end.first && current.y == end.second) {
                return reconstructPath(current)
            }

            if (visited[current.x][current.y]) continue
            visited[current.x][current.y] = true

            for ((dx, dy) in directions) {
                val nx = current.x + dx
                val ny = current.y + dy

                if (!isWalkable(nx, ny)) continue
                if (grid[nx][ny] == 0) continue
                if (visited[nx][ny]) continue

                val currentCost = current.g + 1

                if (currentCost < bestCost[nx][ny]) {
                    bestCost[nx][ny] = currentCost
                    val h = heuristic(Pair(nx, ny), end)
                    toVisit.add(Node(nx, ny, currentCost, h, current))
                }
            }
        }

        return emptyList()
    }

    private fun reconstructPath(node: Node): List<Pair<Int, Int>> {
        val path = mutableListOf<Pair<Int, Int>>()
        var current: Node? = node
        while (current != null) {
            path.add(Pair(current.x, current.y))
            current = current.parent
        }
        return path.reversed()
    }

    private fun heuristic(current: Pair<Int, Int>, goal: Pair<Int, Int>): Int {
        val dx = abs(goal.first - current.first)
        val dy = abs(goal.second - current.second)
        return max(dx, dy)
    }

    private fun isWalkable(x: Int, y: Int): Boolean {
        return x in 0 until rows && y in 0 until cols && grid[x][y] > 0
    }
}

data class Node(
    val x: Int,
    val y: Int,
    var g: Int,
    var h: Int,
    var parent: Node? = null
) {
    val f: Int
        get() = g + h
}