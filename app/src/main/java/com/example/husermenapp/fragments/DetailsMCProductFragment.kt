package com.example.husermenapp.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.husermenapp.ProductActivity
import com.example.husermenapp.databinding.FragmentDetailsMercadoLibreBinding
import com.example.husermenapp.dataclasses.MCProduct
import com.example.husermenapp.dataclasses.Product
import com.example.husermenapp.utils.FragmentUtils.applyTextViewFormat
import com.example.husermenapp.fragments.basefragments.BaseItemDetailsFragment
import com.example.husermenapp.utils.MercadoLibre
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch
import java.io.Serializable

class DetailsMCProductFragment : BaseItemDetailsFragment<FragmentDetailsMercadoLibreBinding, MCProduct>() {
    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDetailsMercadoLibreBinding = FragmentDetailsMercadoLibreBinding.inflate(inflater, container, false)

    override fun setupTextViews() {
        mcProduct?.let {
            binding.tvName.text = applyTextViewFormat(it.name.toString())
            binding.tvCategoryValue.text = applyTextViewFormat(it.category.toString())
            binding.tvPriceValue.text = "$ " + it.price.toString()
            binding.tvStockValue.text = it.stock.toString() + " Producto/s Disponible/s"
            if (it.imageUrl != null) Glide.with(requireContext())
                .load(it.imageUrl)
                .transform(
                    MultiTransformation(
                        CenterCrop(),
                        RoundedCorners(16)
                    )
                )
                .into(binding.ivPicture)
        }
    }

    override fun EditItemFragment(): Fragment {
        error("This function have not to be called in this class.")
    }

    override fun <T : Serializable> setupEditItemFragment(selectedItem: T?): Fragment {
        error("This function have not to be called in this class.")
    }

    override var modelRef: String = "mcProducts"

    private var mcProduct: MCProduct? = null
    private var mcProductsRef: DatabaseReference = FirebaseDatabase.getInstance().getReference(modelRef)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mcProduct = it.getSerializable("selectedMCProduct") as? MCProduct
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTextViews()

        val mercadoLibre = MercadoLibre(requireContext())

        lifecycleScope.launch {
            mercadoLibre.fetchSalesData { salesData ->
                if (salesData.isEmpty()) {
                    binding.tvNoDataMessage.visibility = View.VISIBLE
                    binding.lineChart.visibility = View.GONE
                } else {
                    binding.tvNoDataMessage.visibility = View.GONE
                    binding.lineChart.visibility = View.VISIBLE

                    showChart(binding.lineChart, salesData)
                }
            }
        }
    }

    fun showChart(chart: LineChart, salesData: Map<String, Int>) {
        val entries = salesData.entries.mapIndexed { index, (date, value) ->
            Entry(index.toFloat(), value.toFloat())
        }

        val dataSet = LineDataSet(entries, "Ventas por Día").apply {
            color = Color.BLUE
            valueTextColor = Color.BLACK
            lineWidth = 2f
            setCircleColor(Color.RED)
        }

        chart.data = LineData(dataSet)
        chart.xAxis.valueFormatter = IndexAxisValueFormatter(salesData.keys.toList())
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.description.isEnabled = false
        chart.invalidate()
    }

//    USELESS
//    private fun handleClickBtnMoreInformation() {
//        if (mcProduct?.localProductKey != null) {
//            goToLocalProduct()
//        } else {
//            // Looking for and going to local product
//            val formatedQuery = mcProduct?.name?.lowercase()
//
//            mcProductsRef.orderByChild("name").startAt(formatedQuery).endAt(formatedQuery + "\uf8ff")
//                .addValueEventListener(object: ValueEventListener {
//                    override fun onDataChange(snapshot: DataSnapshot) {
//                        if (snapshot.childrenCount.toInt() == 0) {
//                            Toast.makeText(
//                                requireContext(),
//                                "No se pudo encontrar un producto local que coincida. Por favor, asegurese que el producto local tiene el mismo nombre e intentelo de nuevo.",
//                                Toast.LENGTH_LONG
//                            ).show()
//                            return
//                        }
//
//                        val localProductKey = snapshot.children.first().key
//
//                        Log.d("Busqueda", "Se encontró la coincidencia buscada.")
//                        mcProduct?.key?.let {
//                            mcProductsRef.child(it)
//                                .updateChildren(mapOf("localProductKey" to localProductKey))
//                                .addOnSuccessListener {
//                                    Log.d(
//                                        "Firebase",
//                                        "Información actualizada correctamente."
//                                    )
//
//                                    Toast.makeText(
//                                        requireContext(),
//                                        "Producto local referenciado correctamente.",
//                                        Toast.LENGTH_SHORT
//                                    ).show()
//
//                                    goToLocalProduct()
//
//                                    // TODO: probar que el anterior código funciona
//                                }
//                                .addOnFailureListener {
//                                    Log.e(
//                                        "Firebase",
//                                        "Ocurrio un problema al actualizar la información."
//                                    )
//                                }
//                        }
//
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {
//                        Log.w("Firebase", "Error al consultar los datos.", error.toException())
//                    }
//                })
//        }
//    }

//    private fun goToLocalProduct() {
//        val productDetailsIntent = Intent(context, ProductActivity::class.java)
//
//        // Looking for and going to local product
//        val formatedQuery = mcProduct?.localProductKey?.lowercase()
//
//        mcProductsRef.orderByChild("key").startAt(formatedQuery).endAt(formatedQuery + "\uf8ff")
//            .addValueEventListener(object: ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    val filteredArray = ArrayList<Product>()
//
//                    snapshot.children.forEach{ itemRef ->
//                        val product = itemRef.getValue(Product::class.java)
//                        product?.let { filteredArray.add(it) }
//                    }
//
//                    Log.d("Busqueda", "Se encontró la coincidencia buscada.")
//
//                    productDetailsIntent.putExtra("selectedProduct", filteredArray[0])
//
//                    startActivity(productDetailsIntent)
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    Log.w("Firebase", "Error al consultar los datos.", error.toException())
//                }
//            })
//    }
}