package com.example.proyectodispositivosmoviles

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_producto.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductoActivity : AppCompatActivity() {

    private lateinit var database: AppBasedeDatos
    private lateinit var producto: Producto
    private lateinit var productoLiveData: LiveData<Producto>
    private val EDIT_ACTIVITY = 49

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_producto)

        database = AppBasedeDatos.getDataBase(this)

       val idproducto = intent.getIntExtra("id",0)

        val imageUri = ImageController.getImageUri(this, idproducto.toLong())
        imageView2.setImageURI(imageUri)

        productoLiveData = database.productos().get(idproducto)

        productoLiveData.observe(this, Observer {
            producto=it

            nombre_producto.text = producto.nombre
            precio2.text = "$${producto.precio}"
            detalles_producto.text=producto.descripcion



        })



    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.producto_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.editItem ->{
                val intent=Intent(this,NuevoProductoActivity::class.java)
                intent.putExtra("producto",producto)
                startActivityForResult(intent, EDIT_ACTIVITY)

            }

            R.id.deleteitem ->{
                productoLiveData.removeObservers(this)

                CoroutineScope(Dispatchers.IO).launch {
                    database.productos().delete(producto)
                    ImageController.deleteImage(this@ProductoActivity, producto.idProducto.toLong())
                    this@ProductoActivity.finish()
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when {
            requestCode == EDIT_ACTIVITY && resultCode == Activity.RESULT_OK -> {
                imageView2.setImageURI(data!!.data)
            }
        }
    }
}