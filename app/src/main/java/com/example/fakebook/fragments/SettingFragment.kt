package com.example.fakebook.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.fakebook.R
import com.example.fakebook.activities.LoginActivity
import com.example.fakebook.models.Users
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.util.*

class SettingFragment : Fragment() {

    private var user: Users? = null
    private var refUsers: DatabaseReference? = null
    private var firebaseUser: FirebaseUser? = null

    private var settingFragmentCvGeneralAccountSettings: CardView? = null
    private var settingFragmentLlGeneralAccountSettings: LinearLayout? = null
    private var settingFragmentCvUsername: CardView? = null
    private var settingFragmentTvUsername: TextView? = null
    private var settingFragmentCvEmail: CardView? = null
    private var settingFragmentTvEmail: TextView? = null
    private var settingFragmentCvPhone: CardView? = null
    private var settingFragmentTvPhone: TextView? = null

    private var settingFragmentCvSocialNetwork: CardView? = null
    private var settingFragmentLlSocialNetwork: LinearLayout? = null
    private var settingFragmentCvFacebook: CardView? = null
    private var settingFragmentTvFacebook: TextView? = null
    private var settingFragmentCvInstagram: CardView? = null
    private var settingFragmentTvInstagram: TextView? = null
    private var settingFragmentCvTiktok: CardView? = null
    private var settingFragmentTvTiktok: TextView? = null

    private var settingFragmentCvSecurityAndLogin: CardView? = null
    private var settingFragmentLlSecurityAndLogin: LinearLayout? = null
    private var settingFragmentCvChangePassword: CardView? = null
    private var settingFragmentCvLogOut: CardView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_setting, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

        settingFragmentCvGeneralAccountSettings = view.findViewById(R.id.settingFragmentCvGeneralAccountSettings)
        settingFragmentLlGeneralAccountSettings = view.findViewById(R.id.settingFragmentLlGeneralAccountSettings)
        settingFragmentCvUsername = view.findViewById(R.id.settingFragmentCvUsername)
        settingFragmentTvUsername = view.findViewById(R.id.settingFragmentTvUsername)
        settingFragmentCvEmail = view.findViewById(R.id.settingFragmentCvEmail)
        settingFragmentTvEmail = view.findViewById(R.id.settingFragmentTvEmail)
        settingFragmentCvPhone = view.findViewById(R.id.settingFragmentCvPhone)
        settingFragmentTvPhone = view.findViewById(R.id.settingFragmentTvPhone)

        settingFragmentCvSocialNetwork = view.findViewById(R.id.settingFragmentCvSocialNetwork)
        settingFragmentLlSocialNetwork = view.findViewById(R.id.settingFragmentLlSocialNetwork)
        settingFragmentCvFacebook = view.findViewById(R.id.settingFragmentCvFacebook)
        settingFragmentTvFacebook = view.findViewById(R.id.settingFragmentTvFacebook)
        settingFragmentCvInstagram = view.findViewById(R.id.settingFragmentCvInstagram)
        settingFragmentTvInstagram = view.findViewById(R.id.settingFragmentTvInstagram)
        settingFragmentCvTiktok = view.findViewById(R.id.settingFragmentCvTiktok)
        settingFragmentTvTiktok = view.findViewById(R.id.settingFragmentTvTiktok)

        settingFragmentCvSecurityAndLogin = view.findViewById(R.id.settingFragmentCvSecurityAndLogin)
        settingFragmentLlSecurityAndLogin = view.findViewById(R.id.settingFragmentLlSecurityAndLogin)
        settingFragmentCvChangePassword = view.findViewById(R.id.settingFragmentCvChangePassword)
        settingFragmentCvLogOut = view.findViewById(R.id.settingFragmentCvLogOut)

        setData()

        settingFragmentCvGeneralAccountSettings!!.setOnClickListener {
            settingFragmentCvGeneralAccountSettingsOnClick()
        }
        settingFragmentCvUsername!!.setOnClickListener {
            settingFragmentCvUsernameOnClick()
        }
        settingFragmentCvEmail!!.setOnClickListener {
            settingFragmentCvEmailOnClick()
        }
        settingFragmentCvPhone!!.setOnClickListener {
            settingFragmentCvPhoneOnClick()
        }

        settingFragmentCvSocialNetwork!!.setOnClickListener {
            settingFragmentCvSocialNetworkOnClick()
        }
        settingFragmentCvFacebook!!.setOnClickListener {
            settingFragmentCvFacebookOnClick()
        }
        settingFragmentCvInstagram!!.setOnClickListener {
            settingFragmentCvInstagramOnClick()
        }
        settingFragmentCvTiktok!!.setOnClickListener {
            settingFragmentCvTiktokOnClick()
        }

        settingFragmentCvSecurityAndLogin!!.setOnClickListener {
            settingFragmentCvSecurityAndLoginOnClick()
        }
        settingFragmentCvChangePassword!!.setOnClickListener {
            settingFragmentCvChangePasswordOnClick()
        }
        settingFragmentCvLogOut!!.setOnClickListener {
            settingFragmentCvLogOutOnClick()
        }

        return view
    }

    private fun setData() {
        refUsers!!.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    user = p0.getValue(Users::class.java)
                    settingFragmentTvUsername?.text = "Name: ${user?.getUsername()}"
                    if (settingFragmentTvUsername?.text?.length!! > 25) {
                        settingFragmentTvUsername?.text = settingFragmentTvUsername?.text?.substring(0, 25) + "..."
                    }
                    settingFragmentTvEmail?.text = "Email: ${user?.getEmail()}"
                    if (settingFragmentTvEmail?.text?.length!! > 25) {
                        settingFragmentTvEmail?.text = settingFragmentTvEmail?.text?.substring(0, 25) + "..."
                    }
                    settingFragmentTvPhone?.text = "Phone: ${user?.getPhone()}"
                    if (settingFragmentTvPhone?.text?.length!! > 25) {
                        settingFragmentTvPhone?.text = settingFragmentTvPhone?.text?.substring(0, 25) + "..."
                    }
                    settingFragmentTvFacebook?.text = "Facebook: ${user?.getFacebook()}"
                    if (settingFragmentTvFacebook?.text?.length!! > 25) {
                        settingFragmentTvFacebook?.text = settingFragmentTvFacebook?.text?.substring(0, 25) + "..."
                    }
                    settingFragmentTvInstagram?.text = "Instagram: ${user?.getInstagram()}"
                    if (settingFragmentTvInstagram?.text?.length!! > 25) {
                        settingFragmentTvInstagram?.text = settingFragmentTvInstagram?.text?.substring(0, 25) + "..."
                    }
                    settingFragmentTvTiktok?.text = "Tiktok: ${user?.getTiktok()}"
                    if (settingFragmentTvTiktok?.text?.length!! > 25) {
                        settingFragmentTvTiktok?.text = settingFragmentTvTiktok?.text?.substring(0, 25) + "..."
                    }
                }
            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun settingFragmentCvGeneralAccountSettingsOnClick() {
        if (settingFragmentLlGeneralAccountSettings!!.visibility == View.GONE) {
            settingFragmentLlGeneralAccountSettings!!.visibility = View.VISIBLE
        } else {
            settingFragmentLlGeneralAccountSettings!!.visibility = View.GONE
        }
    }
    private fun settingFragmentCvUsernameOnClick() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_change_data)
        dialog.setCancelable(true)
        val dialogChangeDataTvData = dialog.findViewById<TextView>(R.id.dialogChangeDataTvData)
        val dialogChangeDataEtData = dialog.findViewById<EditText>(R.id.dialogChangeDataEtData)
        val dialogChangeDataTvEdit = dialog.findViewById<TextView>(R.id.dialogChangeDataTvEdit)
        dialogChangeDataTvData.text = "Username: "
        dialogChangeDataEtData.setText(user?.getUsername())
        dialogChangeDataTvEdit.text = "Edit username"
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val dialogChangeDataBtnSave = dialog.findViewById<Button>(R.id.dialogChangeDataBtnSave)
        dialogChangeDataBtnSave.setOnClickListener {
            if (dialogChangeDataEtData.text.toString().isEmpty()) {
                Toast.makeText(requireContext(), "Please enter your username", Toast.LENGTH_SHORT).show()
            } else {
                refUsers!!.child("username").setValue(dialogChangeDataEtData.text.toString())
                refUsers!!.child("search").setValue(dialogChangeDataEtData.text.toString().lowercase(Locale.getDefault()))
                Toast.makeText(requireContext(), "Username changed", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }
        dialog.show()
    }
    private fun settingFragmentCvEmailOnClick() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_change_data)
        dialog.setCancelable(true)
        val dialogChangeDataTvData = dialog.findViewById<TextView>(R.id.dialogChangeDataTvData)
        val dialogChangeDataEtData = dialog.findViewById<EditText>(R.id.dialogChangeDataEtData)
        val dialogChangeDataTvEdit = dialog.findViewById<TextView>(R.id.dialogChangeDataTvEdit)
        dialogChangeDataTvData.text = "Email: "
        dialogChangeDataEtData.setText(user?.getEmail())
        dialogChangeDataTvEdit.text = "Edit email"
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val dialogChangeDataBtnSave = dialog.findViewById<Button>(R.id.dialogChangeDataBtnSave)
        dialogChangeDataBtnSave.setOnClickListener {
            if (dialogChangeDataEtData.text.toString().isEmpty()) {
                Toast.makeText(requireContext(), "Please enter your email", Toast.LENGTH_SHORT).show()
            } else {
                refUsers!!.child("email").setValue(dialogChangeDataEtData.text.toString())
                Toast.makeText(requireContext(), "Email changed", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }
        dialog.show()
    }
    private fun settingFragmentCvPhoneOnClick() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_change_data)
        dialog.setCancelable(true)
        val dialogChangeDataTvData = dialog.findViewById<TextView>(R.id.dialogChangeDataTvData)
        val dialogChangeDataEtData = dialog.findViewById<EditText>(R.id.dialogChangeDataEtData)
        val dialogChangeDataTvEdit = dialog.findViewById<TextView>(R.id.dialogChangeDataTvEdit)
        dialogChangeDataTvData.text = "Phone: "
        dialogChangeDataEtData.setText(user?.getPhone())
        dialogChangeDataTvEdit.text = "Edit phone"
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val dialogChangeDataBtnSave = dialog.findViewById<Button>(R.id.dialogChangeDataBtnSave)
        dialogChangeDataBtnSave.setOnClickListener {
            if (dialogChangeDataEtData.text.toString().isEmpty()) {
                Toast.makeText(requireContext(), "Please enter your phone", Toast.LENGTH_SHORT).show()
            } else {
                refUsers!!.child("phone").setValue(dialogChangeDataEtData.text.toString())
                Toast.makeText(requireContext(), "Phone changed", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun settingFragmentCvSocialNetworkOnClick() {
        if (settingFragmentLlSocialNetwork!!.visibility == View.GONE) {
            settingFragmentLlSocialNetwork!!.visibility = View.VISIBLE
        } else {
            settingFragmentLlSocialNetwork!!.visibility = View.GONE
        }
    }
    private fun settingFragmentCvFacebookOnClick() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_change_data)
        dialog.setCancelable(true)
        val dialogChangeDataTvData = dialog.findViewById<TextView>(R.id.dialogChangeDataTvData)
        val dialogChangeDataEtData = dialog.findViewById<EditText>(R.id.dialogChangeDataEtData)
        val dialogChangeDataTvEdit = dialog.findViewById<TextView>(R.id.dialogChangeDataTvEdit)
        dialogChangeDataTvData.text = "Facebook: "
        dialogChangeDataEtData.setText(user?.getFacebook())
        dialogChangeDataTvEdit.text = "Edit facebook"
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val dialogChangeDataBtnSave = dialog.findViewById<Button>(R.id.dialogChangeDataBtnSave)
        dialogChangeDataBtnSave.setOnClickListener {
            if (dialogChangeDataEtData.text.toString().isEmpty()) {
                Toast.makeText(requireContext(), "Please enter your facebook", Toast.LENGTH_SHORT).show()
            } else {
                refUsers!!.child("facebook").setValue(dialogChangeDataEtData.text.toString())
                Toast.makeText(requireContext(), "facebook changed", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }
        dialog.show()
    }
    private fun settingFragmentCvInstagramOnClick() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_change_data)
        dialog.setCancelable(true)
        val dialogChangeDataTvData = dialog.findViewById<TextView>(R.id.dialogChangeDataTvData)
        val dialogChangeDataEtData = dialog.findViewById<EditText>(R.id.dialogChangeDataEtData)
        val dialogChangeDataTvEdit = dialog.findViewById<TextView>(R.id.dialogChangeDataTvEdit)
        dialogChangeDataTvData.text = "Instagram: "
        dialogChangeDataEtData.setText(user?.getInstagram())
        dialogChangeDataTvEdit.text = "Edit instagram"
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val dialogChangeDataBtnSave = dialog.findViewById<Button>(R.id.dialogChangeDataBtnSave)
        dialogChangeDataBtnSave.setOnClickListener {
            if (dialogChangeDataEtData.text.toString().isEmpty()) {
                Toast.makeText(requireContext(), "Please enter your instagram", Toast.LENGTH_SHORT).show()
            } else {
                refUsers!!.child("instagram").setValue(dialogChangeDataEtData.text.toString())
                Toast.makeText(requireContext(), "Instagram changed", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }
        dialog.show()
    }
    private fun settingFragmentCvTiktokOnClick() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_change_data)
        dialog.setCancelable(true)
        val dialogChangeDataTvData = dialog.findViewById<TextView>(R.id.dialogChangeDataTvData)
        val dialogChangeDataEtData = dialog.findViewById<EditText>(R.id.dialogChangeDataEtData)
        val dialogChangeDataTvEdit = dialog.findViewById<TextView>(R.id.dialogChangeDataTvEdit)
        dialogChangeDataTvData.text = "Tiktok: "
        dialogChangeDataEtData.setText(user?.getTiktok())
        dialogChangeDataTvEdit.text = "Edit tiktok"
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val dialogChangeDataBtnSave = dialog.findViewById<Button>(R.id.dialogChangeDataBtnSave)
        dialogChangeDataBtnSave.setOnClickListener {
            if (dialogChangeDataEtData.text.toString().isEmpty()) {
                Toast.makeText(requireContext(), "Please enter your tiktok", Toast.LENGTH_SHORT).show()
            } else {
                refUsers!!.child("tiktok").setValue(dialogChangeDataEtData.text.toString())
                Toast.makeText(requireContext(), "Tiktok changed", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun settingFragmentCvSecurityAndLoginOnClick() {
        if (settingFragmentLlSecurityAndLogin!!.visibility == View.GONE) {
            settingFragmentLlSecurityAndLogin!!.visibility = View.VISIBLE
        } else {
            settingFragmentLlSecurityAndLogin!!.visibility = View.GONE
        }
    }
    private fun settingFragmentCvChangePasswordOnClick() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_change_data)
        dialog.setCancelable(true)
        val dialogChangeDataTvData = dialog.findViewById<TextView>(R.id.dialogChangeDataTvData)
        val dialogChangeDataEtData = dialog.findViewById<EditText>(R.id.dialogChangeDataEtData)
        val dialogChangeDataTvEdit = dialog.findViewById<TextView>(R.id.dialogChangeDataTvEdit)
        dialogChangeDataTvData.text = "Password: "
        dialogChangeDataEtData.setText(user?.getPassword())
        dialogChangeDataTvEdit.text = "Edit password"
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val dialogChangeDataBtnSave = dialog.findViewById<Button>(R.id.dialogChangeDataBtnSave)
        dialogChangeDataBtnSave.setOnClickListener {
            if (dialogChangeDataEtData.text.toString().isEmpty()) {
                Toast.makeText(requireContext(), "Please enter your password", Toast.LENGTH_SHORT).show()
            } else {
                refUsers!!.child("password").setValue(dialogChangeDataEtData.text.toString())
                val credential = EmailAuthProvider.getCredential(user?.getEmail().toString(), user?.getPassword().toString())
                firebaseUser?.reauthenticate(credential)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            firebaseUser!!.updatePassword(dialogChangeDataEtData.text.toString())
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(requireContext(), "Password changed", Toast.LENGTH_SHORT).show()
                                        dialog.dismiss()
                                    } else {
                                        Toast.makeText(requireContext(), "Password not changed", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        } else {
                            Toast.makeText(requireContext(), "Password not changed", Toast.LENGTH_SHORT).show()
                        }
                    }
                dialog.dismiss()
            }
        }
        dialog.show()
    }
    private fun settingFragmentCvLogOutOnClick() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to logout?")
        builder.setPositiveButton("Yes") { _, _ ->
            FirebaseAuth.getInstance().signOut()
            refUsers!!.child("status").setValue("offline")
            if (FirebaseAuth.getInstance().currentUser == null) {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                requireActivity().finish()
            }
        }.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }.show()
    }
}