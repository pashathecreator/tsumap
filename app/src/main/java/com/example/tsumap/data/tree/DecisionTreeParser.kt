package com.example.tsumap.data.tree

import com.example.tsumap.domain.model.tree.TreeNode
import com.google.gson.JsonObject

object DecisionTreeParser {

    fun parse(json: JsonObject): TreeNode {
        val tree = json.getAsJsonObject("tree")
        return parseNode(tree)
    }

    private fun parseNode(obj: JsonObject): TreeNode {
        return when (obj.get("type").asString) {
            "leaf" -> TreeNode.Leaf(
                label = obj.get("label").asString
            )
            else -> {
                val childrenObj = obj.getAsJsonObject("children")
                val children = childrenObj.keySet().associateWith { key ->
                    parseNode(childrenObj.getAsJsonObject(key))
                }
                TreeNode.Decision(
                    attribute = obj.get("attribute").asString,
                    default = obj.get("default").asString,
                    children = children
                )
            }
        }
    }
}