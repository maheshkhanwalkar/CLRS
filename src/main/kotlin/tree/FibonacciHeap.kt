package com.revtekk.tree

import kotlin.math.floor
import kotlin.math.log
import kotlin.math.sqrt

class FibonacciHeap<K: Comparable<K>> {
    private var n = 0
    private var min: FibonacciNode<K>? = null

    private val PHI = (1 + sqrt(5.0)) / 2

    fun insert(key: K) {
        if (min == null) {
            // Set up root circularly linked list
            val node = FibonacciNode(key, 0)
            node.prev = node
            node.next = node

            // Only node, so its min by definition
            this.min = node
        } else {
            // Insert into the root list
            val node = FibonacciNode(key, 0)
            node.prev = node
            node.next = node

            node.insert(min!!)

            if (node.key < min!!.key) {
                min = node
            }
        }

        this.n += 1
    }

    fun min(): K {
        if (min == null) {
            throw NoSuchElementException("Heap is empty")
        }
        return min!!.key
    }

    fun merge(other: FibonacciHeap<K>) {
        // Nothing to merge if the other heap is empty
        if (other.min == null) {
            return
        }

        if (this.min == null) {
            // Copy over 'other' into this heap
            this.min = other.min
            this.n = other.n
        } else {
            // Merge the two heaps' root lists
            val tail1 = this.min!!.prev!!
            val tail2 = other.min!!.prev!!

            tail1.next = other.min!!
            other.min!!.prev = tail1

            tail2.next = this.min!!
            this.min!!.prev = tail2

            // Update the min node
            this.min = if (this.min!!.key < other.min!!.key) this.min else other.min
            this.n += other.n
        }

        // Destroy the other heap
        other.min = null
        other.n = 0
    }

    fun extractMin(): K {
        if (this.min == null) {
            throw NoSuchElementException("heap is empty")
        }

        val min = this.min!!

        // Add all of min's children to the root list
        for (child in min.children) {
            child.parent = null
            child.insert(this.min!!)
        }

        if (this.min!!.next == min) {
            // Heap is empty, so there is no min
            this.min = null
        } else {
            /*
             * Remove min from the root list and consolidate.
             *
             * We set the new min temporarily to the next node in the root list,
             * but this will be properly set in the consolidation step
             */
            this.min = min.next
            min.remove()
            consolidate()
        }

        this.n -= 1
        return min.key
    }

    fun size(): Int = this.n

    private fun consolidate() {
        // Size of the array is log_phi(n) -- as there will be at most log_phi(n) trees
        // with different degrees.
        val size = floor(log(this.n.toDouble(), PHI)).toInt()
        val A = Array<FibonacciNode<K>?>(size, { null })

        var currNode = this.min!!
        val seen = mutableSetOf<FibonacciNode<K>>()

        // Consolidate nodes with the same degree, repeatedly until we've seen all nodes
        do {
            val save = currNode
            var degree = currNode.degree

            while (A[degree] != null) {
                var otherNode = A[degree]!!
                if (currNode.key > otherNode.key) {
                    val tmp = currNode
                    currNode = otherNode
                    otherNode = tmp
                }

                link(currNode, otherNode)
                A[degree] = null
                degree++
            }

            A[degree] = currNode
            seen.add(currNode)

            currNode = save.next!!
        } while(!seen.contains(currNode))

        this.min = A.first { it != null }
        this.min!!.next = this.min
        this.min!!.prev = this.min

        val startPos = A.indexOf(this.min)

        // Rebuild the root list and determine the new min
        for (i in startPos + 1 until size) {
            // Ignore null nodes
            val currNode = A[i] ?: continue

            currNode.insert(this.min!!)
            if (currNode.key < this.min!!.key) {
                this.min = currNode
            }
        }
    }

    private fun link(parent: FibonacciNode<K>, child: FibonacciNode<K>) {
        /*
         * H: [...] <---> parent <---> [...]
         *               /
         *            child
         */
        child.remove()
        parent.children.add(child)
        parent.degree++
        parent.mark = false
    }
}

class FibonacciNode<K: Comparable<K>>(val key: K,
   var degree: Int,
   var parent: FibonacciNode<K>? = null, val children: MutableList<FibonacciNode<K>> = mutableListOf(),
   var prev: FibonacciNode<K>? = null, var next: FibonacciNode<K>? = null,
   var mark: Boolean = false,
) {
    fun insert(list: FibonacciNode<K>) {
        val prevLeft = list.prev!!
        list.prev = this
        this.prev = prevLeft
        this.next = list
        prevLeft.next = this
    }

    fun remove() {
        this.prev!!.next = this.next
        this.next!!.prev = this.prev
    }

    override fun toString(): String {
        return "FibonacciNode(key=$key, degree=$degree)"
    }
}
