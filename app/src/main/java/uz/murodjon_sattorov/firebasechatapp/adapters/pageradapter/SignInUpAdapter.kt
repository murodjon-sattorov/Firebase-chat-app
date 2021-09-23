package uz.murodjon_sattorov.firebasechatapp.adapters.pageradapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import java.util.*

/**
 * Created by [Murodjon Sattorov](mailto: sattorovmurodjon43@gmail.com)
 *
 * @author Murodjon
 * @date 9/2/2021
 * @project Firebase Chat app
 */
class SignInUpAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    var fragmentArrayList = ArrayList<Fragment>()
    var stringArrayList = ArrayList<String>()
    override fun getItem(position: Int): Fragment {
        return fragmentArrayList[position]
    }

    override fun getCount(): Int {
        return stringArrayList.size
    }

    fun addPagerFragment(fragment: Fragment, s: String) {
        fragmentArrayList.add(fragment)
        stringArrayList.add(s)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return stringArrayList[position]
    }
}