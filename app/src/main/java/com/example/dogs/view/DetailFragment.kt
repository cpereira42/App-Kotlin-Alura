package com.example.dogs.view

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.dogs.R
import com.example.dogs.databinding.FragmentDetailBinding
import com.example.dogs.model.DogBreed
import com.example.dogs.model.DogPalette
import com.example.dogs.util.getProgressDrawable
import com.example.dogs.util.loadImage
import com.example.dogs.viewmodel.DetailViewModel
import com.example.dogs.viewmodel.ListViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.android.synthetic.main.fragment_list.*

class DetailFragment : Fragment() {

    private lateinit var viewModel: DetailViewModel
    private var dogUuid = 0

    private lateinit var dataBinding : FragmentDetailBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        // alterado aula 51
        dataBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_detail, container, false)
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_detail, container, false)
        // alterado aula 51
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            dogUuid = DetailFragmentArgs.fromBundle(it).dogUuid
        }
        viewModel = ViewModelProviders.of(this).get(DetailViewModel::class.java)
        viewModel.fetch(dogUuid)



        observeViewModel()
    }
    fun observeViewModel(){
        viewModel.dogLiveData.observe(viewLifecycleOwner, { dog ->
            dog.let{
                dataBinding.dog = dog
                it.imageUrl?.let {
                    setupBackgroundColor(it)  // aula 53 - Pallete
                }

                /*dogName.text = dog.dogBreed
                dogPurpose.text = dog.bredFor
                dogTemperament.text = dog.temperament
                dogLifespan.text = dog.lifeSpan
                context?.let { dogImage.loadImage(dog.imageUrl, getProgressDrawable(it))}*/
            }
        })
    }

    // aula 53 - Pallete
    private fun setupBackgroundColor(url: String){
        Glide.with(this)
            .asBitmap()
            .load(url)
            .into(object :CustomTarget<Bitmap>(){
                override fun onLoadCleared(placeholder: Drawable?) {
                }
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    Palette.from(resource)
                        .generate { palette ->
                            val intColor = palette?.lightMutedSwatch?.rgb ?: 0
                            val myPallete = DogPalette(intColor)
                            dataBinding.palette = myPallete

                        }
                }



            })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.detail_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_send_sms -> {

            }
            R.id.action_share -> {

            }
        }
        return super.onOptionsItemSelected(item)
    }
}