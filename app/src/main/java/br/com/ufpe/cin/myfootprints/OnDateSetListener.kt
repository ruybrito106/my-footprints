package br.com.ufpe.cin.myfootprints

internal interface OnDateSetListener {
    fun onDateSet(type: String?, year: Int, month: Int, day: Int)
}