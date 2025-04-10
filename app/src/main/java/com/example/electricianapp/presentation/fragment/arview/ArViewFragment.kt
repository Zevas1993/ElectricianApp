package com.example.electricianapp.presentation.fragment.arview

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.electricianapp.databinding.FragmentArViewBinding // Use ViewBinding
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Session
import com.google.ar.core.exceptions.CameraNotAvailableException
import com.google.ar.core.exceptions.UnavailableApkTooOldException
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException
import com.google.ar.core.exceptions.UnavailableSdkTooOldException
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException
import com.google.ar.sceneform.ux.ArFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint // Add Hilt if dependencies are needed later
class ArViewFragment : Fragment() {

    private var _binding: FragmentArViewBinding? = null
    private val binding get() = _binding!!

    private var arFragment: ArFragment? = null
    private var arSession: Session? = null
    private var userRequestedInstall = true // Assume user interaction needed initially

    // Activity Result Launcher for Camera Permission
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Log.i("ArViewFragment", "Camera permission granted.")
                // setupArSession() // Removed call to non-existent function - AR setup happens in onResume
            } else {
                Log.e("ArViewFragment", "Camera permission denied.")
                Toast.makeText(context, "Camera permission is required for AR features.", Toast.LENGTH_LONG).show()
                // Handle permission denial (e.g., disable AR features, show explanation)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArViewBinding.inflate(inflater, container, false)
        // Find the ArFragment defined in the layout
        arFragment = childFragmentManager.findFragmentById(binding.arFragmentContainer.id) as? ArFragment
        if (arFragment == null) {
             Log.e("ArViewFragment", "ArFragment not found in layout.")
             // Handle error - perhaps show a message
        }
        return binding.root
    }

     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // ArFragment setup will happen in onResume after checks
    }

    override fun onResume() {
        super.onResume()
        checkAndSetupAr()
    }

    override fun onPause() {
        super.onPause()
        // Pause the AR session
        arSession?.pause()
    }

     override fun onDestroyView() {
        super.onDestroyView()
         // ARCore session is managed by ArFragment, but clear binding
        _binding = null
        arSession = null // Clear session reference
    }

    private fun checkAndSetupAr() {
        if (!checkCameraPermission()) {
            requestCameraPermission()
            return // Wait for permission result
        }

        if (arSession == null) {
            var exception: Exception? = null
            var message: String? = null
            try {
                when (ArCoreApk.getInstance().requestInstall(requireActivity(), userRequestedInstall)) {
                    ArCoreApk.InstallStatus.INSTALLED -> {
                        // ARCore is installed or already was. Proceed with session creation.
                        arSession = Session(requireContext()) // Requires CAMERA permission
                        Log.i("ArViewFragment", "ARCore session created successfully.")
                        // Configure the session? (Optional)
                        // val config = Config(arSession)
                        // arSession?.configure(config)

                        // Set the session for the ArFragment
                        arFragment?.arSceneView?.setupSession(arSession)
                        setupArInteraction() // Setup listeners etc. now that session is ready
                    }
                    ArCoreApk.InstallStatus.INSTALL_REQUESTED -> {
                        // Installation requested. Ensures next call returns INSTALLED or throws.
                        message = "ARCore installation requested. Restarting check."
                        userRequestedInstall = false // Prevent multiple requests in loop
                        // Return to allow installation to complete. onResume will be called again.
                        return
                    }
                }
            } catch (e: UnavailableUserDeclinedInstallationException) {
                message = "Please install ARCore to use this feature."
                exception = e
            } catch (e: UnavailableApkTooOldException) {
                message = "Please update ARCore."
                exception = e
            } catch (e: UnavailableSdkTooOldException) {
                message = "Please update this app."
                exception = e
            } catch (e: UnavailableDeviceNotCompatibleException) {
                message = "This device does not support AR."
                exception = e
            } catch (e: CameraNotAvailableException) {
                message = "Camera not available. Please grant permission or ensure it's not in use."
                exception = e
            } catch (e: Exception) { // Catch other unexpected exceptions
                message = "Failed to create AR session."
                exception = e
            }

            if (message != null) {
                Log.e("ArViewFragment", "AR Setup Error: $message", exception)
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                // Handle inability to create session (e.g., disable AR button, show error view)
            }
        }

        // Resume the AR session if it exists
        try {
            arSession?.resume()
            arFragment?.arSceneView?.resume()
        } catch (e: CameraNotAvailableException) {
            Log.e("ArViewFragment", "Camera not available on resume", e)
            Toast.makeText(context, "Camera not available.", Toast.LENGTH_SHORT).show()
            arSession = null // Invalidate session
        }
    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    // Placeholder for setting up AR interactions (e.g., tap listeners)
    private fun setupArInteraction() {
         arFragment?.setOnTapArPlaneListener { hitResult, plane, motionEvent ->
             // Example: Place an object on tap
             Log.i("ArViewFragment", "AR Plane Tapped!")
             // val anchor = hitResult.createAnchor()
             // Place object logic would go here...
             Toast.makeText(context, "AR Plane Tapped", Toast.LENGTH_SHORT).show()
         }
    }
}
