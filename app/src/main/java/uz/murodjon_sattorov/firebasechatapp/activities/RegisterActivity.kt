package uz.murodjon_sattorov.firebasechatapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import uz.murodjon_sattorov.firebasechatapp.adapters.pageradapter.SignInUpAdapter
import uz.murodjon_sattorov.firebasechatapp.databinding.ActivityRegisterBinding
import uz.murodjon_sattorov.firebasechatapp.fragments.SignInFragment
import uz.murodjon_sattorov.firebasechatapp.fragments.SignUpFragment

class RegisterActivity : AppCompatActivity() {

    private lateinit var registerBinding: ActivityRegisterBinding

    private var adapter: SignInUpAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(registerBinding.root)

        adapter = SignInUpAdapter(supportFragmentManager)

        adapter!!.addPagerFragment(SignUpFragment(), "Sign Up")
        adapter!!.addPagerFragment(SignInFragment(), "Sign In")

        registerBinding.viewPager.adapter = adapter
        registerBinding.tabLayout.setupWithViewPager(registerBinding.viewPager)

    }

}