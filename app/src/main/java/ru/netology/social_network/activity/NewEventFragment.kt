package ru.netology.social_network.activity

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.netology.social_network.R
import ru.netology.social_network.databinding.FragmentNewEventBinding
import ru.netology.social_network.dto.Coordinates
import ru.netology.social_network.dto.Event
import ru.netology.social_network.enumeration.AttachmentType
import ru.netology.social_network.untils.*
import ru.netology.social_network.viewmodel.EventViewModel
import java.util.*

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class NewEventFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    var type: AttachmentType? = null

    private val eventViewModel by activityViewModels<EventViewModel>()

    private var fragmentNewEventBinding: FragmentNewEventBinding? = null

    private var latitude: Double? = null
    private var longitude: Double? = null

    @SuppressLint("SetTextI18n")
    )
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentNewEventBinding.inflate(
            inflater,
            container,
            false
        )

        (activity as AppCompatActivity).supportActionBar?.title =
            context?.getString(R.string.title_event)

        fragmentNewEventBinding = binding

        latitude = arguments?.getDouble("lat")
        longitude = arguments?.getDouble("long")

        arguments?.textArg
            ?.let(binding.editTextFragmentNewEvent::setText)

        binding.editTextFragmentNewEvent.requestFocus()

        val datetime = arguments?.getString("datetime")?.let {
            formatToDate(it)
        } ?: formatToDate("${eventViewModel.edited.value?.datetime}")
        val date = datetime.substring(0, 10)
        val time = datetime.substring(11)

        binding.editTextFragmentNewEvent.setText(
            arguments?.getString("content") ?: eventViewModel.edited.value?.content
        )
        if (eventViewModel.edited.value != Event.emptyEvent) {
            binding.editTextDateFragmentNewEvent.setText(date)
            binding.editTextTimeFragmentNewEvent.setText(time)
        }

        binding.imageViewPickGeoFragmentNewEvent.setOnClickListener {
            eventViewModel.changeContent(
                binding.editTextFragmentNewEvent.text.toString(),
                formatToInstant(
                    "${binding.editTextDateFragmentNewEvent.text}" +
                            " " +
                            "${binding.editTextTimeFragmentNewEvent.text}"
                ),
                null
            )
            val bundle = Bundle().apply {
                putString("open", "newEvent")
                if (latitude != null) {
                    putDouble("lat", latitude!!)
                }
                if (longitude != null) {
                    putDouble("long", longitude!!)
                }
            }
            findNavController().navigate(R.id.nav_map_fragment, bundle)
        }

        binding.editTextDateFragmentNewEvent.setOnClickListener {
            context?.let { item ->
                pickDate(binding.editTextDateFragmentNewEvent, item)
            }
        }

        binding.editTextTimeFragmentNewEvent.setOnClickListener {
            context?.let { item ->
                pickTime(binding.editTextTimeFragmentNewEvent, item)
            }
        }

        binding.buttonAddSpeakersFragmentNewEvent.setOnClickListener {
            val bundle = Bundle().apply {
                putString("open", "speaker")
            }
            findNavController().navigate(R.id.nav_users, bundle)
        }

        eventViewModel.edited.observe(viewLifecycleOwner) {
            binding.buttonAddSpeakersFragmentNewEvent.apply {
                text = "$text ${eventViewModel.edited.value?.speakerIds?.count().toString()}"
            }
        }

        val photoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                it.data?.data.let { uri ->
                    val stream = uri?.let {
                        context?.contentResolver?.openInputStream(it)
                    }
                    eventViewModel.changeMedia(uri, stream, type)
                }
            }

        binding.imageViewPickPhotoFragmentNewEvent.setOnClickListener {

            ImagePicker.Builder(this)
                .galleryOnly()
                .galleryMimeTypes(
                    arrayOf(
                        "image/png",
                        "image/jpeg",
                        "image/jpg"
                    )
                )
                .maxResultSize(2048, 2048)
                .createIntent(photoLauncher::launch)
            type = AttachmentType.IMAGE
        }

        binding.imageViewTakePhotoFragmentNewEvent.setOnClickListener {
            ImagePicker.Builder(this)
                .cameraOnly()
                .maxResultSize(2048, 2048)
                .createIntent(photoLauncher::launch)
        }

        binding.buttonRemovePhotoFragmentNewEvent.setOnClickListener {
            eventViewModel.changeMedia(null, null, null)
        }

        eventViewModel.media.observe(viewLifecycleOwner) {
            if (it?.uri == null) {
                binding.frameLayoutPhotoFragmentNewEvent.visibility = View.GONE
                return@observe
            }
            binding.frameLayoutPhotoFragmentNewEvent.visibility = View.VISIBLE
            binding.imageViewPhotoFragmentNewEvent.setImageURI(it.uri)
        }

        eventViewModel.eventCreated.observe(viewLifecycleOwner) {
            findNavController().navigate(R.id.nav_events)
        }

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.create_post_menu, menu)
            }

            )
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                when (menuItem.itemId) {
                    R.id.save -> {
                        fragmentNewEventBinding?.let {
                            if (it.editTextFragmentNewEvent.text.isNullOrBlank()) {
                                Toast.makeText(
                                    activity,
                                    R.string.error_empty_content,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            } else {
                                eventViewModel.changeContent(
                                    it.editTextFragmentNewEvent.text.toString(),
                                    formatToInstant(
                                        "${it.editTextDateFragmentNewEvent.text} " +
                                                "${it.editTextTimeFragmentNewEvent.text}"
                                    ),
                                    Coordinates(latitude, longitude)
                                )
                                eventViewModel.save()
                                AndroidUtils.hideKeyboard(requireView())
                            }
                        }
                        true
                    }
                    else -> false
                }
        }, viewLifecycleOwner)

        return binding.root
    }

    override fun onDestroyView() {
        fragmentNewEventBinding = null
        super.onDestroyView()
    }
}