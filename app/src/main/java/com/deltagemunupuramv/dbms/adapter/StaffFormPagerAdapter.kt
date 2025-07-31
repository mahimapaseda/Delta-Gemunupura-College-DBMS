package com.deltagemunupuramv.dbms.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.deltagemunupuramv.dbms.fragment.BasicInfoFragment
import com.deltagemunupuramv.dbms.fragment.PersonalDetailsFragment
import com.deltagemunupuramv.dbms.fragment.EmploymentDetailsFragment
import com.deltagemunupuramv.dbms.fragment.StaffClassificationFragment

class StaffFormPagerAdapter(private val fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    
    private val fragmentList = mutableListOf<Fragment>()
    
    companion object {
        const val TAB_BASIC_INFO = 0
        const val TAB_PERSONAL = 1
        const val TAB_EMPLOYMENT = 2
        const val TAB_CLASSIFICATION = 3
        const val TAB_COUNT = 4
    }
    
    override fun getItemCount(): Int = TAB_COUNT
    
    override fun createFragment(position: Int): Fragment {
        val fragment = when (position) {
            TAB_BASIC_INFO -> BasicInfoFragment()
            TAB_PERSONAL -> PersonalDetailsFragment()
            TAB_EMPLOYMENT -> EmploymentDetailsFragment()
            TAB_CLASSIFICATION -> StaffClassificationFragment()
            else -> BasicInfoFragment()
        }
        
        // Ensure the fragment list is large enough
        while (fragmentList.size <= position) {
            fragmentList.add(Fragment())
        }
        fragmentList[position] = fragment
        
        return fragment
    }
    
    fun getFragment(position: Int): Fragment? {
        return if (position < fragmentList.size) fragmentList[position] else null
    }
    
    fun getBasicInfoFragment(): BasicInfoFragment? = getFragment(TAB_BASIC_INFO) as? BasicInfoFragment
    fun getPersonalFragment(): PersonalDetailsFragment? = getFragment(TAB_PERSONAL) as? PersonalDetailsFragment  
    fun getEmploymentFragment(): EmploymentDetailsFragment? = getFragment(TAB_EMPLOYMENT) as? EmploymentDetailsFragment
    fun getClassificationFragment(): StaffClassificationFragment? = getFragment(TAB_CLASSIFICATION) as? StaffClassificationFragment
} 