package getfun.com.br.getfun;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.SearchRecentSuggestions;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import getfun.com.br.getfun.adapters.EventAdapter;
import getfun.com.br.getfun.domain.Event;
import getfun.com.br.getfun.interfaces.RecyclerViewOnClickListenerHack;
import getfun.com.br.getfun.provider.SearchableProvider;


public class SearchableActivity extends AppCompatActivity implements RecyclerViewOnClickListenerHack {
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private List<Event> mList;
    private List<Event> mListAux;
    private EventAdapter adapter;
    private CoordinatorLayout clContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);

        mToolbar = (Toolbar) findViewById(R.id.tb_main);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(savedInstanceState != null){
            mList = savedInstanceState.getParcelableArrayList("mList");
            mListAux = savedInstanceState.getParcelableArrayList("mListAux");
        }
        else{
            mList = (new MainActivity()).getSetEventList(10, 0);
            mListAux = new ArrayList<>();
        }

        clContainer = (CoordinatorLayout) findViewById(R.id.cl_container);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_list);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager( this );
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);

        adapter = new EventAdapter(this, mListAux);
        adapter.setRecyclerViewOnClickListenerHack(this);
        mRecyclerView.setAdapter(adapter);

        hendleSearch( getIntent() );
    }


    @Override
    protected void onNewIntent(Intent intent) {
        setIntent( intent );
        hendleSearch( intent );
    }

    public void hendleSearch( Intent intent ){
        if( Intent.ACTION_SEARCH.equalsIgnoreCase( intent.getAction() ) ){
            String q = intent.getStringExtra( SearchManager.QUERY );

            mToolbar.setTitle(q);
            filterEvents( q );

            SearchRecentSuggestions searchRecentSuggestions = new SearchRecentSuggestions(this,
                    SearchableProvider.AUTHORITY,
                    SearchableProvider.MODE);
            searchRecentSuggestions.saveRecentQuery( q, null );
        }
    }


    public void filterEvents( String q ){
        mListAux.clear();

        for( int i = 0, tamI = mList.size(); i < tamI; i++ ){
            if( mList.get(i).getNome().toLowerCase().startsWith( q.toLowerCase() ) ){
                mListAux.add( mList.get(i) );
            }
        }
        for( int i = 0, tamI = mList.size(); i < tamI; i++ ){
            if( !mListAux.contains( mList.get(i) )
                    && mList.get(i).getTipo().toLowerCase().startsWith( q.toLowerCase() ) ){
                mListAux.add( mList.get(i) );
            }
        }

        mRecyclerView.setVisibility( mListAux.isEmpty() ? View.GONE : View.VISIBLE);
        if( mListAux.isEmpty() ){
            TextView tv = new TextView( this );
            tv.setText( "Nenhum evento encontrado." );
            tv.setTextColor( getResources().getColor( R.color.colorPrimarytext ) );
            tv.setId( 1 );
            tv.setLayoutParams( new FrameLayout.LayoutParams( FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT )  );
            tv.setGravity(Gravity.CENTER);

            clContainer.addView( tv );
        }
        else if( clContainer.findViewById(1) != null ) {
            clContainer.removeView( clContainer.findViewById(1) );
        }

        adapter.notifyDataSetChanged();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("mList", (ArrayList<Event>) mList);
        outState.putParcelableArrayList("mListAux", (ArrayList<Event>) mListAux);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_searchable_activity, menu);

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
        else if( id == R.id.action_delete ){
            SearchRecentSuggestions searchRecentSuggestions = new SearchRecentSuggestions(this,
                    SearchableProvider.AUTHORITY,
                    SearchableProvider.MODE);

            searchRecentSuggestions.clearHistory();

            Toast.makeText(this, "Históricos de Busca Removidos", Toast.LENGTH_SHORT).show();
        }

        return true;
    }



    // LISTENERS
    @Override
    public void onClickListener(View view, int position) {
        Intent intent = new Intent(this, EventActivity.class);
        intent.putExtra("event", mListAux.get(position));

        // TRANSITIONS
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ){

            View ivEvent = view.findViewById(R.id.iv_event);
            View tvNome = view.findViewById(R.id.tv_nome);
            View tvTipo = view.findViewById(R.id.tv_tipo);

            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                    Pair.create(ivEvent, "element1"),
                    Pair.create( tvNome, "element2" ),
                    Pair.create( tvTipo, "element3" ));

            startActivity(intent, options.toBundle() );
        }
        else{
            startActivity(intent);
        }
    }

    @Override
    public void onLongPressClickListener(View view, int position) {}
}
