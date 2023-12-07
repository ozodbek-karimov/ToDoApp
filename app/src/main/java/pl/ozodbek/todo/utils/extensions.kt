package pl.ozodbek.todo.utils

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pl.ozodbek.todo.R
import pl.ozodbek.todo.databinding.CustomDialogBinding

fun Fragment.changeFragmentTo(destination: Any) {
    val navController = this.findNavController()

    when (destination) {
        is Int -> {
            navController.safeNavigate(destination)
        }

        is NavDirections -> {
            navController.safeNavigate(destination)
        }
    }
}

fun Fragment.changeFragmentTo(destination: Any, navController: NavController?) {

    when (destination) {
        is Int -> {
            navController?.safeNavigate(destination)
        }

        is NavDirections -> {
            navController?.safeNavigate(destination)
        }
    }
}

fun NavController.safeNavigate(direction: Any) {
    when (direction) {
        is Int -> {
            if (currentDestination?.id == direction) {
                return
            }
            navigate(direction)
        }

        is NavDirections -> {
            var isNavigationEnabled = true
            CoroutineScope(Dispatchers.Main).launch {
                if (isNavigationEnabled) {
                    isNavigationEnabled = false
                    currentDestination?.getAction(direction.actionId)?.run {
                        navigate(direction)
                    }
                    delay(1000)
                    isNavigationEnabled = true
                }
            }
        }

        else -> {
        }
    }
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.hide() {
    this.visibility = View.INVISIBLE
}

fun View.onClick(clickListener: (View) -> Unit) {
    setOnClickListener(clickListener)
}


fun Fragment.onBackPressed(onBackPressed: OnBackPressedCallback.() -> Unit) {
    requireActivity().onBackPressedDispatcher.addCallback(this) {
        onBackPressed()
    }
}

fun Fragment.popBackStack() {
    findNavController().popBackStack()
}


fun View.showSoftKeyboard() {
    if (this.requestFocus()) {
        val imm = this.context.getSystemService(InputMethodManager::class.java)
        imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }
}

fun Fragment.hideSoftKeyboard() {
    val inputMethodManager = requireActivity().getSystemService(
        Activity.INPUT_METHOD_SERVICE
    ) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(requireView().windowToken, 0)
}

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(value: T) {
            observer.onChanged(value)
            removeObserver(this)
        }

    })
}

val EditText.fullText: String
    get() = this.text.toString().trim()

fun <T : ViewBinding> ViewGroup.viewBinding(viewBindingFactory: (LayoutInflater, ViewGroup, Boolean) -> T) =
    viewBindingFactory.invoke(LayoutInflater.from(this.context), this, false)


fun <T : ViewBinding> Fragment.inflateViewBinding(
    bindingInflater: (LayoutInflater) -> T,
): T {
    return bindingInflater.invoke(LayoutInflater.from(requireContext()))
}

fun Fragment.buildDialog(
    titleRes: String,
    messageRes: String,
    positiveButtonAction: () -> Unit
) {
    val binding = CustomDialogBinding.inflate(LayoutInflater.from(requireActivity()))
    val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        .setView(binding.root)

    val dialog = builder.create().apply {
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.deleteTitle.text = titleRes
        binding.deleteItem.text = messageRes
        setCancelable(false)
    }

    binding.apply {
        yesButton.onClick {
            positiveButtonAction()
            dialog.dismiss()
        }

        noButton.onClick {
            dialog.dismiss()
        }
        dialog.show()
    }
}

fun ViewModel.viewModelScopeOnIOThread(
    block: suspend CoroutineScope.() -> Unit,
) = viewModelScope.launch(Dispatchers.IO) {
    block()
}

fun ViewModel.viewModelScopeOnDefaultThread(
    block: suspend CoroutineScope.() -> Unit,
) = viewModelScope.launch {
    block()
}


