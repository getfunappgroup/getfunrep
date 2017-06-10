package getfun.com.br.getfun.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import getfun.com.br.getfun.R;
import getfun.com.br.getfun.fragments.EventFragment;
import getfun.com.br.getfun.fragments.RecomendEventFragment;
import getfun.com.br.getfun.fragments.CheckinEventFragment;
import getfun.com.br.getfun.fragments.SavedEventFragment;


public class TabsAdapter extends FragmentPagerAdapter {
    private Context mContext;
    private String[] titles = {"Eventos", "Recomendados", "Salvos", "Check-in"};
    private int[] icons = new int[]{R.drawable.car_1, R.drawable.car_1, R.drawable.car_2};
    private int heightIcon;


    public TabsAdapter(FragmentManager fm, Context c) {
        super(fm);

        mContext = c;
        double scale = c.getResources().getDisplayMetrics().density;
        heightIcon = (int)( 24 * scale + 0.5f );
    }

    @Override
    public Fragment getItem(int position) {
        Fragment frag = null;

        if(position == 0){ // Eventos
            frag = new EventFragment();
        }
        else if(position == 1){ // Recomendados
            frag = new RecomendEventFragment();
        }
        else if(position == 2){ // Salvos
            frag = new SavedEventFragment();
        }
        else if(position == 3){ // Check-in
            frag = new CheckinEventFragment();
        }


        Bundle b = new Bundle();
        b.putInt("position", position);

        frag.setArguments(b);

        return frag;
    }

    @Override
    public int getCount() {
        return titles.length;
    }


    @Override
    public CharSequence getPageTitle(int position) {

        return ( titles[position] );
    }
}
