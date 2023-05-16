package com.murgierasmus.myapplication.ui.Maps

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.murgierasmus.myapplication.R
import com.murgierasmus.myapplication.core.RetrofitHelper
import com.murgierasmus.myapplication.data.network.CiudadesApiService
import com.murgierasmus.myapplication.databse.Database
import com.murgierasmus.myapplication.dataclass.CitiesResponse
import com.murgierasmus.myapplication.dataclass.DatosCiudad2Item
import com.murgierasmus.myapplication.dataclass.ImagenesSubir
import com.murgierasmus.myapplication.dataclass.PosicionCiudades
import com.murgierasmus.myapplication.ui.Maps.serviceMaps.ApiService
import com.murgierasmus.myapplication.ui.activities.MainActivity
import com.murgierasmus.myapplication.ui.activities.MainActivity.Companion.context
import com.murgierasmus.myapplication.utils.dialogs.Dialogs
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class AniadirUbicacion : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMyLocationButtonClickListener {
    private lateinit var map: GoogleMap
    private lateinit var btnCalculate: Button
    private lateinit var btnGuardar: Button
    private lateinit var ubicacion: LatLng
    private var allCiudades = mutableListOf<PosicionCiudades>()
    private var start: String = ""
    private var end: String = ""

    private var dialogs: Dialogs = Dialogs(this)
    private var database: Database = Database()
    private lateinit var datosCiudad2Item: DatosCiudad2Item
    private lateinit var imagenesCiudad: ImagenesSubir

    var poly: Polyline? = null
    var editar: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_aniadir)
        numeroPosicion()
        btnCalculate = findViewById(R.id.btnCalculateRoute)
        btnGuardar = findViewById(R.id.btnAceptar)

        //Este boton guarda la ubicacion para el marcador
        //y para calcular la ruta en la actividad final
        btnGuardar.setOnClickListener {
            if (end != "") {
                lifecycleScope.launch {
                    val deferredList = listOf(
                        async { database.subirArchivo(datosCiudad2Item.Foto, imagenesCiudad.Foto) },
                        async {
                            database.subirArchivo(
                                datosCiudad2Item.ImagenActividad,
                                imagenesCiudad.ImagenActividad
                            )
                        },
                        async {
                            database.subirArchivo(
                                datosCiudad2Item.ImagenComida,
                                imagenesCiudad.ImagenComida
                            )
                        },
                        async {
                            database.subirArchivo(
                                datosCiudad2Item.ImagenRestaurante,
                                imagenesCiudad.ImagenRestaurante
                            )
                        },
                        async {
                            database.subirArchivo(
                                datosCiudad2Item.ImagenCultura,
                                imagenesCiudad.ImagenCultura
                            )
                        }
                    )
                    deferredList.awaitAll()
                }


                val retrofit = RetrofitHelper.getRetrofit()
                val myApi = retrofit.create(CiudadesApiService::class.java)

                val bundle = intent
                val id = bundle?.getIntExtra("POSICION", 0)
                Log.i("POSITION", id.toString())
                var position =  if (id !=null){
                    id
                }else{
                    allCiudades.size
                }
                Log.i("POSITION", position.toString())
                myApi.agregarCiudad("Ciudades/Ciudad/" + position + ".json", datosCiudad2Item)
                    .enqueue(object : Callback<DatosCiudad2Item> {
                        override fun onResponse(
                            call: Call<DatosCiudad2Item>,
                            response: Response<DatosCiudad2Item>
                        ) {
                            Toast.makeText(
                                applicationContext,
                                context.getString(R.string.aniadirubi_ciudad),
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(applicationContext, MainActivity::class.java)
                            startActivity(intent)

                            Log.i("CAMPOS", "AGREGADA FIREBASE")
                        }

                        override fun onFailure(call: Call<DatosCiudad2Item>, t: Throwable) {
                            // Manejo de errores en la conexión o petición
                        }
                    })


            } else {
                Toast.makeText(
                    this,
                    context.getString(R.string.aniadirubi_seleccionar),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        //Este boton calcula la ruta a modo de prueba si se quiere
        //(seguramente lo quitemos)
        btnCalculate.setOnClickListener {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(this, context.getString(R.string.aniadirubi_gps), Toast.LENGTH_SHORT)
                    .show()
            }
            poly?.remove()
            start = ""
            //end=""
            poly = null
            if (::map.isInitialized) {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return@setOnClickListener
                }
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        // Se ha obtenido la ubicación actual
                        if (location != null) {
                            val latitude = location?.latitude
                            val longitude = location?.longitude
                            start = "${longitude},${latitude}"
                            createRoute()
                        }
                    }
            }
        }
        createFragment()
    }

    fun numeroPosicion() {
        val retrofit = RetrofitHelper.getRetrofit()
        val myApi = retrofit.create(CiudadesApiService::class.java)

        myApi.getMovies("Ciudades.json/").enqueue(object : Callback<CitiesResponse> {
            override fun onResponse(
                call: Call<CitiesResponse>,
                response: Response<CitiesResponse>
            ) {
                val cities = response.body()?.cities

                if (cities != null) {
                    Log.i("CAMPOS", allCiudades.size.toString() + " inical")
                    allCiudades.addAll(cities)
                    Log.i("CAMPOS", allCiudades.size.toString() + " final")
                } else {
                    Log.i("CAMPOS", " null")
                }


            }

            override fun onFailure(call: Call<CitiesResponse>, t: Throwable) {
                Log.e("MainActivity", "Error al cargar los datos de la API", t)
            }
        })


    }






    //Este metodo permite crear un marcador
    //al pinchar en el mapa, le pone de titulo
    //ubicación por defecto y hace la animación
    //para situar el mapa en el marcador colocado
    private fun metodo() {
        val bundle = intent
        datosCiudad2Item = bundle.getParcelableExtra<DatosCiudad2Item>("DATA")!!
        imagenesCiudad = bundle.getParcelableExtra<ImagenesSubir>("DATA2")!!
        val destino = bundle?.getStringExtra("UBI")
        end = destino.toString()
        map.setOnMapClickListener {
            map.clear()
            end = "${it.longitude},${it.latitude}"
            val coordinates = LatLng(it.latitude, it.longitude)
            ubicacion = LatLng(it.latitude, it.longitude)
            datosCiudad2Item.UbicacionString = end
            datosCiudad2Item.UbicacionLng1 = it.latitude
            datosCiudad2Item.UbicacionLng2 = it.longitude
            val marker: MarkerOptions = MarkerOptions().position(coordinates)
                .title(context.getString(R.string.aniadirubi_ubi))
            map.addMarker(marker)
            //map.moveCamera(CameraUpdateFactory.newLatLng(coordinates))
            println("Longitud:" + coordinates.longitude)
            println("Latitud:" + coordinates.latitude)
            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(coordinates, 15f),
                1000,
                null
            )
        }
    }

    //Metodo para obtener la api de las rutas
    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.openrouteservice.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    companion object {
        const val REQUEST_CODE_LOCATION = 0
    }

    //Metodo para crear la ruta
    private fun createRoute() {
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(ApiService::class.java)
                .getRoute("5b3ce3597851110001cf624808c292df8dea4e369babd5796e166540", start, end)
            if (call.isSuccessful) {
                Log.i("aris", "OK")
                drawRoute(call.body())
            } else {
                Log.i("aris", "KO")
            }
        }
    }

    //Metodo para dibujar las lineas de la ruta
    private fun drawRoute(routeResponse: RouteResponse?) {
        val polyLineOptions = PolylineOptions()
        routeResponse?.features?.first()?.geometry?.coordinates?.forEach {
            polyLineOptions.add(LatLng(it[1], it[0]))
        }
        runOnUiThread {
            poly = map.addPolyline(polyLineOptions)
            val color = ContextCompat.getColor(MainActivity.context, R.color.azul)
            poly!!.setColor(color)
            poly!!.setWidth(15f)
        }

    }

    //Metodo para cargar el fragment del mapa
    private fun createFragment() {
        val mapFragment: SupportMapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        //Permite tener el boton que al pulsarlo
        //te manda a la ubicación en tiempo real del gps
        map.setOnMyLocationButtonClickListener(this)
        enableLocation()
        metodo()

        //Este codigo es para cuando al estar el mapa
        //listo hace una animación para posicionarlo
        //justo donde esté la ubicación el tiempo real
        //del gps
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // Se ha obtenido la ubicación actual
                if (location != null) {
                    val latitude = location?.latitude
                    val longitude = location?.longitude
                    val coordinates = LatLng(latitude!!, longitude!!)
                    map.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(coordinates, 15f),
                        4000,
                        null
                    )

                }
            }
    }

    //Metodo para comprobar si está el permiso
    //de localización garatinzado
    private fun isLocationPermissionGranted() = ContextCompat.checkSelfPermission(
        this,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    //Metodo habilita la localización
    //cuando el mapa está inicializado
    private fun enableLocation() {
        if (!::map.isInitialized) return
        if (isLocationPermissionGranted()) {
            //si
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            map.isMyLocationEnabled = true
        } else {
            requestLocationPermission()
            //no
        }
    }

    //Metodo para pedir los permisos de ubicación
    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            Toast.makeText(
                this,
                context.getString(R.string.aniadirubi_permisos),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION
            )
        }
    }

    //Según la respuesta de la peticiónd de permisos
    //lanza el toast o habilita la localización
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_LOCATION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                map.isMyLocationEnabled = true
            } else {
                Toast.makeText(
                    this,
                    context.getString(R.string.aniadirubi_permisosLoca),
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {}
            //hola
            //añadido desde github
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        if (!::map.isInitialized) return
        if (!isLocationPermissionGranted()) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            map.isMyLocationEnabled = false
            Toast.makeText(
                this,
                context.getString(R.string.aniadirubi_permisosLoca),
                Toast.LENGTH_SHORT
            ).show()

        }
    }

    //Mensaje cuando pulsa el botoon de posicionar
    //en la localización
    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(
            this,
            context.getString(R.string.aniadirubi_ubiTiempoReal),
            Toast.LENGTH_SHORT
        ).show()
        return false
    }
}
