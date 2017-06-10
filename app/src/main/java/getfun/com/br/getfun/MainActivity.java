package getfun.com.br.getfun;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeBounds;
import android.transition.ChangeTransform;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Toast;


import com.facebook.AccessToken;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.login.LoginManager;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.util.ArrayList;
import java.util.List;

import getfun.com.br.getfun.adapters.TabsAdapter;
import getfun.com.br.getfun.domain.Event;
import getfun.com.br.getfun.domain.Person;
import getfun.com.br.getfun.extras.SlidingTabLayout;


public class MainActivity extends AppCompatActivity {
    private static String TAG = "LOG";

    private Toolbar mToolbar;
    private Drawer.Result navigationDrawerLeft;
    private AccountHeader.Result headerNavigationLeft;
    //private FloatingActionMenu fab;
    private int mItemDrawerSelected;
    private int mProfileDrawerSelected;

    private List<PrimaryDrawerItem> listCatefories;
    private List<Person> listProfile;
    private List<Event> listEvents;

    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TRANSITIONS
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ){


            TransitionInflater inflater = TransitionInflater.from( this );
            Transition transition = inflater.inflateTransition( R.transition.transitions );

            getWindow().setSharedElementExitTransition( transition );

        }


        super.onCreate(savedInstanceState);

        Fresco.initialize(this);
        setContentView(R.layout.activity_main);

          if (AccessToken.getCurrentAccessToken() == null) {
             goLoginScreen();
          }
        if(savedInstanceState != null){
            mItemDrawerSelected = savedInstanceState.getInt("mItemDrawerSelected", 0);
            mProfileDrawerSelected = savedInstanceState.getInt("mProfileDrawerSelected", 0);
            listEvents = savedInstanceState.getParcelableArrayList("listEvents");
        }
        else{
            listEvents = getSetEventList(50);
        }

        // TOOLBAR
        mToolbar = (Toolbar) findViewById(R.id.tb_main);
        mToolbar.setTitle("GetFun");
        //mToolbar.setSubtitle("just a subtitle");
        //mToolbar.setLogo(R.drawable.ic_launcher);
        setSupportActionBar(mToolbar);


        // TABS
        mViewPager = (ViewPager) findViewById(R.id.vp_tabs);
        mViewPager.setAdapter(new TabsAdapter(getSupportFragmentManager(), this));

        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.stl_tabs);
        //mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.colorFAB));
        mSlidingTabLayout.setCustomTabView(R.layout.tab_view, R.id.tv_tab);
        mSlidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                navigationDrawerLeft.setSelection(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mSlidingTabLayout.setViewPager(mViewPager);
        //mSlidingTabLayout.setHorizontalFadingEdgeEnabled(true);
        //mSlidingTabLayout.setHorizontalScrollBarEnabled(true);


        // NAVIGATION DRAWER
        // HEADER
        headerNavigationLeft = new AccountHeader()
                .withActivity(this)
                .withCompactStyle(false)
                .withSavedInstance(savedInstanceState)
                .withThreeSmallProfileImages(true)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile iProfile, boolean b) {
                        Person aux = getPersonByEmail(listProfile, (ProfileDrawerItem) iProfile);
                        mProfileDrawerSelected = getPersonPositionByEmail(listProfile, (ProfileDrawerItem) iProfile);
                        headerNavigationLeft.setBackgroundRes(aux.getBackground());
                        return true;
                    }
                })
                .build();

        listProfile = getSetProfileList();
        if(listProfile != null && listProfile.size() > 0){
            if(mProfileDrawerSelected != 0){
                Person aux = listProfile.get(mProfileDrawerSelected);
                listProfile.set(mProfileDrawerSelected, listProfile.get(0));
                listProfile.set(0, aux);
            }
            for(int i = 0; i < listProfile.size(); i++){
                headerNavigationLeft.addProfile(listProfile.get(i).getProfile(), i);
            }
            headerNavigationLeft.setBackgroundRes(listProfile.get(0).getBackground());
        }


        // BODY
        navigationDrawerLeft = new Drawer()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withDisplayBelowToolbar(false)
                .withActionBarDrawerToggleAnimated(true)
                .withDrawerGravity(Gravity.START)
                .withSavedInstance(savedInstanceState)
                .withActionBarDrawerToggle(true)
                .withAccountHeader(headerNavigationLeft)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l, IDrawerItem iDrawerItem) {
                        mViewPager.setCurrentItem( i );


                    }
                })
                .withOnDrawerItemLongClickListener(new Drawer.OnDrawerItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l, IDrawerItem iDrawerItem) {
                        Toast.makeText(MainActivity.this, "Escolha um evento!: " + i, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                })
                .build();

        listCatefories = getSetCategoryList();
        if(listCatefories != null && listCatefories.size() > 0){
            for( int i = 0; i < listCatefories.size(); i++ ){
                navigationDrawerLeft.addItem( listCatefories.get(i) );
            }
            navigationDrawerLeft.setSelection(mItemDrawerSelected);
        }


        // FLOATING ACTION BUTTON
        //fab = (FloatingActionMenu) findViewById(R.id.fab);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView;
        MenuItem item = menu.findItem(R.id.action_searchable_activity);

        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ){
            searchView = (SearchView) item.getActionView();
        }
        else{
            searchView = (SearchView) MenuItemCompat.getActionView( item );
        }

        searchView.setSearchableInfo( searchManager.getSearchableInfo( getComponentName() ) );
        searchView.setQueryHint( getResources().getString(R.string.search_hint) );

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_config_activity){
            startActivity(new Intent(this, ScrollingActivity.class));
        }
        else if(id == R.id.action_logoff_facebook){
            logout();
        }
        return super.onOptionsItemSelected(item);
    }



    // CATEGORIES
    private List<PrimaryDrawerItem> getSetCategoryList(){
        String[] names = new String[]{"Eventos", "Recomendados", "Salvos", "Check-in"};
        int[] icons = new int[]{R.drawable.car_4, R.drawable.car_4, R.drawable.car_4, R.drawable.car_4};
        int[] iconsSelected = new int[]{R.drawable.car_4, R.drawable.car_4, R.drawable.car_4, R.drawable.car_4};
        List<PrimaryDrawerItem> list = new ArrayList<>();

        for(int i = 0; i < names.length; i++){
            PrimaryDrawerItem aux = new PrimaryDrawerItem();
            aux.setName( names[i] );
            aux.setIcon(getResources().getDrawable(icons[i]));
            aux.setTextColor(getResources().getColor(R.color.colorPrimarytext));
            aux.setSelectedIcon(getResources().getDrawable(iconsSelected[i]));
            aux.setSelectedTextColor(getResources().getColor(R.color.colorPrimary));

            list.add( aux );
        }
        return(list);
    }



    // PERSON
    private Person getPersonByEmail( List<Person> list, ProfileDrawerItem p ){
        Person aux = null;
        for(int i = 0; i < list.size(); i++){
            if( list.get(i).getProfile().getEmail().equalsIgnoreCase( p.getEmail() ) ){
                aux = list.get(i);
                break;
            }
        }
        return( aux );
    }

    private List<Person> getSetProfileList(){
        String[] names = new String[]{"Rodrigo Néris"};
        String[] emails = new String[]{"rodrigo.neris@live.com"};
        int[] photos = new int[]{R.drawable.rodrigo};
        int[] background = new int[]{R.drawable.background};
        List<Person> list = new ArrayList<>();

        for(int i = 0; i < names.length; i++){
            ProfileDrawerItem aux = new ProfileDrawerItem();
            aux.setName(names[i]);
            aux.setEmail(emails[i]);
            aux.setIcon(getResources().getDrawable(photos[i]));

            Person p = new Person();
            p.setProfile(aux);
            p.setBackground(background[i]);

            list.add( p );
        }
        return(list);
    }

    private int getPersonPositionByEmail( List<Person> list, ProfileDrawerItem p ){
        for(int i = 0; i < list.size(); i++){
            if( list.get(i).getProfile().getEmail().equalsIgnoreCase( p.getEmail() ) ){
                return(i);
            }
        }
        return( -1 );
    }



    // Cadastrar Eventos
    public List<Event> getSetEventList(int qtd){
        return(getSetEventList(qtd, 0));
    }

    public List<Event> getSetEventList(int qtd, int category) {
        String[] nomes = new String[]{"King Festival", "Red & Blue", "Via Show"};
        String[] tipos = new String[]{"Festival de Música Eletrônica", "Casa de Show", "Casa Noturna"};
        int[] categories = new int[]{1, 2, 3};
        int[] photos = new int[]{R.drawable.kingshow, R.drawable.blueshow, R.drawable.viashow};
        String[] description = {"Horário: 22h   \n\nLocal: Centro de Convenções\n\nAtrações: Martin Garrix, R3HAB, Dimitri Vegas", "Horário: 21h   \n\nLocal: Boa Viagem - Centro CEP:54489-290\n\nAtrações: Geraldo Show", "Horário: 20h   \n\nLocal: Pina - Recife\n\nAtrações: DJ Bolado"};
        String[] tel = {"997318492", "987654578", "352299954"};
        String[] website = {"http://www.kingfestival.com.br", "http://www.redblueeventos.com.br", "http://www.viashow.com.br"};
        String[] maps = {"google.navigation:q=Centro+de+Convenções,+Olinda&avoid=tf", "google.navigation:q=Centro+de+Convenções,+Olinda&avoid=tf", "google.navigation:q=Centro+de+Convenções,+Olinda&avoid=tf"};
        List<Event> listAux = new ArrayList<>();

        for (int i = 0; i < qtd; i++) {
            Event e = new Event(nomes[i % nomes.length], tipos[i % tipos.length], photos[i % nomes.length]);
            e.setCategory(categories[i % tipos.length]);
            e.setDescription(description[i % description.length]);
            e.setTel(tel[i % tipos.length]);
            e.setWebsite(website[i % tipos.length]);
            e.setMaps(maps[i % tipos.length]);

            if (category != 0 && e.getCategory() != category) {
                continue;
            }

            listAux.add(e);
        }
        return(listAux);
    }



    public List<Event> getEventsByCategory(int category){
        List<Event> listAux = new ArrayList<>();
        for(int i = 0; i < listEvents.size() ; i++){
            if(category != 0 && listEvents.get(i).getCategory() != category){
                continue;
            }

            listAux.add(listEvents.get(i));
        }
        return(listAux);
    }

    public List<Event> getListEvents(){
        return(listEvents);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("mItemDrawerSelected", mItemDrawerSelected);
        outState.putInt("mProfileDrawerSelected", mProfileDrawerSelected);
        outState.putParcelableArrayList("listEvents", (ArrayList<Event>) listEvents);
        outState = navigationDrawerLeft.saveInstanceState(outState);
        outState = headerNavigationLeft.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if(navigationDrawerLeft.isDrawerOpen()){
            navigationDrawerLeft.closeDrawer();
        }
        /*else if(fab.isOpened()){
            fab.close(true);
        }*/
        else{
            super.onBackPressed();
        }
    }

    //IR TELA DE LOGIN
    private void goLoginScreen(){
        Intent intent = new Intent(this, FacebookLogin.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }



    //DESLOGAR DO APP
    public void logout(){
        LoginManager.getInstance().logOut();
        goLoginScreen();
    }

}

