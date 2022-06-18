package com.example.proyectodispositivosmoviles

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.activity_nuevo_producto.*
import kotlinx.android.synthetic.main.activity_producto.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NuevoProductoActivity : AppCompatActivity() {

    private val SELECT_ACTIVITY= 50
    private var imageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuevo_producto)

        var idProducto: Int? = null

        if(intent.hasExtra("producto")){
            val producto= intent.extras?.getSerializable("producto") as Producto

            nombreet.setText(producto.nombre)
            precioedit.setText(producto.precio.toString())
            descripcionedit.setText(producto.descripcion)
            idProducto = producto.idProducto

            val imageUri = ImageController.getImageUri(this, idProducto.toLong())
            imagenselect.setImageURI(imageUri)
        }

        val database= AppBasedeDatos.getDataBase(this)

        savebtn.setOnClickListener {
            val nombre= nombreet.text.toString()
            val precio=precioedit.text.toString().toDouble()
            val descripcion=descripcionedit.text.toString()

            val producto=Producto(nombre,precio,descripcion,R.drawable.ic_launcher_background)

            if(idProducto != null){
                CoroutineScope(Dispatchers.IO).launch {
                    producto.idProducto=idProducto
                    database.productos().update(producto)

                    imageUri?.let {
                        val intent = Intent()
                        intent.data = it
                        setResult(Activity.RESULT_OK, intent)
                        ImageController.saveImage(this@NuevoProductoActivity, idProducto.toLong(), it)
                    }

                    this@NuevoProductoActivity.finish()
                }
            }else{
                CoroutineScope(Dispatchers.IO).launch {
                    val id = database.productos().insertAll(producto)[0]

                    imageUri?.let {
                        ImageController.saveImage(this@NuevoProductoActivity, id, it)
                    }

                    this@NuevoProductoActivity.finish()
                }
            }
        }

        imagenselect.setOnClickListener {
            ImageController.selectPhotoFromGallery(this, SELECT_ACTIVITY)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when {
            requestCode == SELECT_ACTIVITY && resultCode == Activity.RESULT_OK -> {
                imageUri = data!!.data

                imagenselect.setImageURI(imageUri)
            }
        }
    }
}