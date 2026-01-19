package com.revtekk

import com.revtekk.tree.Treap

fun main() {

    val treap = Treap<Int, Int>()

    for (i in 1..100000) {
        treap.insert(i, i)
    }

    for (i in 1..100000) {
        assert(treap.get(i) != null)
    }

    println(treap.height())
}
