package getfun.com.br.getfun;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.mikepenz.materialdrawer.Drawer;

import getfun.com.br.getfun.domain.Event;
import me.drakeet.materialdialog.MaterialDialog;


public class EventActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private Event event;
    private Drawer.Result navigationDrawerLeft;
    private MaterialDialog mMaterialDialog;
    private TextView tvDescription;
    private ViewGroup mRoot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TRANSITIONS
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ){


            TransitionInflater inflater = TransitionInflater.from( this );
            Transition transition = inflater.inflateTransition( R.transition.transitions );

            getWindow().setSharedElementEnterTransition(transition);

            Transition transition1 = getWindow().getSharedElementEnterTransition();
            transition1.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {

                }

                @Override
                public void onTransitionEnd(Transition transition) {
                    TransitionManager.beginDelayedTransition(mRoot, new Slide());
                    tvDescription.setVisibility( View.VISIBLE );
                }

                @Override
                public void onTransitionCancel(Transition transition) {

                }

                @Override
                public void onTransitionPause(Transition transition) {

                }

                @Override
                public void onTransitionResume(Transition transition) {

                }
            });
        }

        super.onCreate(savedInstanceState);

        Fresco.initialize(this);
        setContentView(R.layout.activity_event);

        if(savedInstanceState != null){
            event = savedInstanceState.getParcelable("event");
        }
        else {
            if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().getParcelable("event") != null) {
                event = getIntent().getExtras().getParcelable("event");
            } else {
                Toast.makeText(this, "Falha!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mCollapsingToolbarLayout.setTitle(event.getNome() );

        mToolbar = (Toolbar) findViewById(R.id.tb_main);
        mToolbar.setTitle(event.getNome());
        setSupportActionBar(mToolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);

        mRoot = (ViewGroup) findViewById(R.id.ll_tv_description);
        tvDescription = (TextView) findViewById(R.id.tv_description);
        ImageView ivEvent = (ImageView) findViewById(R.id.iv_event);
        TextView tvNome = (TextView) findViewById(R.id.tv_nome);
        TextView tvTipo = (TextView) findViewById(R.id.tv_tipo);
        Button btPhone = (Button) findViewById(R.id.bt_phone);
        Button btWebsite = (Button) findViewById(R.id.bt_website);
        Button btMaps = (Button) findViewById(R.id.bt_maps);

        btPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMaterialDialog = new MaterialDialog(new ContextThemeWrapper(EventActivity.this, R.style.MyAlertDialog))
                        .setTitle("Telefone do Evento")
                        .setMessage(event.getTel())
                        .setPositiveButton("Ligar", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Chamada();
                            }
                        })
                        .setNegativeButton("Voltar", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mMaterialDialog.dismiss();
                            }
                        });
                mMaterialDialog.show();
            }
        });

        btWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMaterialDialog = new MaterialDialog(new ContextThemeWrapper(EventActivity.this, R.style.MyAlertDialog))
                        .setTitle("Website do Evento")
                        .setMessage(event.getWebsite())
                        .setPositiveButton("Acessar", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                irSite();
                            }
                        })
                        .setNegativeButton("Voltar", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mMaterialDialog.dismiss();
                            }
                        });
                mMaterialDialog.show();
            }
        });

        btMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMaterialDialog = new MaterialDialog(new ContextThemeWrapper(EventActivity.this, R.style.MyAlertDialog))
                        .setTitle("Mapa do Evento")
                        .setMessage("Iremos lhe mostrar a melhor rota para: " + event.getNome())
                        .setPositiveButton("Ok", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                irMaps();
                            }
                        })
                        .setNegativeButton("Voltar", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mMaterialDialog.dismiss();
                            }
                        });
                mMaterialDialog.show();
            }
        });




        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize( size );
        int w;
        try{
            w = size.x;
        }
        catch( Exception e ){
            w = display.getWidth();
        }




        ivEvent.setImageResource(event.getPhoto());
        tvNome.setText(event.getNome());
        tvTipo.setText(event.getTipo());
        tvDescription.setText(event.getDescription());
        tvDescription.setVisibility(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP || savedInstanceState != null ? View.VISIBLE : View.INVISIBLE);

        navigationDrawerLeft = new Drawer()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withActionBarDrawerToggle(false)
                .withCloseOnClick(true)
                .withActionBarDrawerToggleAnimated(false)
                .withActionBarDrawerToggle(new ActionBarDrawerToggle(this, new DrawerLayout(this), R.string.drawer_open, R.string.drawer_close){
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        super.onDrawerSlide(drawerView, slideOffset);
                        navigationDrawerLeft.closeDrawer();
                        finish();
                    }
                })
                .build();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_event_activity, menu);

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
        if(id == android.R.id.home){
            finish();
        }
        return true;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("event", event);
    }


    @Override
    public void onBackPressed() {
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ){
            TransitionManager.beginDelayedTransition(mRoot, new Slide());
            tvDescription.setVisibility( View.INVISIBLE );
        }

        super.onBackPressed();
    }

    public void Chamada(){

        Uri uri = Uri.parse("tel:" +  event.getTel().trim());
        Intent ch = new Intent(Intent.ACTION_DIAL,uri);

        startActivity(ch);

    }


    public void irSite() {

        //  Uri uri = Uri.parse("tel:" +  event.getWebsite().trim());
        Intent it = new Intent(Intent.ACTION_VIEW);
        it.setData(Uri.parse(event.getWebsite().trim()));

        startActivity(it);
    }


    public void irMaps() {

        Uri gmmIntentUri = Uri.parse(event.getMaps().trim());
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);

    }

}

