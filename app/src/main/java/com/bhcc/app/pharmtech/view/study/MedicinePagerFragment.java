package com.bhcc.app.pharmtech.view.study;

import com.bhcc.app.pharmtech.R;
import com.bhcc.app.pharmtech.data.MedicineLab;
import com.bhcc.app.pharmtech.data.model.Medicine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class MedicinePagerFragment extends Fragment {

    private static final String EXTRA_MEDICINE_GENERIC_NAME =
            "extra: medicine generic name";

    private ViewPager mViewPager;

    private View rootView;

    private List<Medicine> mMedicines;

    private String medicineGenericName;

    public static MedicinePagerFragment newInstance(Medicine medicine) {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_MEDICINE_GENERIC_NAME, medicine.getGenericName());
        MedicinePagerFragment medicinePagerFragment = new MedicinePagerFragment();
        medicinePagerFragment.setArguments(bundle);
        return medicinePagerFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_medicine_pager, container, false);
        setUpViewPager();
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        medicineGenericName = getArguments().getString(EXTRA_MEDICINE_GENERIC_NAME);
    }

    void setUpViewPager() {

        mViewPager = (ViewPager) rootView.findViewById(R.id.medicine_pager_view_pager);
        mMedicines = MedicineLab.get(getContext()).getMedicines();
        mViewPager.setAdapter(
                new FragmentStatePagerAdapter(getActivity().getSupportFragmentManager()) {
                    @Override
                    public Fragment getItem(int position) {
                        Medicine Medicine = mMedicines.get(position);
                        return MedicineDetailFragment.newInstance(Medicine.getGenericName());
                    }

                    @Override
                    public int getCount() {
                        return mMedicines.size();
                    }

                    @Override
                    public int getItemPosition(Object object) {
                        return POSITION_NONE;
                    }
                });

        for (int i = 0; i < mMedicines.size(); i++) {
            if (mMedicines.get(i).getGenericName().equals(medicineGenericName)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }

}
