package com.revtekk.tree

import kotlin.random.Random

data class Treap<K: Comparable<K>, V>(private var root: TreapNode<K, V>? = null) {
    fun insert(key: K, value: V) {
        val priority = Random.nextInt()
        this.root = insert(key, value, priority, this.root)
    }

    private fun insert(key: K, value: V, priority: Int, current: TreapNode<K, V>?): TreapNode<K, V> {
        if (current == null) {
            return TreapNode(key, value, priority)
        }

        // Updating a key that already exists -- no need to adjust priority
        if (current.key == key) {
            current.value = value
            return current
        }

        if (current.key > key) {
            current.left = insert(key, value, priority, current.left)

            // Fix min-heap violation (left case)
            if (current.left!!.priority < current.priority) {
                return rotateRight(current)
            }
        } else {
            current.right = insert(key, value, priority, current.right)

            // Fix min-heap violation (right case)
            if (current.right!!.priority < current.priority) {
                return rotateLeft(current)
            }
        }

        // If we hit this case, then min-heap property was not violated,
        // so we don't need to do anything else
        return current
    }

    private fun rotateRight(current: TreapNode<K, V>): TreapNode<K, V> {
        val nCurrent = current.left!!
        val r1 = nCurrent.right

        nCurrent.right = current
        current.left = r1

        return nCurrent
    }

    private fun rotateLeft(current: TreapNode<K, V>): TreapNode<K, V> {
        val nCurrent = current.right!!
        val l1 = nCurrent.left

        nCurrent.left = current
        current.right = l1

        return nCurrent
    }

    fun get(key: K): V? {
        return get(key, root)
    }

    private fun get(key: K, current: TreapNode<K, V>?): V? {
        if (current == null) {
            return null
        }

        if (current.key == key) {
            return current.value
        }

        if (current.key > key) {
            return get(key, current.left)
        } else {
            return get(key, current.right)
        }
    }

    fun height(): Int {
        return height(root)
    }

    private fun height(node: TreapNode<K, V>?): Int {
        return if (node == null) 0 else 1 + maxOf(height(node.left), height(node.right))
    }
}

data class TreapNode<K: Comparable<K>, V>(val key: K, var value: V, val priority: Int,
                                          var left: TreapNode<K, V>? = null, var right: TreapNode<K, V>? = null)
