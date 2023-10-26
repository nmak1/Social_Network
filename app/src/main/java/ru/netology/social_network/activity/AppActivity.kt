package ru.netology.social_network.activity

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailabilityLight
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.netology.social_network.R
import ru.netology.social_network.auth.AppAuth
import ru.netology.social_network.databinding.ActivityAppBinding
import ru.netology.social_network.viewmodel.AuthViewModel
import ru.netology.social_network.viewmodel.UserViewModel
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class AppActivity : AppCompatActivity() {

    @Inject
    lateinit var appAuth: AppAuth

    @Inject
    lateinit var firebaseMessaging: FirebaseMessaging

    @Inject
    lateinit var googleApiAvailability: GoogleApiAvailabilityLight

    private val authViewModel by viewModels<AuthViewModel>()

    private val userViewModel by viewModels<UserViewModel>()

    private lateinit var binding: ActivityAppBinding

    @Suppress("UNUSED_EXPRESSION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        binding = ActivityAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        navView.itemIconTintList = null

        val navController = findNavController(R.id.nav_host_fragment_activity_app)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.nav_posts,
                R.id.nav_users,
                R.id.nav_events,
                R.id.nav_profile,
                -> {
                    navView.visibility = View.VISIBLE
                }
                else -> {
                    navView.visibility = View.GONE
                }
            }
        }

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_posts,
                R.id.nav_users,
                R.id.nav_events,
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        navView.setupWithNavController(navController)

        val itemIcon = navView.menu.findItem(R.id.nav_profile)

        authViewModel.data.observe(this) { auth ->
            invalidateOptionsMenu()
            if (auth.id == 0L) {
                itemIcon.setIcon(R.drawable.ic_default_user_profile_image)
            } else {
                userViewModel.getUserById(auth.id)
            }

            navView.menu.findItem(R.id.nav_profile).setOnMenuItemClickListener {
                if (!authViewModel.authorized) {
                    findNavController(R.id.nav_host_fragment_activity_app)
                        .navigate(R.id.nav_sign_in_fragment)
                    true
                } else {
                    userViewModel.getUserById(auth.id)
                    val bundle = Bundle().apply {
                        userViewModel.user.value?.id?.let { it ->
                            putLong("id", it)
                        }
                        putString("avatar", userViewModel.user.value?.avatar)
                        putString("name", userViewModel.user.value?.name)
                    }

                    findNavController(R.id.nav_host_fragment_activity_app).popBackStack()

                    findNavController(R.id.nav_host_fragment_activity_app)
                        .navigate(R.id.nav_profile, bundle)
                    true
                }
            }
        }

        userViewModel.user.observe(this) {
            Glide.with(this)
                .asBitmap()
                .load("${it.avatar}")
                .transform(CircleCrop())
                .into(object : CustomTarget<Bitmap>() {

                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?,
                    ) {
                        itemIcon.icon = BitmapDrawable(resources, resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                }
                )
        }
        checkGoogleApiAvailability()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        menu.let {
            it.setGroupVisible(R.id.unauthentificated, !authViewModel.authorized)
            it.setGroupVisible(R.id.authentificated, authViewModel.authorized)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController(R.id.nav_host_fragment_activity_app).navigateUp()
            }
            R.id.sign_in -> {
                findNavController(R.id.nav_host_fragment_activity_app)
                    .navigate(R.id.nav_sign_in_fragment)
                true
            }
            R.id.sign_up -> {
                findNavController(R.id.nav_host_fragment_activity_app)
                    .navigate(R.id.nav_sign_up_fragment)
                true
            }
            R.id.sign_out -> {
                appAuth.removeAuth()
                findNavController(R.id.nav_host_fragment_activity_app)
                    .navigate(R.id.nav_posts)
                true
            }
            else ->
                super.onOptionsItemSelected(item)
        }
    }

    private fun checkGoogleApiAvailability() {
        with(googleApiAvailability) {
            val code = isGooglePlayServicesAvailable(this@AppActivity)
            if (code == ConnectionResult.SUCCESS) {
                return@with
            }
            if (isUserResolvableError(code)) {
                getErrorString(code)
                return
            }
            Toast.makeText(this@AppActivity, R.string.google_play_unavailable, Toast.LENGTH_LONG)
                .show()
        }

        firebaseMessaging.token.addOnSuccessListener {
            println(it)
        }
    }
}