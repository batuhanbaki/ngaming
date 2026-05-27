package com.example.ngamingcase.presentation.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.ngamingcase.R
import com.example.ngamingcase.core.logging.AppLogger
import com.example.ngamingcase.databinding.FragmentPostEditBottomSheetBinding
import com.example.ngamingcase.presentation.posts.viewmodel.PostsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.content.DialogInterface
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostEditBottomSheetFragment : BottomSheetDialogFragment() {
    @Inject lateinit var logger: AppLogger
    private var _binding: FragmentPostEditBottomSheetBinding? = null
    private val binding get() = _binding!!
    private val vm: PostsViewModel by viewModels(ownerProducer = { requireParentFragment() })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPostEditBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val id = requireArguments().getInt(ARG_POST_ID)
        val title = requireArguments().getString(ARG_TITLE).orEmpty()
        val body = requireArguments().getString(ARG_BODY).orEmpty()
        logger.i("PostEditBottomSheet", "post edit opened. PostId: $id")

        binding.titleEdit.setText(title)
        binding.bodyEdit.setText(body)
        binding.titleEdit.requestFocus()

        binding.cancelButton.setOnClickListener { dismiss() }
        binding.saveButton.setOnClickListener {
            vm.updatePost(
                postId = id,
                title = binding.titleEdit.text?.toString().orEmpty(),
                body = binding.bodyEdit.text?.toString().orEmpty()
            )
        }

        binding.titleEdit.doAfterTextChanged { binding.titleLayout.error = null }
        binding.bodyEdit.doAfterTextChanged { binding.bodyLayout.error = null }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.postEditUiState.collect { state ->
                    if (state?.postId != id) return@collect
                    binding.titleLayout.error = state.titleError
                    binding.bodyLayout.error = state.bodyError
                    binding.saveButton.isEnabled = !state.isSaving
                    if (state.errorMessage != null) {
                        Snackbar.make(binding.root, state.errorMessage, Snackbar.LENGTH_SHORT).show()
                    }
                    if (state.isSaved) {
                        Snackbar.make(requireParentFragment().requireView(), R.string.post_updated, Snackbar.LENGTH_SHORT).show()
                        dismiss()
                    }
                }
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        vm.onEditDismissed()
    }

    override fun onDestroyView() {
        logger.d("PostEditBottomSheet", "bottom sheet dismissed")
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_POST_ID = "post_id"
        private const val ARG_TITLE = "title"
        private const val ARG_BODY = "body"

        fun newInstance(id: Int, title: String, body: String) = PostEditBottomSheetFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_POST_ID, id)
                putString(ARG_TITLE, title)
                putString(ARG_BODY, body)
            }
        }
    }
}
