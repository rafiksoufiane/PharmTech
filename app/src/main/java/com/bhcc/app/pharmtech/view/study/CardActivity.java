package com.bhcc.app.pharmtech.view.study;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.bhcc.app.pharmtech.R;
import com.bhcc.app.pharmtech.data.MedicineLab;
import com.bhcc.app.pharmtech.data.model.Medicine;

import java.util.List;


public class CardActivity extends Activity {

    // Extra for an intent
    private static final String EXTRA_MEDICINE_GENERIC_NAME = "extra: medicine generic name";

    // Lists
    private List<Medicine> mMedicines;


    /**
     * to create a new activity with an intent (a medicine name)
     * @param packageContext
     * @param genericName medicine name
     * @return CardActivity
     */
    public static Intent newIntent(Context packageContext, String genericName) {
        Intent intent = new Intent(packageContext, CardActivity.class);
        intent.putExtra(EXTRA_MEDICINE_GENERIC_NAME, genericName);
        return intent;
    }

    /**
     * Set up the ViewPager
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        String medicineGenericName = getIntent().getStringExtra(EXTRA_MEDICINE_GENERIC_NAME);
        mMedicines = MedicineLab.get(this).getMedicines();

        CardPagerAdapter adapter = new CardPagerAdapter(getFragmentManager());
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(adapter);

        for (int i = 0; i < mMedicines.size(); i++) {
            if (mMedicines.get(i).getGenericName().equals(medicineGenericName)) {
                viewPager.setCurrentItem(i);
                break;
            }
        }
    }

    /**
     * Pager Adapter
     */
    public class CardPagerAdapter extends android.support.v13.app.FragmentPagerAdapter {

        /**
         * Constructor
         * @param fm
         */
        public CardPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * To get a fragment from 'position'
         * @param position
         * @return Fragment
         */
        @Override
        public Fragment getItem(int position) {
            Medicine medicine = mMedicines.get(position);

            CardContainerFragment fragment = CardContainerFragment.newInstance(medicine);

            return fragment;
        }

        /**
         * To get size of the list
         * @return size of the list
         */
        @Override
        public int getCount() {
            return mMedicines.size();
        }
    }
}
