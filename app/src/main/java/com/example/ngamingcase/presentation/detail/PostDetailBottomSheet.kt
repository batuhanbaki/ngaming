package com.example.ngamingcase.presentation.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.ngamingcase.R
import com.example.ngamingcase.core.logging.AppLogger
import com.example.ngamingcase.databinding.BottomSheetPostDetailBinding
import com.example.ngamingcase.presentation.posts.viewmodel.PostsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PostDetailBottomSheet : BottomSheetDialogFragment() {
    @Inject lateinit var logger: AppLogger
    private var _binding: BottomSheetPostDetailBinding? = null
    private val binding get() = _binding!!
    private val vm: PostsViewModel by viewModels(ownerProducer = { requireParentFragment() })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = BottomSheetPostDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val id = requireArguments().getInt("id")
        logger.i("DetailBottomSheet", "detail opened for post id: $id")
        binding.titleEdit.setText(requireArguments().getString("title").orEmpty())
        binding.bodyEdit.setText(requireArguments().getString("body").orEmpty())
        binding.saveButton.setOnClickListener {
            logger.i("DetailBottomSheet", "save clicked. PostId: $id")
            val title = binding.titleEdit.text.toString().trim()
            val body = binding.bodyEdit.text.toString().trim()
            if (title.isEmpty() || body.isEmpty()) {
                logger.w("DetailBottomSheet", "validation failed: title/body empty")
                binding.titleEdit.error = getString(R.string.error_validation)
                return@setOnClickListener
            }
            vm.updatePost(id, title, body)
            logger.i("DetailBottomSheet", "update completed. PostId: $id")
            dismiss()
        }
        binding.cancelButton.setOnClickListener { dismiss() }
    }

    override fun onDestroyView() {
        logger.d("DetailBottomSheet", "dialog dismissed")
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(id: Int, title: String, body: String) = PostDetailBottomSheet().apply {
            arguments = Bundle().apply { putInt("id", id); putString("title", title); putString("body", body) }
        }
    }
}
