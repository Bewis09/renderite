package net.bewis09.renderite.logic

class CacheBox<B, T>(val compute: (B) -> T, vararg val params: () -> Any?) {
    var cache: T? = null
    var cached = false
    val paramCache = hashMapOf<() -> Any?, Any?>()

    operator fun invoke(p: B): T {
        for (param in params) {
            val tmp = param()

            if (tmp != paramCache[param]) {
                paramCache[param] = tmp
                cached = false
            }
        }

        if (!cached) {
            cached = true
            cache = compute(p)
        }

        @Suppress("UNCHECKED_CAST")
        return cache as T
    }
}