package com.example.ngamingcase.presentation.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.ngamingcase.databinding.BottomSheetPostDetailBinding
import com.example.ngamingcase.presentation.posts.viewmodel.PostsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostDetailBottomSheet : BottomSheetDialogFragment() {
    private var _binding: BottomSheetPostDetailBinding? = null
    private val binding get() = _binding!!
    private val vm: PostsViewModel by viewModels(ownerProducer = { requireParentFragment() })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = BottomSheetPostDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val id = requireArguments().getInt("id")
        binding.titleEdit.setText(requireArguments().getString("title").orEmpty())
        binding.bodyEdit.setText(requireArguments().getString("body").orEmpty())
        binding.saveButton.setOnClickListener {
            vm.updatePost(id, binding.titleEdit.text.toString(), binding.bodyEdit.text.toString())
            dismiss()
        }
        binding.cancelButton.setOnClickListener { dismiss() }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }

    companion object {
        fun newInstance(id: Int, title: String, body: String) = PostDetailBottomSheet().apply {
            arguments = Bundle().apply { putInt("id", id); putString("title", title); putString("body", body) }
        }
    }
}
