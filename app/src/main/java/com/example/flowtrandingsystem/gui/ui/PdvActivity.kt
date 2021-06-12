package com.example.flowtrandingsystem.gui.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flowtrandingsystem.R
import com.example.flowtrandingsystem.gui.adapter.BarCodeAdapter
import com.example.flowtrandingsystem.gui.adapter.ItensInventoryAdatpter
import com.example.flowtrandingsystem.gui.api.ProductCalls
import com.example.flowtrandingsystem.gui.api.RetrofitApi
import com.example.flowtrandingsystem.gui.http.HttpHelper
import com.example.flowtrandingsystem.gui.model.Product
import com.example.flowtrandingsystem.gui.model.RegisterClientPdv
import com.google.gson.Gson
import org.jetbrains.anko.doAsync
import retrofit2.Call
import retrofit2.Response

class PdvActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var rvItens: RecyclerView
    lateinit var adapterItensList: BarCodeAdapter

    private lateinit var buttonAddClient: Button
    private lateinit var editCpf: EditText
    private lateinit var buttonSaveClient: Button
    private lateinit var buttonCancelClient: Button
    private lateinit var buttonAddDiscount: Button
    private lateinit var editDiscount: EditText
    private lateinit var imgCameraCode: ImageView
    private lateinit var buttonAddCode: Button

    private lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pdv)

        rvItens = findViewById(R.id.recycler_view_product_sale)

        rvItens.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        adapterItensList = BarCodeAdapter(this)

        rvItens.adapter = adapterItensList

        buttonAddClient = findViewById(R.id.pdv_client_register)
        buttonAddClient.setOnClickListener(this)

        buttonAddDiscount = findViewById(R.id.pdv_add_discount)
        buttonAddDiscount.setOnClickListener(this)

        imgCameraCode = findViewById(R.id.img_camera_code)
        imgCameraCode.setOnClickListener(this)

        buttonAddCode = findViewById(R.id.add_code)
        buttonAddCode.setOnClickListener(this)

        //recuperar o token do sharedPreferences
        val prefs: SharedPreferences =
            this@PdvActivity.getSharedPreferences("preferencias", Context.MODE_PRIVATE)

        val retrivedToken =
            prefs.getString("TOKEN", "Nada foi recebido")

        Toast.makeText(this@PdvActivity, "RETRIEVED: ${retrivedToken}", Toast.LENGTH_LONG).show()

    }

    override fun onClick(v: View) {
        if (v.id == R.id.pdv_client_register){
            openClientRegister()
        }else if(v.id == R.id.pdv_add_discount) {
            openAddDiscount()
        }else if(v.id == R.id.img_camera_code) {
            val scanScreen = Intent(this, ScannerActivity::class.java)
            startActivity(scanScreen)
        }else if(v.id == R.id.add_code) {
            addProductByCode()
        }else{
            Toast.makeText(this, "Nada foi clicado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addProductByCode() {

        //recuperar o token do sharedPreferences
        val prefs: SharedPreferences =
            this@PdvActivity.getSharedPreferences("preferencias", Context.MODE_PRIVATE)

        val retrivedToken =
            prefs.getString("TOKEN", "Nada foi recebido")

        val editCode = findViewById<EditText>(R.id.pdv_activity_product_code)
        val editUnitValue = findViewById<EditText>(R.id.pdv_prices)
        val editQtde = findViewById<EditText>(R.id.pdv_qtde_sale)

        var itemProduct: Product

        val retrofit = RetrofitApi.getRetrofit()
        val productBarCode = retrofit.create(ProductCalls::class.java)

        val call = productBarCode.getBarProduct(editCode.text.toString(), "Bearer ${retrivedToken}")

        call.enqueue(object : retrofit2.Callback<Product>{

            override fun onFailure(call: Call<Product>, t: Throwable) {
                Toast.makeText(this@PdvActivity, "Ops! Acho que ocorreu um problema.", Toast.LENGTH_SHORT).show()
                Log.e("ERRO_CONEXÃO", t.message.toString())
            }

            override fun onResponse(call: Call<Product>, response: Response<Product>) {
                itemProduct = response.body()!!

                itemProduct.cost_per_item = editUnitValue.text.toString().toDouble()
                itemProduct.total_quantity = editQtde.text.toString().toInt()

                val itemTotalValue = itemProduct.total_quantity * itemProduct.cost_per_item

                adapterItensList.updateListProducts(itemProduct)

                Toast.makeText(this@PdvActivity, "Numero Item: ${itemProduct.id} " +
                        "Codigo: ${itemProduct.bar_code} " +
                        "Qtde: ${itemProduct.total_quantity} " +
                        "Produto: ${itemProduct.product_name} " +
                        "Valor Unitario: ${itemProduct.cost_per_item} " +
                        "Valor Total: ${itemTotalValue}", Toast.LENGTH_SHORT).show()


            }
        })
    }

    private fun openAddDiscount() {
        val alertDialog = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.add_discount_pdv, null)
        alertDialog.setView(view)

        editDiscount = view.findViewById(R.id.edit_add_discount_pdv)
        buttonSaveClient = view.findViewById(R.id.button_save_discount)
        buttonCancelClient = view.findViewById(R.id.button_cancel_discount)
    }

    private fun openClientRegister() {
        val alertDialog = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.client_register_pdv, null)
        alertDialog.setView(view)

        editCpf = view.findViewById(R.id.edit_client_register_cpf)
        buttonSaveClient = view.findViewById(R.id.button_save_client_register)
        buttonCancelClient = view.findViewById(R.id.button_cancel_client_register)

        buttonSaveClient.setOnClickListener{
            val newClient = RegisterClientPdv()

            newClient.cpf = editCpf.text.toString()

            val gson = Gson()
            val clientJson = gson.toJson(newClient)

            doAsync {
                val http = HttpHelper()
                http.postCostumer(clientJson)
            }
        }

        buttonCancelClient.setOnClickListener(this)

        dialog = alertDialog.create()
        dialog.setCancelable(false)
        dialog.show()


    }

}

