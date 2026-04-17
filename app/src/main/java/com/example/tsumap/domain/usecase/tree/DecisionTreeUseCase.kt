package com.example.tsumap.domain.usecase.tree

import com.example.tsumap.domain.model.tree.TreeNode

data class DiningAnswers(
    val location: String,
    val budget: String,
    val timeAvailable: String,
    val foodType: String,
    val queueTolerance: String,
    val weather: String
)

data class ClassificationResult(
    val place: String,
    val path: List<String>
)

class DecisionTreeUseCase(private val tree: TreeNode) {
    fun invoke(answers: DiningAnswers): ClassificationResult {
        val path = mutableListOf<String>()
        val place = classify(tree, answers, path)
        return ClassificationResult(place = place, path = path)
    }

    private fun classify(node: TreeNode, answers: DiningAnswers, path: MutableList<String>): String {
        return when (node) {
            is TreeNode.Leaf -> {
                path.add("→ ${node.label}")
                node.label
            }
            is TreeNode.Decision -> {
                val value = getAnswer(answers, node.attribute)
                path.add("${node.attribute} = $value")
                val child = if (value != null) node.children[value] else null
                classify(child ?: TreeNode.Leaf(node.default), answers, path)
            }
        }
    }

    private fun getAnswer(answers: DiningAnswers, attribute: String): String? {
        return when (attribute) {
            "location" -> answers.location
            "budget" -> answers.budget
            "time_available" -> answers.timeAvailable
            "food_type" -> answers.foodType
            "queue_tolerance" -> answers.queueTolerance
            "weather" -> answers.weather
            else -> null
        }
    }
}