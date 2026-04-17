package com.example.tsumap.domain.model.tree

sealed class TreeNode {
    data class Decision(
        val attribute: String,
        val default: String,
        val children: Map<String, TreeNode>
    ) : TreeNode()

    data class Leaf(
        val label: String
    ) : TreeNode()
}