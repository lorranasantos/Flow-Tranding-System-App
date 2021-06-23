package com.example.flowtrandingsystem.gui.model

import java.io.Serializable

data class Sale(
    var payment_method_id: Int = 0,
    var branch_id: Int = 0,
    var items: Array<Itens>
) : Serializable